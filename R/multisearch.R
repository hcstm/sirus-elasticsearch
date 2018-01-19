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
  stringi::stri_match_all(template, regex = "(?<=<<).+?(?=>>)")[[1]]
}


multi_search <- function(body, asdf = TRUE) {
  url <- paste0(elastic:::make_url(elastic:::es_get_auth()), "/_msearch")
  tt <- httr::POST(url, 
                   elastic:::make_up(), 
                   elastic:::es_env$headers, 
                   httr::content_type("application/x-ndjson"), 
                   body = body)
  elastic:::geterror(tt)
  res <- elastic:::cont_utf8(tt)
  jsonlite::fromJSON(res, simplifyVector = asdf)
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

