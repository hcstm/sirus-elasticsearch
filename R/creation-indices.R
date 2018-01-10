library(elastic)

# Connection à ES
connect()

# Petite vérification
index_exists('shakespeare')

# Création de sirus_naive
index_create('sirus_naive')

# Création de sirus_basic_mapping
## On ne va pas recopier brutalement le body de la requête PUT, mais on va le lire dans le fichier d'origine :
sirus_basic_mapping_file <- readLines("./creation-indices/1_sirus_basic_mapping.txt")
debut <- grep("^PUT /sirus_naive", sirus_basic_mapping_file) + 1
fins <- grep("^}", sirus_basic_mapping_file)
fin <- min(fins[fins > debut])
body <- paste0(sirus_basic_mapping_file[debut:fin], collapse = "\n")
## Maintenant qu'on a récupéré le body, on crée l'index (demander à Philippe pour le nom de l'index)
index_create('sirus_basic_mapping', body = body)

# Modification des settings de sirus_basic_mapping :
debut <- grep("^PUT /sirus_basic_mapping/_settings", sirus_basic_mapping_file) + 1
fin <- min(fins[fins > debut])
body <- paste0(sirus_basic_mapping_file[debut:fin], collapse = "")
# J'ai un peu galéré car la v6 d'ES attend un header avec le Content-Type: application/json
index_settings_update('sirus_basic_mapping', body = body, httr::content_type_json())

# Le package elastic n'a pas de fonction pour modifier les settings du cluster, on y va à pied (on peut mettre ça en fonction, c'est facile):
debut <- grep("^PUT /_cluster/settings", sirus_basic_mapping_file) + 1
fin <- min(fins[fins > debut])
body <- paste0(sirus_basic_mapping_file[debut:fin], collapse = "")
tt <- httr::PUT("http://localhost:9200/_cluster/settings", body = body, httr::content_type_json())
elastic:::geterror(tt)
res <- elastic:::cont_utf8(tt)
jsonlite::fromJSON(res)

