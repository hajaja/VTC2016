import java.io.IOException;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


public class ClassAlgorithmDCFFKSP implements InterfaceAlgorithmDC {
	private int s;
	private int t;
	private double value;
	private double timeCalculation;

	private LinkedList<ClassPath> pathsResult = new LinkedList<ClassPath>();	
	
	@Override
	public void findMaximumFlow(InterfaceNetwork network, int s, int t) {
		timeCalculation = System.currentTimeMillis();

		// TODO Auto-generated method stub
		while(true)
		{
			ClassPath path = augmentingPath(network, s, t); 
			if (path == null)
				break;
			if (path.DCMet())
			{
				pathsResult.add(path);
				value += path.flow();
				network.addAugmentingPath(path);
				System.out.println(path);

			}
			else 
			{
				// flaw: it is possible that the path with longer expected delay would meet delay constraint. 
				break;
			}
		}
		
		System.out.println("maximum flow paths from " + s + " to " + t + ":");
		for (ClassPath path : pathsResult)
		{
			System.out.println(path);
		}
		
		timeCalculation = System.currentTimeMillis() - timeCalculation;

	}
	
	public ClassPath augmentingPath(InterfaceNetwork network, int s, int t)
	{
		int K = PARAMS.K;
		int[] count = new int[network.V()];
		PriorityQueue<ClassPathAdaptorByDelay> pq = new PriorityQueue<ClassPathAdaptorByDelay>(network.V());
		PriorityQueue<ClassPathAdaptorByDelay> kPaths = new PriorityQueue<ClassPathAdaptorByDelay>();
		for(int i = 0; i < network.V(); i++)
		{
 			count[i] = 0;
		}
		
		ClassPathAdaptorByDelay pathPopped;
		boolean switchFirstIterationIndicator = true;
		while((pq.isEmpty() == false && count[t] < K) 
				|| switchFirstIterationIndicator == true)
		{
			int u;
			if (switchFirstIterationIndicator == true) {
				switchFirstIterationIndicator = false;
				pathPopped = new ClassPathAdaptorByDelay(new ClassPath());
				u = s;
			}
			else {
				pathPopped = pq.poll();
				u = pathPopped.path.edges.getLast().to(); 
			}
			
			count[u]++;
			if (u == t)
			{
				kPaths.add(pathPopped);
			}
			if (count[u] <= K)
			{
				for (ClassEdge edge : network.adj(u))
				{
					int v = edge.to();
					if (edge.from() != u)
						continue;
					if (edge.residualCapacityTo(v) <= 0)
						continue;
					ClassPathAdaptorByDelay pathToVNew = new ClassPathAdaptorByDelay(new ClassPath(pathPopped.path, edge));
					if (pathToVNew.path.containsCircle()) {
						continue;
					}
					pq.add(pathToVNew);
				}
			}
		}
		
		if (kPaths.size() == 0)
			return null;
		
		ClassPath ret = kPaths.peek().path;
		double probabilityMax = Double.MIN_VALUE;
		System.out.println("ksp");
		while (kPaths.isEmpty() == false) {
			ClassPath path = kPaths.poll().path;
			System.out.println(path);
			double probabilityTemp = path.probabilityOfDelay(PARAMS.delayMaximum);
			if (probabilityTemp > probabilityMax) {
				probabilityMax = probabilityTemp;
				ret = path;
			}
		}
		return ret;
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
    	/*
    	String fileAddress = "data/tinyFNMyDelayBeta1.txt";
        In in = new In(fileAddress);
        ClassNetworkDCNSG network = new ClassNetworkDCNSG(in);
        int s = 0;
		int t = network.V() - 1;
        */
    	
    	/*
		ClassXMLReader reader = new ClassXMLReader("data/quickstart.net.xml");
		ClassNetworkSG network = new ClassNetworkSG(reader);
		int s = network.VertexInteger("91");
		int t = network.VertexInteger("1");
		*/
		
    	ClassXMLReaderNet reader = new ClassXMLReaderNet("data/Singapore.net.xml");
		ClassNetworkSG network = new ClassNetworkSG(reader);
		int s = network.VertexFromEdgeInteger("44818682#1");
		int t = network.VertexFromEdgeInteger("33793661#1");
		
        StdOut.println(network);
		ClassAlgorithmDCFFKSP maxflow = new ClassAlgorithmDCFFKSP();
		maxflow.findMaximumFlow(network, s, t);
		StdOut.println("maximum flow: " + maxflow.value);
		//StdOut.println(network);
    }
}
