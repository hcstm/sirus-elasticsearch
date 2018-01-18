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

# connect()

# Quelques interrogations pour voir les index présents sur le serveur ES
# cat_()
# cat_aliases(index = 'sirus_basic_mapping')
# cat_allocation()
# cat_count(index = 'sirus_basic_mapping')
# cat_health()
# cat_pending_tasks()
# cat_recovery(index = 'sirus_basic_mapping')
# 
# Search(index = 'sirus_basic_mapping')
# Search(index = 'sirus_basic_mapping', fields = 'description')
# 
# Search(index = 'sirus_basic_mapping', body = '{
#   "description": "retraite"
# }')
# 
# 
# Search(index = 'sirus_basic_mapping')$hits$total
# #8526230
# 
# Search(index = 'sirus_basic_mapping', type = 'doc')$hits$total
# 
# Search(index = 'sirus_basic_mapping', type = 'doc', q= 'description:MAISON DE RETRAITE 59375 MARCHIENNES')$hits$total
# 
# t <- Search(index = 'sirus_basic_mapping', type = 'doc', q= 'description:MAISON DE RETRAITE 59375 MARCHIENNES', size =10, sort = '_score')
# 
# rostan <- Search(index = 'sirus_basic_mapping', type = 'doc', q= 'description:ROSTAN GAP 05061')
# rostan$hits$hits[[1]]$`_source`$sirus_id
# 
# 
# siret_desc("ROSTAN GAP 05061")

