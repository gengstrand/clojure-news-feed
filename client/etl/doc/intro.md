# Introduction to etl

This Clojure project creates a command line utility that inputs the output from the news feed performance map reduce Hadoop job and uses that data to populate a mysql database that can be used by Pentaho's Mondrian open source OLAP project.

cd ~/git/clojure-news-feed/clients/etl/etc

Create a database with appropriate credentials and define the proper schema for it was the starschema.sql file.

Download and install Mondrian as a tomcat service. Let Tomcat load this app then make the following adjustments.

cd /var/lib/tomcat/webapps

cp -Rf mondrian feed

cp ~/git/clojure-news-feed/clients/etl/etc/feedcube.xml feed/WEB-INF/queries

Everywhere in the feed folder that specifies a mysql database and credentials needs to be adjusted to point to your mysql database and credentials instead.

Everywhere in the feed folder that references FoodMart.xml now needs to reference feedcube.xml instead. On my copy of Mondrian, that was this list of files.

test/param4.jsp
WEB-INF/jpivot/jpivot-tags.tld
WEB-INF/jpivot/jpivot-tags.xml
WEB-INF/mondrian.properties
WEB-INF/queries/mondrian.jsp
WEB-INF/queries/arrows.jsp
WEB-INF/queries/testrole.jsp
WEB-INF/queries/fourhier.jsp
WEB-INF/queries/colors.jsp
WEB-INF/datasources.xml
zero.jsp

You will also need to adjust all the MDX queries. For example, you should use something like this on the WEB-INF/queries/mondrian.jsp file.

select {[Measures].[throughput]} ON COLUMNS,
  {([Entity].[All Entities], [Time].[2014])} ON ROWS
from [feed]
where [Activity].[All Activities]

Here are some more queries that I found to be of use when analyzing the performance data.

select {[Measures].[mode]} ON COLUMNS,
  {([Entity].[Outbound], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[post] 

select {[Measures].[vigintile]} ON COLUMNS,
  {([Entity].[Outbound], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[post] 

select {[Measures].[throughput]} ON COLUMNS,
  {([Entity].[Outbound], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[post] 

select {[Measures].[mode]} ON COLUMNS,
  {([Entity].[Outbound], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[search] 

select {[Measures].[vigintile]} ON COLUMNS,
  {([Entity].[Outbound], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[search] 

select {[Measures].[throughput]} ON COLUMNS,
  {([Entity].[Outbound], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[search] 

select {[Measures].[mode]} ON COLUMNS,
  {([Entity].[Participant], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[store] 

select {[Measures].[vigintile]} ON COLUMNS,
  {([Entity].[Participant], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[store] 

select {[Measures].[throughput]} ON COLUMNS,
  {([Entity].[Participant], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[store] 

select {[Measures].[mode]} ON COLUMNS,
  {([Entity].[Inbound], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[store] 

select {[Measures].[vigintile]} ON COLUMNS,
  {([Entity].[Inbound], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[store] 

select {[Measures].[throughput]} ON COLUMNS,
  {([Entity].[Inbound], [Time].[2014].[1].[3])} ON ROWS
from [feed]
where [Activity].[store] select {[Measures].[mode]} ON COLUMNS,
  Crossjoin(Hierarchize(Union({[Activity].[All Activities]}, [Activity].[All Activities].Children)), {[Time].[2014]}) ON ROWS
from [feed]
where [Entity].[Outbound]

