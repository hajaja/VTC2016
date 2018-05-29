import java.util.LinkedList;
import java.util.PriorityQueue;


public class ClassAlgorithmNCFF implements InterfaceAlgorithmNC{
	private int s;
	private int t;
	private double value;
	private double timeCalculation;

	private LinkedList<ClassPath> paths = new LinkedList<ClassPath>();

	@Override
	public void findMaximumFlow(InterfaceNetwork network, int s, int t) {
		timeCalculation = System.currentTimeMillis();

		// TODO Auto-generated method stub
		this.s = s;
		this.t = t;
		ClassPath augmentingPath = augmentingPath(network, s, t); 
		while(augmentingPath != null)
		{
			double minResidualCapacity = augmentingPath.flow();
			network.addAugmentingPath(augmentingPath);
			value = value + minResidualCapacity;
			paths.add(augmentingPath);
			augmentingPath = augmentingPath(network, s, t); 
		}
		
		for (ClassPath path : paths) {
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
		return paths;
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
		paths = new LinkedList<ClassPath>();
	}
	
	public static void main(String[] args) {
    	String fileAddress = "C:/tools/eclipse/workspace/SUMOJavaDP/data/tinyFNMy.txt";
		ClassNetworkNCNSG network = new ClassNetworkNCNSG(new In(fileAddress));
		int s = 0;
		int t = network.V() - 1;
		StdOut.println(network);
		ClassAlgorithmNCFF maxflow = new ClassAlgorithmNCFF();
		maxflow.findMaximumFlow(network, s, t);
		StdOut.println("maximum flow: " + maxflow.value);
		StdOut.println(network);
	}
}
