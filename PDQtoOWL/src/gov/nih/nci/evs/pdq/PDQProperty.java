package gov.nih.nci.evs.pdq;

public class PDQProperty {

	private String name = "";
	private String value = "";

	public PDQProperty(String propName, String propValue) {
		this.name = propName;
		this.value = propValue;
	}

	public String getPropertyName() {
		return name;
	}

	public String getPropertyValue() {
		return value;
	}
}
