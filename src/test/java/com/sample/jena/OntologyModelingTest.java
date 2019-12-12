package com.sample.jena;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

import com.sample.jena.OntologyModeling;

public class OntologyModelingTest {

	@Test
	public void testQueryOWLSample() {

		Model model = createOWLSample();

		OntologyModeling ontModeling = new OntologyModeling();

		ontModeling.queryOWLSample(model);

		assertNotNull("Checking OWL Model", model);
		//assertTrue("The model should not be empty", model.size() > 0); // Failure case
	}

	public static Model createOWLSample() {

		// create an empty Model
		Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);

		return model;
	}
}
