import hosts
print '''
# cache settings

cache_pool: 10

cache_host: {redis}

cache_port: 6379

cache_timeout: 60

cache_ttl: 300

circuit_breaker_error_threshold: 20

circuit_breaker_sleep_window: 20000

# NoSql settings

nosql_host: {cassandra}

nosql_keyspace: activity

nosql_consistency_level: one

# elastic search settings

search_host: {elastic}

search_port: 9200

search_index: feed

search_mapping: stories

# kafka settings

message_broker: {kafka}

message_topic: feed

# Database settings.

database:

  driverClass: com.mysql.jdbc.Driver

  user: feed

  password: feed1234

  url: jdbc:mysql://{mysql}/feed

  minSize: 3

  maxSize: 18

server:
#  softNofileLimit: 1000
#  hardNofileLimit: 1000
  applicationConnectors:
    - type: http
      port: 8080
    - type: https
      port: 8443
      keyStorePath: example.keystore
      keyStorePassword: example
      validateCerts: false
      validatePeers: false
    #this requires the alpn-boot library on the JVM's boot classpath
    #- type: h2
    #  port: 8445
    #  keyStorePath: example.keystore
    #  keyStorePassword: example
    #  validateCerts: false
    #  validatePeers: false
  adminConnectors:
    - type: http
      port: 8081
    - type: https
      port: 8444
      keyStorePath: example.keystore
      keyStorePassword: example
      validateCerts: false
      validatePeers: false

logging:

  # The default level of all loggers. Can be OFF, ERROR, WARN, INFO, DEBUG, TRACE, or ALL.
  level: WARN
  loggers:
    info.glennengstrand: WARN
  appenders:
    - type: console
    - type: file
      threshold: ALL
      logFormat: "%-6level [%d{{HH:mm:ss.SSS}}] [%t] %logger{{5}} - %X{{code}} %msg %n"
      currentLogFilename: /tmp/application.log
      archivedLogFilenamePattern: /tmp/application-%d{{yyyy-MM-dd}}-%i.log.gz
      archivedFileCount: 7
      timeZone: UTC
      maxFileSize: 10MB

'''.format(redis=hosts.settings['redis'],
           cassandra=hosts.settings['cassandra'],
           elastic=hosts.settings['elastic'], 
           kafka=hosts.settings['kafka'], 
           mysql=hosts.settings['mysql'])
