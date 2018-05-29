import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ClassNetworkSG extends ClassNetwork {

	private Hashtable<String, Integer> junctionS2I = new Hashtable<String, Integer>();
	private Hashtable<Integer, String> junctionI2S = new Hashtable<Integer, String>();
	private Hashtable<ClassEdge, String> edgeE2S = new Hashtable<ClassEdge, String>();
	private Hashtable<String, ClassEdge> edgeS2E = new Hashtable<String, ClassEdge>();
	private Hashtable<Integer, ClassNode> nodes = new Hashtable<Integer, ClassNode>();

	public ClassNetworkSG(int V) {
		super(V);
		// TODO Auto-generated constructor stub
	}
	
	public ClassNetworkSG(ClassXMLReaderNet reader) {
        super(reader.numJunctions);
        Document document = reader.document;
	    NodeList list = document.getElementsByTagName("junction");
        // add nodes
        for (int i = 0; i < list.getLength(); i++)
        {
        	Element elementJunction = (Element) list.item(i);
        	if (elementJunction.getAttribute("type").equals("internal") == false)
        	{
        		String nameJunction = elementJunction.getAttribute("id");
        		junctionS2I.put(nameJunction, i);
        		junctionI2S.put(i, nameJunction);     
        		double x = Double.parseDouble(elementJunction.getAttribute("x"));
        		double y = Double.parseDouble(elementJunction.getAttribute("y"));
        		nodes.put(i, new ClassNode(x, y));
        	}
        }
        
        // add edges
        list = document.getElementsByTagName("edge");
        for (int i = 0; i < list.getLength(); i++)
        {
        	Element elementEdge = (Element) list.item(i);
        	if (elementEdge.hasAttribute("function") == false || (elementEdge.hasAttribute("function") == true && elementEdge.getAttribute("function").equals("normal")))
        	{
            	String edgeName = elementEdge.getAttribute("id");
            	String from = elementEdge.getAttribute("from");
            	String to = elementEdge.getAttribute("to");
        		
        		Set<String> laneNames = new HashSet<String>();
            	NodeList lanes = elementEdge.getElementsByTagName("lane");
            	int numLanes = lanes.getLength();
            	Element elementLane = (Element) lanes.item(0);
               	double edgeSpeed = Double.parseDouble(elementLane.getAttribute("speed")) * PARAMS.coefficientSpeedLimit;
            	double edgeLength = Double.parseDouble(elementLane.getAttribute("length"));
            	for (int nLane = 0; nLane < lanes.getLength(); nLane++) {
            		elementLane = (Element) lanes.item(nLane);
            		laneNames.add(elementLane.getAttribute("id"));
            	}
            	
            	//InterfaceStochasticity stochasticity = new ClassStochasticityTimeGama(PARAMS.shape, edgeLength / edgeSpeed);
            	//InterfaceStochasticity stochasticity = new ClassStochasticitySpeedGama(PARAMS.shape, edgeSpeed, edgeLength);
            	InterfaceStochasticity stochasticity = new ClassStochasticitySpeedUni(edgeSpeed, edgeLength);
            	String edgeType = elementEdge.getAttribute("type");
                ClassEdge edge = new ClassEdge(junctionS2I.get(from), junctionS2I.get(to), numLanes, stochasticity, edgeName, laneNames, edgeLength, edgeSpeed, edgeType);
                edgeS2E.put(edge.name(), edge);
                edgeE2S.put(edge, edge.name());
                addEdge(edge);
        	}
        }
        
        // read connection information
    	Hashtable<String, LinkedList<String>> hashtableConnection = new Hashtable<String, LinkedList<String>>();
    	Hashtable<String, String> hashtableConnectionType = new Hashtable<String, String>();
    	PARAMS.hashtableConnectionType = hashtableConnectionType;
	    list = document.getElementsByTagName("connection");
        for (int i = 0; i < list.getLength(); i++)
        {
        	Element elementJunction = (Element) list.item(i);
        	String edgeNameFrom = elementJunction.getAttribute("from");
        	String edgeNameTo = elementJunction.getAttribute("to");
        	String connectionType = elementJunction.getAttribute("state");
        	if (edgeNameFrom.startsWith(":") == false && edgeNameTo.startsWith(":") == false)
        	{
        		if (hashtableConnection.containsKey(edgeNameFrom)) {
        			hashtableConnection.get(edgeNameFrom).add(edgeNameTo);
        		}
        		else {
        			LinkedList<String> listEdgeNameTo = new LinkedList<String>();
        			hashtableConnection.put(edgeNameFrom, listEdgeNameTo);
        			hashtableConnection.get(edgeNameFrom).add(edgeNameTo);
        		}
        		
        		String connectionEdgeNamePair = edgeNameFrom + "-" + edgeNameTo;  
        		hashtableConnectionType.put(connectionEdgeNamePair, connectionType);
        	}
        }
        
        // replicate nodes if necessary
        
        replicateIfNecessary(hashtableConnection);
        replicateIfNecessary(hashtableConnection);
        replicateIfNecessary(hashtableConnection);    	
        replicateIfNecessary(hashtableConnection);    	

	}
	
	public void replicateIfNecessary(Hashtable<String, LinkedList<String>> hashtableConnection) {
		Hashtable<Integer, LinkedList<ClassEdge>> hashtableAdjReplicated = new Hashtable<Integer, LinkedList<ClassEdge>>();
    	Hashtable<Integer, String> hashtableI2SReplicated = new Hashtable<Integer, String>();
    	Hashtable<Integer, LinkedList<ClassEdge>> hashtableEdgesToBeRemoved = new Hashtable<Integer, LinkedList<ClassEdge>>();

    	int vNow = V();
        for (int v = 0; v < V(); v++) {
        	Iterable<ClassEdge> edges = adj(v);
        	int nVReplicated = 0;
        	for (ClassEdge edgeIn : edges) {
        		// iterate edges that end at node v, to determine whether this edge can reach every departing edge
        		if (edgeIn.to() != v) {
        			continue;
        		}
        		
        		String edgeInOriginalName = edgeIn.nameReplicated();
        		edgeInOriginalName = edgeInOriginalName.split("Replicated")[0];
    			if (hashtableConnection.containsKey(edgeInOriginalName) == false) {
    				continue;
    			}
    			
    			// iterate edges departing this node v, to determine whether can be reached by edge
    			boolean booleanNeedReplicate = false;
    			LinkedList<ClassEdge> edgesReachableByEdgeIn = new LinkedList<ClassEdge>();
    			for (ClassEdge edgeOut : edges) {
    				if (edgeOut.from() != v)
    					continue;
    				
    				String edgeOutOriginalName = edgeOut.nameReplicated();
    				edgeOutOriginalName = edgeOutOriginalName.split("Replicated")[0];
    					
    				if (hashtableConnection.get(edgeInOriginalName).contains(edgeOutOriginalName) == false) {
    					booleanNeedReplicate = true;
    					PARAMS.ps.print(edgeIn.name() + "->" + edgeOut.name() + '\n');
    				}
    				else {
    					edgesReachableByEdgeIn.add(edgeOut);
    				}
    			}
    			
    			// create replicated nodes
    			if (booleanNeedReplicate == true) {
    				// create new node vNow, store in hashtableI2SReplicated, 
    				// create new adj[vNow], store in hashtableAdjReplicated
    				String nodeReplicatedName = junctionI2S.get(v) + "Replicated_" + nVReplicated;
    				LinkedList<ClassEdge> listEdgesConnected = new LinkedList<ClassEdge>();
    				for (ClassEdge edgeOut: edgesReachableByEdgeIn) {
    					String nameNew = edgeOut.name() + "ReplicatedEdgeOutDueToNode_" + junctionI2S.get(v) + "_"+ nVReplicated;
    					ClassEdge edgeOutReplicated = new ClassEdge(edgeOut, vNow, edgeOut.to(), nameNew);	
    					listEdgesConnected.add(edgeOutReplicated);
    				}
    				String nameNew = edgeIn.name() + "ReplicatedEdgeInDueToNode_" + junctionI2S.get(v) + "_"+ nVReplicated;
    				ClassEdge edgeInReplicated = new ClassEdge(edgeIn, edgeIn.from(), vNow, nameNew);
    				listEdgesConnected.add(edgeInReplicated);
    				
    				hashtableAdjReplicated.put(vNow, listEdgesConnected);
    				hashtableI2SReplicated.put(vNow, nodeReplicatedName);
    				nVReplicated++;
    				vNow++;
    				
    				// collect edges to be remove from v
        			if (hashtableEdgesToBeRemoved.containsKey(v)) {
        				hashtableEdgesToBeRemoved.get(v).add(edgeIn);
        			}
        			else {
        				LinkedList<ClassEdge> listEdgesToBeRemoved = new LinkedList<ClassEdge>();
        				hashtableEdgesToBeRemoved.put(v, listEdgesToBeRemoved);
        				hashtableEdgesToBeRemoved.get(v).add(edgeIn);
        			}
    			}
    			
    		
        	}
        }
        
        // modify the original network
		// remove the edgeIn from the original adj[]
        Enumeration nodesChanged = hashtableEdgesToBeRemoved.keys();
        while (nodesChanged.hasMoreElements()){
        	int v = (int) nodesChanged.nextElement();
        	for (ClassEdge edgeToBeRemoved : hashtableEdgesToBeRemoved.get(v)) {
        		boolean booleanRemoveEdgeFromAdj = adj[v].remove(edgeToBeRemoved);
        		PARAMS.ps.println("edge " + edgeToBeRemoved.name() + " removed from node " + junctionI2S.get(v) + ":" + booleanRemoveEdgeFromAdj);
        		adj[edgeToBeRemoved.from()].remove(edgeToBeRemoved);
        	}
        }
        
        // check 
        for (int v = 0; v < V; v++) {
        	if (junctionI2S.get(v).equals("474459")) {
    			PARAMS.ps.println("checking 474459, v=" + v);
        		for (ClassEdge edge : adj[v]) {
        			PARAMS.ps.println(edge);
        		}
        	}
        }
        
		// update V in ClassNetwork 
        int VOld = V;
        V = vNow;

        // enlarge the original adj
        LinkedList<ClassEdge>[] adjEnlarged = (LinkedList<ClassEdge>[]) new LinkedList[V];
        for (int v = 0; v < VOld; v++) {
        	adjEnlarged[v] = adj[v];
        }
        adj = adjEnlarged;
        for (int v = VOld; v < V; v++) {
        	adj[v] = new LinkedList<ClassEdge>();
        }
        
        // add the adj information in hashtableAdjReplicated to adj[]
        for (int v = VOld; v < V; v++) {
        	for (ClassEdge edgeConnected : hashtableAdjReplicated.get(v)) {
        		/*
        		if (edgeConnected.name().equals("204001544")) {
        			PARAMS.ps.println("v=" + v + " " + hashtableI2SReplicated.get(v) + " caused addtition of 204001544");
        		}
        		if (edgeConnected.name().equals("204001548")) {
        			PARAMS.ps.println("v=" + v + " " + hashtableI2SReplicated.get(v) + " caused addtition of 204001548");
        		}
        		*/
        		addEdge(edgeConnected);
        	}
        }
		
		// insert into junctionS2I, junctionI2S, nodes
		for (int v = VOld; v < V; v++) {
			junctionI2S.put(v, hashtableI2SReplicated.get(v));
			junctionS2I.put(hashtableI2SReplicated.get(v), v);
		}

		// enlarge nodes
        for (int v = VOld; v < V; v++) {
        	String nodeReplicatedName = junctionI2S.get(v);
        	String nameSubStrings[] = nodeReplicatedName.split("Replicated");
        	String nodeOriginalName = nameSubStrings[0];
        	ClassNode nodeOriginal = nodes.get(junctionS2I.get(nodeOriginalName));
        	nodes.put(v, new ClassNode(nodeOriginal.x, nodeOriginal.y));
        }

        // check 
        for (int v = 0; v < V; v++) {
        	if (junctionI2S.get(v).equals("474459")) {
    			PARAMS.ps.println("checking 474459, v=" + v);
        		for (ClassEdge edge : adj[v]) {
        			PARAMS.ps.println(edge);
        		}
        	}
        }

	}
	
	public void updateNetwork(LinkedList<ClassEdge> edges, double[] coordinate){
		addVertex(edges);
		String name = coordinate[0] + "," + coordinate[1];
		int i = V() - 1;
		junctionS2I.put(name, i);
		junctionI2S.put(i, name);
		nodes.put(i, new ClassNode(coordinate[0], coordinate[1]));
	}
	
	public String VertexSymbol(int v) {
		return junctionI2S.get(v);
	}
	
	public Integer VertexInteger(String name) {
		return junctionS2I.get(name);
	}

	public Integer VertexFromEdgeInteger(String name) {
		return edgeS2E.get(name).from();
	}
	
	public Integer VertexToEdgeInteger(String name) {
		return edgeS2E.get(name).to();
	}
	
	public ClassNode nodeClass(int v) {
		return nodes.get(v);
	}
	
	/*
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
    	ClassXMLReaderNet reader = new ClassXMLReaderNet("data/quickstart.net.xml");
		ClassNetworkSG network = new ClassNetworkSG(reader);
        StdOut.println(network);
    }
    */

}
