package org.example;

import java.io.IOException;

public class Main {
    private static final String rootFolder = "./src/main/resources/rml/";
    private static final String mapPath = rootFolder + "sales/sales-mappings.ttl";
    private static final String outputPath = rootFolder + "sales/sales-rdf-data.ttl";
    private static final String ontologyPath = rootFolder + "sales/superstore-sales-ontology.owl";

    public static void main(String[] args) throws IOException {
        GraphDBConnector.initRepo(); // initializes the connection with the repository of GraphDB
        GraphDBConnector.insertRDFFileToGraphDB(ontologyPath); // inserts the owl ontology file in the repo
        RMLPipeline.runRMLMapper(mapPath, outputPath); // execute the RML rules and create the output file with the rdf data
        GraphDBConnector.insertRDFFileToGraphDB(outputPath); // inserts the rdf data in the repo
        SPARQLExecutor sparqlExecutor = new SPARQLExecutor();
        sparqlExecutor.executePipeline("2011-09-08T00:00:00", "2011-09-15T00:00:00");
    }
}