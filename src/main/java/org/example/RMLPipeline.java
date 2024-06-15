package org.example;

import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.functions.lib.IDLabFunctions;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;
import be.ugent.rml.term.NamedNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class RMLPipeline {
    public static void runRMLMapper(String mapPath, String outputPath) {
        try {
            File mappingFile = new File(mapPath);
            Writer outputFile = new FileWriter(outputPath);
            // Get the mapping string stream
            InputStream mappingStream = new FileInputStream(mappingFile);
            // Load the mapping in a QuadStore
            QuadStore rmlStore = QuadStoreFactory.read(mappingStream);
            // Set up the basepath for the records factory, i.e., the basepath for the (local file) data sources
            RecordsFactory factory = new RecordsFactory(mappingFile.getParent());
            // Set up the functions used during the mapping
            Map<String, Class> libraryMap = new HashMap<>();
            libraryMap.put("IDLabFunctions", IDLabFunctions.class);
            FunctionLoader functionLoader = new FunctionLoader(null, libraryMap);
            // Set up the outputstore (needed when you want to output something else than nquads)
            QuadStore outputStore = new RDF4JStore();
            // Create the Executor
            Executor executor = new Executor(rmlStore, factory, functionLoader, outputStore, Utils.getBaseDirectiveTurtle(mappingStream));
            // Execute the mapping
            QuadStore result = executor.executeV5(null).get(new NamedNode("rmlmapper://default.store"));

            // Output the result in console
            //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
            //result.write(out, "turtle");
            //out.close();

            // Output the results in a file
            result.write(outputFile, "turtle");
            outputFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}