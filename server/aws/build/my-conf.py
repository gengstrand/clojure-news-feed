import hosts
print '''
{{
  "elasticsearch" : {{
  	"host" : "{elastic}", 
  	"port" : 9200, 
  	"index" : "performance", 
  	"type" : "detail", 
  	"interval" : 5000
  }}, 
  "port" : 8888
  
}}
'''.format(elastic=hosts.settings['elastic'])
