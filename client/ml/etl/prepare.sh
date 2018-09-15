#!/bin/bash
if [ $# -eq 0 ]
then
    echo "usage: sh prepare.sh path/to/data"
    exit -1
fi
ETL=$(pwd)
WD=$(mktemp -d)
mkdir $WD/raw
mkdir $WD/throughput
mkdir $WD/latency
ID=$1
AC="{ printf "
AC+='"cp %s '
AC+=$WD
AC+='/raw\n", $1 }'
echo $AC >ac.awk
find $ID -type f -name '*.csv' -print | awk -f ac.awk | sh
rm ac.awk
cd $WD/raw
for f in *.csv
do
    Rscript $ETL/tidy.r $WD/raw/$f $WD/latency/$f $WD/throughput/$f
done
cd $ETL
echo "year,month,day,hour,minute,cloud,feed,friends POST,outbound POST,participant POST" >throughput.csv
cat $WD/throughput/*.csv | grep -v 'year,month,day,hour,minute' >>throughput.csv
echo "year,month,day,hour,minute,second,cloud,feed,friends POST,outbound POST,participant POST" >latency.csv
cat $WD/latency/*.csv | grep -v 'year,month,day,hour,minute' >>latency.csv
rm -Rf $WD

