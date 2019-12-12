package com.sample.jena;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.ValidityReport.Report;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

public class OntologyModeling {

	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(OntologyModeling.class);
		logger.info("Ontology Modeling Code");

		//Model model = createOWLSample();

		//Model model = readOWLSample();

		//selectOWLSample(model);

		//validateOWLSample(model);

		//queryOWLSample(model);

		//querySPARQLendpoint();

		/*
		try {
			readCSVSample();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 */
	}

	public static void readCSVSample() throws IOException {
		CsvMapper csvMapper = new CsvMapper();
		csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
		InputStream csvInput = FileManager.get().open( "Gracenote_genre_mapping.csv" );
		MappingIterator<String[]> csvIterator = csvMapper.readerFor(String[].class).readValues(csvInput);
		while (csvIterator.hasNext()) {
			String[] row = csvIterator.next();
			System.out.println("[" + row[0] +", " + row[1] + "]");
		}
	}

	public static void querySPARQLendpoint() {
		String queryString =
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
						+ "PREFIX bd: <http://www.bigdata.com/rdf#>\n"
						+ "PREFIX wikibase: <http://wikiba.se/ontology#>\n"
						+ "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n"
						+ "PREFIX wd: <http://www.wikidata.org/entity/>\n"
						+ "SELECT DISTINCT ?item ?itemLabel\n"
						+ "WHERE {\n"
						+ "?item wdt:P31 wd:Q11424 .\n"
						+ "?item wdt:P577 ?pubdate .\n"
						+ "FILTER (?pubdate >= \"2019-01-01T00:00:00Z\"^^xsd:dateTime)\n"
						+ "SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\" . }\n"
						+ "}\n";
		QueryExecution qexec = QueryExecutionFactory.sparqlService("https://query.wikidata.org/sparql", queryString);
		try {
			ResultSet results = qexec.execSelect();
			ResultSetFormatter.outputAsTSV(System.out, results);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			qexec.close();
		}
	}

	public static ResultSet queryOWLSample(Model model) {

		String queryString = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "SELECT ?s ?p ?o \n"
				+ "WHERE { \n"
				+ "?s ?p ?o . \n"
				+ "FILTER regex(?o, \"most\", \"i\") \n"
				+ "} \n";

		Query query = QueryFactory.create(queryString);

		ResultSet copiedResults = null;

		Dataset dataset = DatasetFactory.create();
		dataset.setDefaultModel(model) ;

		try (QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {

			ResultSet results = qexec.execSelect();

			copiedResults = ResultSetFactory.copyResults(results) ;

			for ( ; results.hasNext() ; ) {

				QuerySolution solution = results.nextSolution();
				RDFNode nodeResult = solution.get("var_o"); // Get a result variable by name
				Resource resourceResult = solution.getResource("var_o"); // Get a result variable by name - must be a resource
				Literal literalResult = solution.getLiteral("var_o"); // Get a result variable by name - must be a literal
			}

			qexec.close();
		}

		ResultSetFormatter.out(System.out, copiedResults, query) ;

		return copiedResults;
	}

	public static void validateOWLSample(Model model) {

		// validate that it conforms to the OWL model
		// same as "OntModelSpec.OWL_MEM_RULE_INF"
		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		InfModel infmodel = ModelFactory.createInfModel(reasoner, model);
		ValidityReport validity = infmodel.validate();

		if (validity.isValid()) {
			System.out.println("No logical inconsistencies were detected.");
		} else {
			System.out.println("At least one error included.");
			for (Iterator<Report> i = validity.getReports(); i.hasNext(); ) {
				ValidityReport.Report report = (ValidityReport.Report)i.next();
				System.out.println(" - " + report);
			}
		}
	}

	public static Model readOWLSample() {

		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		String inputFileName = "CKG_Ontology_Schema.ttl";

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open( inputFileName );
		if (in == null) {
			throw new IllegalArgumentException(
					"File: " + inputFileName + " not found");
		}

		// read the TURTLE file
		model.read(in, null, "TURTLE");

		// read the N-TRIPLE file
		//model.read(in, null, "N-TRIPLE");

		// write it to standard(RDF/XML) out
		//model.write(System.out);

		return model;
	}

	public static Model createOWLSample() {

		// some definitions
		String className = "http://www.sample.com/ckg#Place";
		String classlabel = "Sample JENA";

		// create an empty Model
		Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);

		// create the resource
		Resource resource = model.createResource(className);

		// add the property
		Property property = model.createProperty("hasWeight");
		resource.addProperty(property, model.createTypedLiteral(0.1234));
		resource.addProperty(RDFS.label, model.createLiteral(classlabel, "en"));
		resource.addProperty(RDFS.label, model.createLiteral(classlabel, "ko"));

		// list the statements in the Model
		StmtIterator iter = model.listStatements();

		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();  // get next statement
			Resource subject = stmt.getSubject();     // get the subject
			Property predicate = stmt.getPredicate();   // get the predicate
			RDFNode object = stmt.getObject();      // get the object

			System.out.print(subject.toString());
			System.out.print(" " + predicate.toString() + " ");
			if (object instanceof Resource) {
				System.out.println(object.toString());
			} else { // object is a literal
				System.out.println("[" + object.toString() + "]");
			}
		}

		// now write the model in N-TRIPLES form to a file
		model.write(System.out, "N-TRIPLE");

		return model;
	}

	public static void selectOWLSample(Model model) {

		Property match = model.getProperty("http://www.sample.com/owl/ckg#hasPriorityWeight");
		Selector selector = new SimpleSelector((Resource)null, (Property)match, (RDFNode)null);
		StmtIterator iter = model.listStatements(selector);

		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();  // get next statement
			Resource subject = stmt.getSubject();     // get the subject
			Property predicate = stmt.getPredicate();   // get the predicate
			RDFNode object = stmt.getObject();      // get the object

			System.out.print(subject.toString());
			System.out.print(" " + predicate.toString() + " ");
			if (object instanceof Resource) {
				System.out.print(object.toString());
			} else {
				// object is a literal
				System.out.print(" \"" + object.toString() + "\"");
			}

			System.out.println(" .");
		}
	}
}
