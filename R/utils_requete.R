first_siret_desc <- function(requete, loc,
                             liste_char = c("sirus_id", "nic", "apet", "cj", "tr_eff_etp", "denom", "adr_et_l4", "adr_et_l5", "adr_depcom"), 
                             taille_requete = 5) {
  if (requete %in% c("", " ")) {
    desc <- ""
  } else {
    desc <- paste0("description:", requete)
  }
  
  if (loc %in% c("", " ")) {
    local <- ""
  } else {
    local <- paste0("localisation:", loc)
  }
  
  if (desc == "") {
    q <- local
  } else {
    q <- paste0(desc, if (local != "") "&", local)
  }
  
  if (q == "") {
    rang <- 0
    score <- NA
    df <- sapply(liste_char, function(x) "", USE.NAMES = TRUE)
    return(
      data.frame(
        requete = requete,
        loc = loc,
        rang = rang,
        score = score,
        as.list(df),
        stringsAsFactors = FALSE
      )
    )
  }
  
  #q <- paste0("description:", requete, "& localisation:", loc)
  res <- Search(index = 'sirus_basic_mapping', 
                type = 'doc', 
                q = q, 
                source = paste0(liste_char, collapse = ","),
                size = taille_requete)
  
  retour_var_char <- function(x, nom_variable) {
    retour <- res$hits$hits[[x]]$`_source`[[nom_variable]]
    if (is.null(retour))
      retour <- NA_character_
    retour
  }
  
  retour_var <- function(nom_variable) {
    vapply(1:taille_requete, function(x) retour_var_char(x, nom_variable), FUN.VALUE = character(1))
  }
  
  df <- sapply(liste_char, retour_var, USE.NAMES = TRUE)
  
  score <- vapply(1:taille_requete, function(x) res$hits$hits[[x]]$`_score`, FUN.VALUE = numeric(1))

  data.frame(
    requete = requete,
    loc = loc,
    rang = 1:taille_requete,
    score = score,
    df,
    stringsAsFactors = FALSE
  )
}
