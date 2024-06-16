package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.*;

public class GraphDBConnector {
    private static RemoteRepositoryManager manager;
    private static Repository salesRepo;
    private static RepositoryConnection salesConn;

    public static void initRepo(){
        Dotenv dotenv = Dotenv.load();
        // initiate a remote repo manager
        manager = new RemoteRepositoryManager(dotenv.get("GRAPHDB_URL"));
        manager.init();
        manager.setUsernameAndPassword(dotenv.get("GDB_USER"), dotenv.get("GDB_PWD"));
        // instantiate repo
        salesRepo = manager.getRepository(dotenv.get("SALES_REPOSITORY_ID"));
        // connect to the repo
        salesConn = salesRepo.getConnection();
    }

    public static void insertRDFFileToGraphDB(String rdfFilePath) throws FileNotFoundException {
        // load an ontology or rdf data from a file
        File ontologyFile = new File(rdfFilePath);
        // starts a new transaction with the GraphDB repo
        salesConn.begin();
        // Read the file and add its contents to the repository
        try (InputStream input = new FileInputStream(ontologyFile)) {
            salesConn.add(input, "", RDFFormat.TURTLE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Committing the transaction persists the data
        salesConn.commit();
    }

    public static void closeSalesConn(){ salesConn.close(); }
    public static RepositoryConnection getSalesRepoConnection() { return salesConn; }
}