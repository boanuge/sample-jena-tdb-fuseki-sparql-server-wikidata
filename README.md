## Apache Jena Fuseki is a SPARQL server.

It can run as a operating system service, as a Java web application (WAR file), and as a standalone server.
Fuseki is tightly integrated with TDB to provide a robust, transactional persistent storage layer, and incorporates Jena text query.
A TDB can be used as a high performance RDF store on a single machine, and the TDB store can be accessed and managed with the provided command line scripts and via the Jena API.
When accessed using transactions a TDB dataset is protected against corruption, unexpected process terminations and system crashes. However, it is recommended to access a TDB dataset from a single JVM at a time otherwise data corruption may occur.
If a TDB dataset needs to be used between multiple applications, please use our Fuseki component which provides a SPARQL server that can use TDB for persistent storage and provides the SPARQL protocols for query, update and REST update over HTTP.

## TDB Java API - https://jena.apache.org/documentation/tdb/java_api.html

Fuseki Quickstart - https://jena.apache.org/documentation/fuseki2/fuseki-quick-start.html

Running Fuseki - https://jena.apache.org/documentation/fuseki2/fuseki-run.html

Configuring Fuseki - https://jena.apache.org/documentation/fuseki2/fuseki-configuration.html

## Note:
That I've used VER as a placeholder for the Fuseki version here since that value will depend on which version of Fuseki you have downloaded. For reference at time of writing this answer the latest version is 1.0.2. This command launches Fuseki against the TDB database located in /path/to/database with the dataset path of /ds. Therefore you can point your chosen SPARQL client at http://localhost:3030/ds/query to make queries or http://localhost:3030/ds/update to make updates.

fuseki-server [--mem | --loc=DIR] [[--update] /NAME]

fuseki-server --config=CONFIG

Ubuntu$ java -jar fuseki-server.jar --loc=/path/to/database/folder --update /ds

Windows> java -jar fuseki-server.jar --loc=C:\TDB --update /ds

$ java -jar fuseki-server.jar --config=path/to/configuration/file

Reference links:

https://stackoverflow.com/questions/24798024/how-i-can-use-fuseki-with-jena-tdb
https://medium.com/@rrichajalota234/how-to-apache-jena-fuseki-3-x-x-1304dd810f09

## [Maven for Jena Fuseki]

<!-- http://jena.apache.org/download/index.cgi -->
 <dependency>
 <groupId>org.apache.jena</groupId>
 <artifactId>apache-jena-libs</artifactId>
 <type>pom</type>
 <version>3.13.1</version>
 </dependency>
 <dependency>
 <groupId>org.apache.jena</groupId>
 <artifactId>jena-fuseki-main</artifactId>
 <type>pom</type>
 <version>3.13.1</version>
 </dependency>

## Jena Library Files:

apache-jena-3.13.1.zip
apache-jena-fuseki-3.13.1.zip
jena-3.13.1-source-release.zip

## SPARQL Standards
https://jena.apache.org/documentation/fuseki2/rdf-sparql-standards.html

The relevant SPARQL 1.1 standards are:
SPARQL 1.1 Query
SPARQL 1.1 Update
SPARQL 1.1 Protocol
SPARQL 1.1 Graph Store HTTP Protocol
SPARQL 1.1 Query Results JSON Format
SPARQL 1.1 Query Results CSV and TSV Formats
SPARQL Query Results XML Format

RDF Standards, Some RDF 1.1 standards:
RDF 1.1 Turtle
RDF 1.1 Trig
RDF 1.1 N-Triples
RDF 1.1 N-Quads
JSON-LD
