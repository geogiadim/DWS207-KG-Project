package org.example;

import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;

public class SPARQLExecutor {
    private final String PREFIXES = "PREFIX supersales: <http://www.semanticweb.org/ontologies/superstore-sales#>\n"
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";

    public void executePipeline(String startDate, String endDate){
        String dateFilter = "     FILTER (?date >= \"" + startDate + "\"^^xsd:dateTime && ?date < \"" + endDate + "\"^^xsd:dateTime)\n";
        totalSales(dateFilter);
        individualSales(dateFilter);
        totalSalesPerProductCategory(dateFilter);
        countsPerShipmentCategory(dateFilter);
        totalOrders(dateFilter);
    }

    private void totalSales(String dateFilter){
        String queryString = PREFIXES
                + "SELECT (SUM(?individualSale) AS ?totalSales) WHERE {\n"
                + "    ?order a supersales:Order;\n"
                + "           supersales:orderDate ?date;\n"
                + "           supersales:involvesProduct ?product.\n"
                + "    ?product a supersales:Product;\n"
                + "             supersales:sales ?individualSale.\n"
                + dateFilter
                + "}";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnector.getSalesRepoConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        try (TupleQueryResult result = tupleQuery.evaluate()) {
            // Process query results
            while (result.hasNext()) {
                // Access the query solution and extract the totalSales value
                org.eclipse.rdf4j.query.BindingSet bindingSet = result.next();
                org.eclipse.rdf4j.model.Value totalSalesValue = bindingSet.getValue("totalSales");
                if (totalSalesValue != null) {
                    double totalSales = ((org.eclipse.rdf4j.model.Literal) totalSalesValue).doubleValue();
                    System.out.println("Total Sales: " + totalSales);
                }
            }
        } finally {
            // Close the repository connection
            GraphDBConnector.closeSalesConn();
        }
    }


    private void individualSales(String dateFilter) {
        String queryString = PREFIXES
                + "SELECT ?product ?productName ?individualSale WHERE {\n"
                + "    ?order a supersales:Order;\n"
                + "           supersales:orderDate ?date;\n"
                + "           supersales:involvesProduct ?product.\n"
                + "    ?product a supersales:Product;\n"
                + "             supersales:sales ?individualSale;\n"
                + "             supersales:productName ?productName.\n"
                + dateFilter
                + "}";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnector.getSalesRepoConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        try (TupleQueryResult result = tupleQuery.evaluate()) {
            // Process query results
            while (result.hasNext()) {
                // Access the query solution and extract the individual sales details
                org.eclipse.rdf4j.query.BindingSet bindingSet = result.next();
                org.eclipse.rdf4j.model.Value productValue = bindingSet.getValue("product");
                org.eclipse.rdf4j.model.Value productNameValue = bindingSet.getValue("productName");
                org.eclipse.rdf4j.model.Value individualSaleValue = bindingSet.getValue("individualSale");
                if (productValue != null && productNameValue != null && individualSaleValue != null) {
                    String product = productValue.stringValue();
                    String productName = productNameValue.stringValue();
                    double individualSale = ((org.eclipse.rdf4j.model.Literal) individualSaleValue).doubleValue();
                    System.out.println("Product: " + product + ", Product Name: " + productName + ", Individual Sale: " + individualSale);
                }
            }
        } finally {
            // Close the repository connection
            GraphDBConnector.closeSalesConn();
        }
    }

    private void totalSalesPerProductCategory(String dateFilter) {
        String queryString = PREFIXES
                + "SELECT ?category (SUM(?sales) AS ?totalSales) WHERE {\n"
                + "    ?order a supersales:Order;\n"
                + "           supersales:orderDate ?date;\n"
                + "           supersales:involvesProduct ?product.\n"
                + "    ?product a ?category;\n"
                + "             supersales:sales ?sales.\n"
                + dateFilter
                + "    FILTER (?category IN (supersales:OfficeSupplies, supersales:Technology, supersales:Furniture))\n"
                + "}\n"
                + "GROUP BY ?category";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnector.getSalesRepoConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        try (TupleQueryResult result = tupleQuery.evaluate()) {
            // Process query results
            while (result.hasNext()) {
                // Access the query solution and extract the category and totalSales value
                org.eclipse.rdf4j.query.BindingSet bindingSet = result.next();
                org.eclipse.rdf4j.model.Value categoryValue = bindingSet.getValue("category");
                org.eclipse.rdf4j.model.Value totalSalesValue = bindingSet.getValue("totalSales");
                if (categoryValue != null && totalSalesValue != null) {
                    String category = categoryValue.stringValue();
                    double totalSales = ((org.eclipse.rdf4j.model.Literal) totalSalesValue).doubleValue();
                    System.out.println("Category: " + category + ", Total Sales: " + totalSales);
                }
            }
        } finally {
            // Close the repository connection
            GraphDBConnector.closeSalesConn();
        }
    }


    private void countsPerShipmentCategory(String dateFilter) {
        String queryString = PREFIXES
                + "SELECT ?shipmentClass (COUNT(?order) AS ?orderCount) WHERE {\n"
                + "    ?order a supersales:Order;\n"
                + "           supersales:orderDate ?date;\n"
                + "           supersales:hasShipment ?shipment.\n"
                + "    ?shipment a ?shipmentClass.\n"
                + dateFilter
                + "    FILTER (?shipmentClass IN (supersales:StandardClassShipment, supersales:FirstClassShipment, supersales:SecondClassShipment))\n"
                + "}\n"
                + "GROUP BY ?shipmentClass";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnector.getSalesRepoConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        try (TupleQueryResult result = tupleQuery.evaluate()) {
            // Process query results
            while (result.hasNext()) {
                // Access the query solution and extract the shipmentClass and orderCount
                org.eclipse.rdf4j.query.BindingSet bindingSet = result.next();
                org.eclipse.rdf4j.model.Value shipmentClassValue = bindingSet.getValue("shipmentClass");
                org.eclipse.rdf4j.model.Value orderCountValue = bindingSet.getValue("orderCount");
                if (shipmentClassValue != null && orderCountValue != null) {
                    String shipmentClass = shipmentClassValue.stringValue();
                    long orderCount = ((org.eclipse.rdf4j.model.Literal) orderCountValue).longValue();
                    System.out.println("Shipment Class: " + shipmentClass + ", Order Count: " + orderCount);
                }
            }
        } finally {
            // Close the repository connection
            GraphDBConnector.closeSalesConn();
        }
    }

    private void totalOrders(String dateFilter) {
        String queryString = PREFIXES
                + "SELECT (COUNT(DISTINCT ?order) AS ?orderCount) WHERE {\n"
                + "    ?order a supersales:Order;\n"
                + "           supersales:orderDate ?date.\n"
                + dateFilter
                + "}";

        // Execute SPARQL query
        TupleQuery tupleQuery = GraphDBConnector.getSalesRepoConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        try (TupleQueryResult result = tupleQuery.evaluate()) {
            // Process query results
            while (result.hasNext()) {
                // Access the query solution and extract the orderCount
                org.eclipse.rdf4j.query.BindingSet bindingSet = result.next();
                org.eclipse.rdf4j.model.Value orderCountValue = bindingSet.getValue("orderCount");
                if (orderCountValue != null) {
                    long orderCount = ((org.eclipse.rdf4j.model.Literal) orderCountValue).longValue();
                    System.out.println("Total Orders: " + orderCount);
                }
            }
        } finally {
            // Close the repository connection
            GraphDBConnector.closeSalesConn();
        }
    }
}