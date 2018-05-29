import java.util.LinkedList;
import java.util.PriorityQueue;


public class ClassAlgorithmDCFFSP implements InterfaceAlgorithmDC {
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
			}
			else 
			{
				// flaw: it is possible that the path with longer expected delay would meet delay constraint. 
				break;
			}
		}
		
		System.out.println("maximum flow paths:");
		for (ClassPath path : pathsResult)
		{
			System.out.println(path);
		}
		timeCalculation = System.currentTimeMillis() - timeCalculation;

	}
	
	public ClassPath augmentingPath(InterfaceNetwork network, int s, int t)
	{
		// Dijkstra
		ClassEdge[] edgeTo = new ClassEdge[network.V()];
		double[] distTo = new double[network.V()];
		LinkedList<Integer> pq = new LinkedList<Integer>();
		for(int v = 0; v < network.V(); v++)
		{
			distTo[v] = Double.POSITIVE_INFINITY;
		}
		distTo[s] = 0.0;
		pq.add(s);
		while(pq.isEmpty() == false)
		{
			int u = s;
			double minDistTo = Double.POSITIVE_INFINITY;
			for (int v : pq)
			{
				if (distTo[v] < minDistTo)
				{
					minDistTo = distTo[v];
					u = v;
				}
			}
			pq.remove(new Integer(u));
			
			// relax(network, u)
			Iterable<ClassEdge> edges = network.adj(u);
			for(ClassEdge edge : edges)
			{
				int w = edge.to();
				if (edge.residualCapacityTo(w) <= 0)
					continue;
				if(distTo[w] > distTo[u] + edge.delay())
				{
					distTo[w] = distTo[u] + edge.delay();
					edgeTo[w] = edge;
					if(pq.contains(w) == false)
						pq.add(w);
				}
			}
		}
		
		if (edgeTo[t] == null)
			return null;
		
		return new ClassPath(edgeTo, s, t); 
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
	
    public static void main(String[] args) {
    	String fileAddress = "C:/tools/eclipse/workspace/SUMOJavaDP/data/tinyFNMyDelayBeta1.txt";
        In in = new In(fileAddress);
        ClassNetworkDCNSG network = new ClassNetworkDCNSG(in);
		int s = 0;
		int t = network.V() - 1;
        StdOut.println(network);
		ClassAlgorithmDCFFSP maxflow = new ClassAlgorithmDCFFSP();
		maxflow.findMaximumFlow(network, s, t);
		StdOut.println("maximum flow: " + maxflow.value);
		StdOut.println(network);
    }
}
