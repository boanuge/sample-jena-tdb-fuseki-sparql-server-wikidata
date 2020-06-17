package com.sample.jena;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

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
import com.samsung.ckg.tdb.TDBConnection;

public class OntologyModeling {

	public class Class_csv { // Default encoding is UTF-8
		public String filmUID;
		public String movieTitle;
		public String movieID;
	}

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

	public static void writeFile2CSV(String fileName, ArrayList<Class_csv> csvData) {
		CsvSchema schema = CsvSchema.builder()
				.addColumn( "filmUID" )
				.addColumn( "movieTitle" )
				.addColumn( "movieID" )
				.setUseHeader( false )
				.build();
		CsvMapper csvMapper = new CsvMapper();
		csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
		ObjectWriter objectWriter = csvMapper.writer(schema);
		try {
			objectWriter.writeValue(new File( fileName ), csvData);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public static String readFile(String fileName) {

		StringBuilder stringBuilder = new StringBuilder();
		try {
			InputStream inputStream = FileManager.get().open(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return stringBuilder.toString();
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

	public static void testJENA_TDB() {

		TDBConnection tdb = null;

		String URI = "https://tutorial-academy.com/2015/tdb#";

		final String namedModel1 = "Model_German_Cars";
		final String namedModel2 = "Model_US_Cars";

		String modelNameTest = "Any_Named_Model";

		String john = URI + "John";
		String mike = URI + "Mike";
		String bill = URI + "Bill";
		String owns = URI + "owns";

		tdb = new TDBConnection("JENA_TDB");
		// named Model 1
		tdb.addStatement( namedModel1, john, owns, URI + "Porsche" );
		tdb.addStatement( namedModel1, john, owns, URI + "BMW" );
		tdb.addStatement( namedModel1, mike, owns, URI + "BMW" );
		tdb.addStatement( namedModel1, bill, owns, URI + "Audi" );
		tdb.addStatement( namedModel1, bill, owns, URI + "BMW" );

		// named Model 2
		tdb.addStatement( namedModel2, john, owns, URI + "Chrysler" );
		tdb.addStatement( namedModel2, john, owns, URI + "Ford" );
		tdb.addStatement( namedModel2, bill, owns, URI + "Chevrolet" );

		// null = wildcard search. Matches everything with BMW as object!
		List<Statement> result = tdb.getStatements( namedModel1, null, null, URI + "BMW");
		System.out.println( namedModel1 + " size: " + result.size() + "\n\t" + result );

		// null = wildcard search. Matches everything with john as subject!
		result = tdb.getStatements( namedModel2, john, null, null);
		System.out.println( namedModel2 + " size: " + result.size() + "\n\t" + result );

		// remove all statements from namedModel1
		tdb.removeStatement( namedModel1, john, owns, URI + "Porsche" );
		tdb.removeStatement( namedModel1, john, owns, URI + "BMW" );
		tdb.removeStatement( namedModel1, mike, owns, URI + "BMW" );
		tdb.removeStatement( namedModel1, bill, owns, URI + "Audi" );
		tdb.removeStatement( namedModel1, bill, owns, URI + "BMW" );

		result = tdb.getStatements( namedModel1, john, null, null);
		System.out.println( namedModel1 + " size: " + result.size() + "\n\t" + result );
		tdb.close();

		TDBConnection tdb1 = new TDBConnection("tdb");
		List<Statement> result1 = tdb1.getStatements(modelNameTest, null, null, null);
		System.out.println( modelNameTest + " size: " + result1.size() + "\n\t" + result1 );
	}
}
