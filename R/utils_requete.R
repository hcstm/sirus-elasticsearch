first_siret_desc <- function(desc, taille_requete = 5) {
  q <- paste0("description:", desc)
  res <- Search(index = 'sirus_basic_mapping', 
                type = 'doc', 
                q = q, 
                source = "sirus_id,nic,apet,cj,tr_eff_etp", 
                size = taille_requete)
  
  score <- vapply(1:taille_requete, function(x) res$hits$hits[[x]]$`_score`, FUN.VALUE = numeric(1))
  sirus_id <- vapply(1:taille_requete, function(x) res$hits$hits[[x]]$`_source`$sirus_id, FUN.VALUE = character(1))
  nic <- vapply(1:taille_requete, function(x) res$hits$hits[[x]]$`_source`$nic, FUN.VALUE = character(1))
  siret <- paste0(sirus_id, nic)
  apet <- vapply(1:taille_requete, function(x) res$hits$hits[[x]]$`_source`$apet, FUN.VALUE = character(1))
  cj <- vapply(1:taille_requete, function(x) res$hits$hits[[x]]$`_source`$cj, FUN.VALUE = character(1))
  tr_eff_etp <- vapply(1:taille_requete, function(x) res$hits$hits[[x]]$`_source`$tr_eff_etp, FUN.VALUE = character(1))
  #score <- res$hits$hits[[1]]$`_score`
  #sirus_id <- res$hits$hits[[1]]$`_source`$sirus_id
  #nic <- res$hits$hits[[1]]$`_source`$nic
  data.frame(
    siret = siret,
    score = score,
    apet = apet,
    cj = cj,
    tr_eff_etp = tr_eff_etp,
    stringsAsFactors = FALSE
    )
}
