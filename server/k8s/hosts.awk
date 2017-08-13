BEGIN {
    settings["mysql"] = "127.0.0.1"
    settings["cassandra"] = "127.0.0.1"
    settings["redis"] = "127.0.0.1"
    settings["kafka"] = "127.0.0.1"
    settings["elasticsearch"] = "127.0.0.1"
}
{
    settings[$1] = $2
}
END {
    print "settings = dict("
    printf "   mysql = '%s', \n", settings["mysql"]
    printf "   cassandra = '%s', \n", settings["cassandra"]
    printf "   redis = '%s', \n", settings["redis"]
    printf "   kafka = '%s', \n", settings["kafka"]
    printf "   elastic = '%s', \n", settings["elasticsearch"]
    printf "   solr = '%s' \n", settings["elasticsearch"]
    print ")"
}
