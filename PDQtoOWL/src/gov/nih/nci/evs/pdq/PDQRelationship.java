package gov.nih.nci.evs.pdq;

public class PDQRelationship {

	private String name = "";
	private String target = "";
	private boolean hierarchy = false;

	public PDQRelationship(String roleName, String roleTarget) {
		this.name = roleName;
		this.target = roleTarget;
	}

	public PDQRelationship(String roleName, String roleTarget,
	        boolean hierarchical) {
		this.name = roleName;
		this.target = roleTarget;
		this.hierarchy = hierarchical;
	}

	public String getName() {
		return name;
	}

	public String getTarget() {
		return target;
	}

	public void setIsHierarchical(boolean isH) {
		hierarchy = isH;
	}

	public boolean getIsHierarchical() {
		return hierarchy;
	}
}
