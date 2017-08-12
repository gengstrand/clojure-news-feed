{
    settings[$1] = $2
}
END {
    print "settings = dict("
    printf "   mysql = '%s', \n", settings["mysql"]
    printf "   cassandra = '%s', \n", settings["cassandra"]
    printf "   redis = '%s', \n", settings["redis"]
    printf "   elastic = '%s' \n", settings["elasticsearch"]
    print ")"
}