library(elastic)
connect(es_host = "elastic-bguq8j.hackathon.insee.eu", es_port = 80)
source("./R/utils_requete.R")

# Exemple
req <- c("RONAN ETS", "CARMINA BURANA")
loc <- c("AGEN", "PARIS")
res <- purrr::map2_dfr(req, loc, first_siret_desc)

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

