library(elastic)
connect(es_host = "elastic-bguq8j.hackathon.insee.eu", es_port = 80)
source("./R/utils_requete.R")

# Exemple
#req <- c("RONAN ETS", "CARMINA BURANA")
#loc <- c("AGEN", "PARIS")
#res <- purrr::map2_dfr(req, loc, first_siret_desc)
req <- str_c(str_replace_na(table_rp$RS_X,replacement=""),
      str_replace_na(table_rp$ACTET_X,replacement=""),sep = " ")
req <- str_replace_all(req,"'"," ")
req <- str_replace_all(req,"/"," ")
req <- str_replace_all(req,fixed(".")," ")

loc  <- str_c(str_replace_na(table_rp$commune,replacement=""),
        str_replace_na(table_rp$NOMVOI_X,replacement=""),sep = " ")
loc <- str_replace_all(loc,"'"," ")
loc <- str_replace_all(loc,"/"," ")
loc <- str_replace_all(loc,"-"," ")
loc <- str_replace_all(loc,fixed(".")," ")

#req = req[1:200]
#loc = loc[1:200]
#res2 <- purrr::map2_dfr(req, loc, first_siret_desc,taille_requete=1)
result=c()
for(i in 1:2000){
  result = rbind(result,first_siret_desc(req[i],loc[i]))
}

req <- req[i]
loc  <- loc[i]
q <- paste0("description:", req,"& localisation:",loc)
res <- Search(index = 'sirus_basic_mapping', type = 'doc', q = q)
res$hits$hits[[4]]$`_source`$sirus_id


result_table = lapply(result,function(x)x[1:2000])
write.csv(table_rp,file = "/Volumes/NO NAME/First_result/table_rp.csv")
write.csv2( result,file = "/Volumes/NO NAME/First_result/result.csv")



# Requête multi search ----------------------------------------------------
# Pour construire une requête multi search, il va nous falloir :
# - un template de requête (ex: requete_simple.template.txt)
# - le nom de l'index
# - des vecteurs de chaînes de caractères pour alimenter le template : 
#   sans valeur NULL (c'est très important). Je pense que les chaînes de caractères vides "" doivent passer

# Exemple :
source('./R/multisearch.R')
#connect()
#connect(es_host = "elastic-bguq8j.hackathon.insee.eu", es_port = 80)
connect(es_host = "elastic-pl.hackathon.insee.eu", es_port = 80)

# On déclare l'index sur lequel on veut travailler et on indique le chemin du template
requete_simple <- ms_factory(index = 'sirus_basic_mapping', 
                             template_file = './R/requete_simple.template.txt')

# On construite le body :
# La fonction make_body est automatiquement alimentée par les variables du template
# Cela devrait éviter des oublis de variable
body <- requete_simple$make_body(description = c("MAISON DE RETRAITE", "MAIRIE"),
                                 localisation = c("MARCHIENNES", "MARSEILLE"))

# On récupère le résultat de la requête
res <- multi_search(body)

# L'objet res est complexe
# res$responses a autant d'enregistrements que de requêtes (2 ici)
res$responses
# J'ai l'impression que c'est à travers les valeurs de took et status qu'on peut 
# savoir si une requête a fonctionné (took et status sont de même longueur que le nb de requetes) :
res$responses$took
res$responses$status
# Par contre, je pense qu'il n'est pas garanti que plus bas, on ait des réponses 
# (au cas où une requête n'aurait pas fonctionné)
# Il serait peut être plus sûr de ne travailler que sur les status 200
# Plus bas, on a hits avec max_score et total (nb de docs trouvés)
res$responses$hits$max_score
res$responses$hits$total
# On peut ensuite descendre plus bas pour voir les retours :
# L'objet :
res$responses$hits$hits # est une liste de data.frame (un df par requête)
# Si on prend un de ces data.frame :
res$responses$hits$hits[[1]] # on constate qu'on a 10 lignes de retour et tous les champs sirus
# Les enregistrements sont triés par la variable _score


# Index : basic_mapping ---------------------------------------------------
# Description : RS_X
# Localisation : CLT_X
# Template : requete_simple
rp2014 <- data.table::fread(
  file.path("./rp_final_2014.csv"), 
  colClasses = rep("character", 52),
  encoding = 'Latin-1')

requete_simple <- ms_factory(index = 'sirus_basic_mapping', 
                             template_file = './R/requete_simple.template.txt')

body <- requete_simple$make_body(description = rp2014$RS_X[1:1000],
                                 localisation = rp2014$CLT_X[1:1000])

# On a un problème de temps d'exécution, même en local sur le rp 2014 (je pense qu'ES ne doit pas faire plus de 10 requêtes/seconde)
res <- multi_search(body)

st <- res$responses$status



# Index : basic_mapping ---------------------------------------------------
# Boost communes
source('./R/multisearch.R')

connect(es_host = "elastic-bguq8j.hackathon.insee.eu", es_port = 80)
requete_simple_restreinte <- ms_factory(index = 'sirus_basic_mapping', 
                             template_file = './R/requete_simple_restreinte.template.txt')

body <- requete_simple_restreinte$make_body(description = stringr::str_c(rp2014$ACTET_X, rp2014$RS_X),
                                            sir_adr_et_com_lib = "",
                                            localisation = "")