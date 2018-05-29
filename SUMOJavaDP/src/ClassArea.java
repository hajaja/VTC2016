import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;


public class ClassArea {
	ClassNetworkSG network;
	double[] coordinate;
	double radius;
	int indicator;
	int virtualNode;
	LinkedList<ClassEdge> edges;
	private Hashtable<String, ClassEdge> dictEdges = new Hashtable<String, ClassEdge>();

	public ClassArea(ClassNetworkSG network, double[] coordinate, double radius, int indicator){
		this.network = network;
		this.coordinate = coordinate;
		this.radius = radius;
		this.indicator = indicator;
		this.virtualNode = network.V(); //add one node
		edges = new LinkedList<ClassEdge>();
		findEdgesConnectedToVirtualNode();
	}

	public void findEdgesConnectedToVirtualNode(){
		// iterate all nodes, to get all the terminals
		for (int v = 0; v < network.V(); v++) {
			if (calculateDistance(v) > radius) {
				continue;
			}
			
			LinkedList<ClassEdge> adj = (LinkedList<ClassEdge>) network.adj(v);
			// v must not be the terminal
			if (adj.size() <= 1) {
				continue;
			}
			
			for (ClassEdge e: adj) {
				if (e.isLink()) {
					// other(v) must be terminal
					LinkedList<ClassEdge> adjTerminal = (LinkedList<ClassEdge>) network.adj(e.other(v));
					if (adjTerminal.size() > 1) {
						continue;
					}
					
					// for origin, the edge must be towards v
					if (indicator == 0) {
						if (e.from() == v) {
							continue;
						}
					}
					// for destination, the edge must be departing v
					else if (indicator == 1) {
						if (e.to() == v) {
							continue;
						}
					}
					else {
						System.out.println("Error: ClassArea, incorrect indicator value: " + indicator);
						System.exit(0);
					}
					
					ClassEdge edge = null;
					String edgeName;
					edgeName = "virtualEdge_" + indicator + "_" + e.name(); 
					ClassStochasticitySpeedUni stochasticity = new ClassStochasticitySpeedUni(PARAMS.speedDefault, 1);
					Set<String> laneNames = new TreeSet<String>();
					laneNames.add("virtualLane");

					int nodeConnected;
					if (indicator == 0) {
						if (e.to() == v) 
							nodeConnected = e.from();
						else
							continue;
						edge = new ClassEdge(network.V(), nodeConnected, PARAMS.virtualEdgeCapacity, stochasticity, edgeName, laneNames, 0, PARAMS.speedDefault, "virtual");
						dictEdges.put(edge.name(), e);
					}
					else if(indicator == 1) {
						if (e.from() == v) 
							nodeConnected = e.to();
						else
							continue;
						edge = new ClassEdge(nodeConnected, network.V(), PARAMS.virtualEdgeCapacity, stochasticity, edgeName, laneNames, 0, PARAMS.speedDefault, "virtual");
						dictEdges.put(edge.name(), e);

					}
					else {
						System.out.println("Error: ClassArea: findEdgesConnectedToVirtualNode");
						System.exit(0);
					}
					edges.add(edge);
				}
			}
		}

		// modify the network, add the virtual node and add all of its adj links
		network.updateNetwork(edges, coordinate);
		
		// modify all the _link edges' length to 1
		for (ClassEdge e : edges) {
			ClassEdge edge = findLinkEdgeFromVirtualEdgeName(e.name());
			edge.setLinkEdge();
		}
		
	}

	private double calculateDistance(int v) {
		// TODO Auto-generated method stub
		ClassNode nodeClass = network.nodeClass(v);
		double x = nodeClass.x;
		double y = nodeClass.y;
		double distance = 0;
		distance += Math.pow(coordinate[0] - x, 2);
		distance += Math.pow(coordinate[1] - y, 2);
		distance = Math.sqrt(distance);
		//System.out.println(coordinate[0] + "," + coordinate[1] + "," + x + "," + y + "," + distance);
		return distance; 
	}
	
	public ClassEdge findLinkEdgeFromVirtualEdgeName(String name) {
		return dictEdges.get(name);
	}
}
