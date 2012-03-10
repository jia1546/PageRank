package pagerank;

/*
 * map entry encodes all the nodes m linked with a given node n and corresponding edge weight
 */
public class MapEntry {
	private String identifier = null;
	private double edgeWeight;
	
	public MapEntry(String identifier, double weight)
	{
		this.identifier = identifier;
		this.edgeWeight = weight;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public double getWeight() {
		return edgeWeight;
	}
	public void setWeight(double weight) {
		this.edgeWeight = weight;
	}
}
