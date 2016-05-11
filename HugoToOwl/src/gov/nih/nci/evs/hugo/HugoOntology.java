package gov.nih.nci.evs.hugo;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class HugoOntology {

	private HugoCsvParser cvsParser = null;
	private Vector<HugoConcept> concepts = new Vector<HugoConcept>();
	private HashMap<String, String> locus = new HashMap<String, String>();

	public HugoOntology(HugoCsvParser in_cvsParser) {
		cvsParser = in_cvsParser;
		createConcepts();
	}

	private void createConcepts() {

		Vector<String> header = cvsParser.getHeader();
		// loop through the data. Create a new concept for each line.
		for (Vector<String> values : cvsParser.getData()) {
			HashMap<String, String> propertyValueList = new HashMap<String, String>();
			for (int i = 0; i < header.size(); i++) {
				if (i < values.size()) {
					propertyValueList.put(header.get(i), values.get(i));
				} else {
					propertyValueList.put(header.get(i), null);
				}
			}
			loadLocusType_LocusGroup(propertyValueList);
			HugoConcept concept = new HugoConcept(propertyValueList);
			concepts.add(concept);
		}

	}

	public Vector<HugoConcept> getConcepts() {
		return concepts;
	}

	private void loadLocusType_LocusGroup(HashMap<String, String> valueList) {
		Set<String> keySet = valueList.keySet();
		String locusType = "", locusGroup = "";
		for (String key : keySet) {
			if (key.equals("Locus Type")) {
				locusType = valueList.get(key);
			}
			if (key.equals("Locus Group")) {
				locusGroup = valueList.get(key);
			}
		}
		if (!locus.containsKey(locusType)) {
			locus.put(locusType, locusGroup + "_group");
		}
	}

	public HashMap<String, String> getLocusHierarchy() {
		return locus;
	}

}
