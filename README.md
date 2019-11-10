# StackExchangeApi.Users
See in src/main/resources/app.properties 
configurable settings
site - "stackoverflow" , a URI parameter for Users API
min_score - minimal integer, a URI parameter for Users API
sort_users_by- a value for URI parameter "sort" for Users API
locations - a list of locations , separated by comma
tags - comma separated list of tags, used for filtering users
page_size - integer value for URI parameter 'pagesize', the maximal number of items retrieved per request
users_uri - API URI for Users
users_tags_uri - API URI for Users tags, it must be set {ids} with semi-colon separated values of userIds
create_filter_uri - API URI for creating a custom filter , for including and/or excluding several fields from objects
sort_user_tags_by - URI parameter 'sort' for user_tags_uri
