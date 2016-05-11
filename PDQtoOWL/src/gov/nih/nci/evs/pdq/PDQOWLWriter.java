package gov.nih.nci.evs.pdq;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.coode.owl.rdf.rdfxml.RDFXMLOntologyStorer;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.OWLXMLOntologyFormat;
import org.semanticweb.owl.io.WriterOutputTarget;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLLabelAnnotation;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.util.SimpleURIMapper;

public class PDQOWLWriter {

	/** The manager. */
	private OWLOntologyManager manager;

	/** The ontology. */
	private OWLOntology ontology;

	private URI saveURI;
	private URI ontologyURI;
	private OWLDataFactory factory;
	String typeConstantString = "http://www.w3.org/2001/XMLSchema#string";
	private final String typeConstantLiteral = "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral";
	String thing = "http://www.w3.org/2002/07/owl#/owlThing";

	public PDQOWLWriter(PDQOntology onto, URI inSaveURI) {
		try {
			// this.saveURI = new URI(
			// "file:///C:/Documents%20and%20Settings/wynner/workspace/HugoToOwl/Hugo.owl");
			this.saveURI = inSaveURI;
			this.manager = OWLManager.createOWLOntologyManager();
			this.ontologyURI = URI
			        .create("http://ncicb.nci.nih.gov/xml/owl/EVS/PDQ.owl");
			SimpleURIMapper mapper = new SimpleURIMapper(this.ontologyURI,
			        this.saveURI);
			this.manager.addURIMapper(mapper);

			this.ontology = this.manager.createOntology(this.saveURI);
			this.factory = this.manager.getOWLDataFactory();
			// createHierarchyConcepts(hugoOntology.getLocusHierarchy());
			createConcepts(onto);
			saveOntology();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Problem creating OWL output.");
			System.exit(0);
		}
	}

	private void createConcepts(PDQOntology pdqOntology) {
		Vector<PDQConcept> conceptVector = pdqOntology.getConcepts();

		for (PDQConcept concept : conceptVector) {
			// Assume hierarchy is unknown
			// OWLClass clz = factory.getOWLClass(URI.create(ontologyURI
			// + concept.code));
			OWLClass clz = this.factory.getOWLClass(createURI(concept.getId()
			        .replace(":", "_")));
			OWLEntity ent = null;

			// Load Parents
			Vector<PDQRelationship> parents = concept.getParents();
			if (parents.size() == 0) {
				OWLClass owlParent = this.factory
				        .getOWLClass(URI.create(thing));
				OWLAxiom axiom = this.factory.getOWLSubClassAxiom(clz,
				        owlParent);

				AddAxiom addAxiom = new AddAxiom(this.ontology, axiom);

				OWLSubClassAxiom subAx = (OWLSubClassAxiom) addAxiom.getAxiom();
				ent = subAx.getSubClass().asOWLClass();
			}

			for (PDQRelationship parent : parents) {
				OWLClass owlParent = this.factory.getOWLClass(createURI(parent
				        .getTarget()));
				// OWLAxiom axiom = factory.getOWLSubClassAxiom(clz, factory
				// .getOWLThing());
				OWLAxiom axiom = this.factory.getOWLSubClassAxiom(clz,
				        owlParent);

				AddAxiom addAxiom = new AddAxiom(this.ontology, axiom);

				OWLSubClassAxiom subAx = (OWLSubClassAxiom) addAxiom.getAxiom();
				ent = subAx.getSubClass().asOWLClass();
				try {
					this.manager.applyChange(addAxiom);
				} catch (Exception e) {
					System.out
					        .println("Error adding axiom :" + concept.getId());
				}

			}

			OWLLabelAnnotation la = this.factory.getOWLLabelAnnotation(concept
			        .getPreferredName());
			OWLAxiom ax = this.factory.getOWLEntityAnnotationAxiom(ent, la);
			AddAxiom addAxiom = new AddAxiom(this.ontology, ax);
			try {
				this.manager.applyChange(addAxiom);
			} catch (Exception e) {
				System.out.println("Error adding label axiom :"
				        + concept.getId());
			}

			addSimpleProperties(concept.getProperties(), ent);
			if (concept.getDefinition() != null) {
				addDefinition(concept.getDefinition(), ent);
			}

		}
	}

	private void addSimpleProperties(Vector<PDQProperty> properties,
	        OWLEntity ent) {
		Set<OWLOntologyChange> changes = new HashSet<OWLOntologyChange>();

		OWLDataType odt = factory
		        .getOWLDataType(URI.create(typeConstantString));

		for (PDQProperty property : properties) {
			OWLTypedConstant otc = factory.getOWLTypedConstant(
			        property.getPropertyValue(), odt);
			OWLAnnotation anno = factory.getOWLConstantAnnotation(
			        createURI(property.getPropertyName()), otc);
			OWLEntityAnnotationAxiom ax1 = factory.getOWLEntityAnnotationAxiom(
			        ent, anno);
			changes.add(new AddAxiom(ontology, ax1));
		}

		if (!changes.isEmpty()) {
			List<OWLOntologyChange> list = new ArrayList<OWLOntologyChange>(
			        changes);
			try {
				manager.applyChanges(list);
			} catch (OWLException e) {
				e.printStackTrace();
			}
		}
	}

	private void addDefinition(PDQProperty definition, OWLEntity ent) {
		// Definitions need to be enclosed in CDATA as Literals.
		String definitionText = "";
		String defType = definition.getPropertyName();
		String def = definition.getPropertyValue();
		definitionText = "<definitionType>" + definition.getPropertyName()
		        + "</definitionType><definition>"
		        + definition.getPropertyValue() + "</definition>";
		OWLDataType odt = factory.getOWLDataType(URI
		        .create(typeConstantLiteral));
		OWLTypedConstant otc = factory.getOWLTypedConstant(definitionText, odt);
		OWLAnnotation anno = factory.getOWLConstantAnnotation(
		        createURI("DEFINITION"), otc);
		OWLEntityAnnotationAxiom ax1 = factory.getOWLEntityAnnotationAxiom(ent,
		        anno);

		AddAxiom addAxiom = new AddAxiom(ontology, ax1);
		try {
			this.manager.applyChange(addAxiom);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates an OWL uri from the ontology namespace and a class name
	 * 
	 * @param className
	 *            the class name
	 * 
	 * @return the uRI
	 */
	public URI createURI(String className) {
		String urlCompliantClassName = PDQtoOWL.underscoredString(className);
		return URI.create(this.ontologyURI + "#" + urlCompliantClassName);
	}

	/**
	 * Save ontology to the file specified in the properties By default encodes
	 * to utf-8
	 */
	private void saveOntology() {
		try {
			RDFXMLOntologyStorer storer = new RDFXMLOntologyStorer();
			File newFile = new File(saveURI);
			FileOutputStream out = new FileOutputStream(newFile);
			WriterOutputTarget target = new WriterOutputTarget(
			        new BufferedWriter(new OutputStreamWriter(out, "UTF8")));
			OWLXMLOntologyFormat format = new OWLXMLOntologyFormat();
			storer.storeOntology(manager, ontology, target, format);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
