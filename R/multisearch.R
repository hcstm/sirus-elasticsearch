library(glue)
library(elastic)
library(purrr)
library(httr)

read_template <- function(file) {
  lines <- readLines(file)
  lines <- stringi::stri_replace_all(lines, "", regex = "\\h")
  paste0(lines, collapse = "")
}

find_var_from_template <- function(template) {
  unique(stringi::stri_match_all(template, regex = "(?<=<<).+?(?=>>)")[[1]])
}


multi_search <- function(body) {
  url <- paste0(elastic:::make_url(elastic:::es_get_auth()), "/_msearch")
  tt <- httr::POST(url, 
                   elastic:::make_up(), 
                   elastic:::es_env$headers, 
                   httr::content_type("application/x-ndjson"), 
                   body = body)
  elastic:::geterror(tt)
  res <- elastic:::cont_utf8(tt)
  jsonlite::fromJSON(res, FALSE)
}

ms_factory <- function(index, template_file) {
  template <- read_template(template_file)
  args <- find_var_from_template(template)
  l <- vector("list", length(args))
  names(l) <- args
  make_unit_body <- function() {
    paste0(sprintf('{"index" : "%s"}\n', index), 
           glue::glue(template, .open = "<<", .close = ">>")
    )
  }
  formals(make_unit_body) <- l
  make_body <- function() {
    liste_arguments_valeur <- mget(args)
    lapply(liste_arguments_valeur, function(x) if(is.null(x)) stop("Il manque un argument."))
    paste0(paste0(pmap_chr(liste_arguments_valeur, make_unit_body), collapse = "\n"), "\n")
  }
  formals(make_body) <- l

  list(make_body = make_body)
}

post_traitement_res <- function(res) {
  get_max_score_doc <- function(response) {
    total_hits <- response$hits$total
    if (total_hits == 0) {
      return(NULL)
    } else {
      response$hits$hits[[1]]$`_source`
    }
  }
  
  has_result <- function(response) {
    total_hits <- response$hits$total
    if (total_hits == 0) {
      return(FALSE)
    } else {
      TRUE
    }
  }
  
  retour_ok <- vapply(res$responses, has_result, FUN.VALUE = logical(1))
  
  max_score_ou_null <- lapply(res$responses, get_max_score_doc)
  
  matrice_des_retours <- do.call(rbind, max_score_ou_null[retour_ok])
  
  d <- dim(matrice_des_retours)
  
  matrice_vide <- vector('character', (length(retour_ok)-d[1])*d[2])
  
  dim(matrice_vide) <- c(length(retour_ok)-d[1], d[2])
  
  colnames(matrice_vide) <- colnames(matrice_des_retours)
  
  indices_retours_valides <- c(1:length(retour_ok))[retour_ok]
  
  indices_retours_non_valides <- c(1:length(retour_ok))[!retour_ok]
  
  matrice_retours_valides <- data.frame(indice = indices_retours_valides, matrice_des_retours, stringsAsFactors = F)
  
  matrice_retours_non_valides <- data.frame(indice = indices_retours_non_valides, matrice_vide, stringsAsFactors = F)
  
  matrice_finale <- rbind(matrice_retours_valides, matrice_retours_non_valides)
  
  matrice_finale
}
