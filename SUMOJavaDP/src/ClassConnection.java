
public class ClassConnection {
	private String connectionEdgeNamePair;
	private String type;
	private double length;
	private double speed;
	
	public ClassConnection(String connectionEdgeNamePair, String type, double length, double speed) {
		this.connectionEdgeNamePair = connectionEdgeNamePair;
		this.type = type;
		this.length = length;
		this.speed = speed;
	}
	
	
	public String toString() {
		String ret = "";
		ret += "connectionEdgeNamePair=" + connectionEdgeNamePair + "\t";
		ret += "type=" + type + "\t";
		ret += "length=" + length + "\t";
		ret += "speed=" + speed + "\t";
		return ret;
	}
	
	public double length() {
		return length;
	}
	
	public double speed() {
		return speed;
	}
	
	public String type() {
		return type;
	}
}
