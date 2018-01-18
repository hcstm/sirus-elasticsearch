library(elastic)
# TEST
connect()

cat_()
cat_aliases(index = 'sirus_basic_mapping')
cat_allocation()
cat_count(index = 'sirus_basic_mapping')
cat_health()
cat_pending_tasks()
cat_recovery(index = 'sirus_basic_mapping')

Search(index = 'sirus_basic_mapping')
Search(index = 'sirus_basic_mapping', fields = 'description')

Search(index = 'sirus_basic_mapping', body = '{
  "description": "retraite"
}')


Search(index = 'sirus_basic_mapping')$hits$total
#8526230

Search(index = 'sirus_basic_mapping', type = 'doc')$hits$total

Search(index = 'sirus_basic_mapping', type = 'doc', q= 'description:MAISON DE RETRAITE 59375 MARCHIENNES')$hits$total

t <- Search(index = 'sirus_basic_mapping', type = 'doc', q= 'description:MAISON DE RETRAITE 59375 MARCHIENNES', size =2, sort = '_score')
