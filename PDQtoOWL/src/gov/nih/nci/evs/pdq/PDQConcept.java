package gov.nih.nci.evs.pdq;

import java.util.Vector;

public class PDQConcept {

	private String id = "";
//	private final String NCIThesaurusConceptId = "";
//	private final String LegacyPDQID = "";
	private String PreferredName = "";
	private final Vector<PDQProperty> properties = new Vector<PDQProperty>();
	private final Vector<PDQRelationship> rels = new Vector<PDQRelationship>();
	private final Vector<PDQRelationship> parents = new Vector<PDQRelationship>();
	private PDQProperty definition = null;
	private boolean deprecated = false;

	public PDQConcept() {

	}

	public PDQProperty getDefinition() {
		return definition;
	}

	public void setDefinition(PDQProperty def) {
		this.definition = def;
	}

	public Vector<PDQRelationship> getParents() {
		return parents;
	}

	public void addParent(PDQRelationship rel) {
		parents.add(rel);
	}

	public void setTermType(PDQProperty prop) {
		if (prop.getPropertyValue().equals("Obsolete term")) {
			// Add relationsip to Obsolete Concept
			deprecated = true;
			PDQRelationship rel = new PDQRelationship("subClassOf",
					"Obsolete_Concepts", true);
			parents.add(rel);
		}
		else if (prop.getPropertyValue().equals("Protocol selection criteria")){
			PDQRelationship rel = new PDQRelationship("subClassOf","Protocol_Selection_Criteria",true);
			parents.add(rel);
		}
		properties.add(prop);
	}

	// public void setNCIThesaurusID(String tmp) {
	// NCIThesaurusConceptId = tmp;
	//
	// }
	//
	// public void setLegacyPDQID(String tmp) {
	// LegacyPDQID = tmp;
	//
	// }
	//
	// public String getNCIThesaurusID() {
	// return NCIThesaurusConceptId;
	// }
	//
	// public String getLegacyPDQID() {
	// return LegacyPDQID;
	// }

	public String getPreferredName() {
		return PreferredName;
	}

	public void setPreferredName(String preferredName) {
		PreferredName = preferredName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void addProperty(PDQProperty property) {
		properties.add(property);
	}

	public Vector<PDQProperty> getProperties() {
		return properties;
	}

	public void addRelationship(PDQRelationship rel) {
		rels.add(rel);
	}

	public Vector<PDQRelationship> getRelationships() {
		return rels;
	}

	public void addMetaCUI(PDQProperty metacui) {
		if (!properties.contains(metacui)) {
			properties.add(metacui);
		}
	}
}
