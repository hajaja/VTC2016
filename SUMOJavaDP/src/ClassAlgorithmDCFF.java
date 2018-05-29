import java.io.IOException;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


public class ClassAlgorithmDCFF implements InterfaceAlgorithmDC{
	private int s;
	private int t;
	private double value;
	public boolean switchTraditionalFF = false;
	private double timeCalculation;
	
	private LinkedList<ClassPath> pathsResult = new LinkedList<ClassPath>();	
	
	@Override
	public void findMaximumFlow(InterfaceNetwork network, int s, int t) {
		timeCalculation = System.currentTimeMillis();
		
		this.s = s;
		this.t = t;
		ClassPath augmentingPath = augmentingPath(network, s, t); 
		LinkedList<ClassPath> paths = new LinkedList<ClassPath>();
		while(augmentingPath != null)
		{
			network.addAugmentingPath(augmentingPath);
			paths.add(augmentingPath);
			augmentingPath = augmentingPath(network, s, t);
		}
		
		for (ClassPath path : paths) {
			if (path.DCMet() || switchTraditionalFF) {
				pathsResult.add(path);
				value = value + path.flow();
			}
		}
		
		System.out.println("maximum flow: " + value + "\tfrom " + s + " to " + t);
		for (ClassPath path : pathsResult) {
			System.out.println(path);
		}
		
		timeCalculation = System.currentTimeMillis() - timeCalculation;
 
	}
	
	private ClassPath augmentingPath(InterfaceNetwork network, int s, int t) {
		// BFS 
		boolean[] marked = new boolean[network.V()];
		ClassEdge[] edgeTo = new ClassEdge[network.V()];
		LinkedList<Integer> queue = new LinkedList<Integer>();

		marked[s] = true;
		queue.addLast(s);
		while(queue.isEmpty() == false)
		{
			int v = queue.removeFirst();
			for (ClassEdge e : network.adj(v))
			{
				int w = e.other(v);
				if (e.residualCapacityTo(w) > 0 && marked[w] == false)
				{
					edgeTo[w] = e;
					marked[w] = true;
					queue.addLast(w);
				}
			}
		}
		
		if (marked[t] == true) {
			return new ClassPath(edgeTo, s, t);
		}
		else {
			return null;
		}
	}
	
	public LinkedList<ClassPath> paths(){
		return pathsResult;
	}
	
	public double flow() {
		return value;
	}
	
	public double timeCalculation() {
		return timeCalculation;
	}
	
	public void reset() {
		value = 0;		
		timeCalculation = 0;
		pathsResult = new LinkedList<ClassPath>();
	}
	
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
    	
    	String fileAddress = "data/tinyFNMyDelayBeta1.txt";
        In in = new In(fileAddress);
        ClassNetworkDCNSG network = new ClassNetworkDCNSG(in);
        int s = 0;
		int t = network.V() - 1;
        
		/*
    	ClassXMLNetReader reader = new ClassXMLNetReader("data/Singapore.net.xml");
		ClassNetworkSG network = new ClassNetworkSG(reader);
		int s = network.VertexInteger("91");
		int t = network.VertexInteger("1");
		*/
        StdOut.println(network);
		ClassAlgorithmDCFF maxflow = new ClassAlgorithmDCFF();
		maxflow.findMaximumFlow(network, s, t);
		System.out.println(network);
    }
}
