package org.example;

import java.io.IOException;

public class Main {
    private static final String rootFolder = "./src/main/resources/rml/";
    private static final String mapPath = rootFolder + "sales/sales-mappings.ttl";
    private static final String outputPath = rootFolder + "sales/outputFile.ttl";
    private static final String ontologyPath = rootFolder + "sales/superstore-sales-ontology.owl";

    public static void main(String[] args) throws IOException {
        GraphDBConnector.initRepo();
        GraphDBConnector.insertOntologyToGraphDB(ontologyPath);

//        RMLPipeline.runRMLMapper(mapPath, outputPath);
    }
}