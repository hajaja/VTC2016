import java.util.PriorityQueue;


public class ClassXMLVehicle implements Comparable<ClassXMLVehicle>{
	private String id;
	private int t;
	private String strRoute;
	public ClassXMLVehicle(String id, int t, String strRoute) {
		this.id = id;
		this.t = t;
		this.strRoute = strRoute;
	}
	@Override
	public int compareTo(ClassXMLVehicle vehicle) {
		// TODO Auto-generated method stub
		return this.t - vehicle.t;
	}
	
	public String id() {
		return id;
	}
	
	public String t() {
		return String.valueOf(t);
	}
	
	public String strRoute() {
		return strRoute;
	}
	
	public String toString() {
		String ret = "";
		ret += "id=" + id + "\t";
		ret += "t=" + t + "\t";
		ret += "route=" + strRoute + "\t";
		return ret;
	}
	
	public static void main(String[] args) {
		PriorityQueue<ClassXMLVehicle> vehicles = new PriorityQueue<ClassXMLVehicle>();
		vehicles.add(new ClassXMLVehicle("veh1", 2, "3->2"));
		vehicles.add(new ClassXMLVehicle("veh1", 3, "2->2"));
		vehicles.add(new ClassXMLVehicle("veh1", 1, "1->2"));
		
		while (vehicles.isEmpty() == false) {
			System.out.println(vehicles.poll());
		}
	}
}
