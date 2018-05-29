import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


public class ClassAlgorithmDCFFKSPDinic implements InterfaceAlgorithmDC {
	private int s;
	private int t;
	private double value;
	private double timeCalculation;

	private LinkedList<ClassPath> paths = new LinkedList<ClassPath>();	

	@Override
	public void findMaximumFlow(InterfaceNetwork network, int s, int t) {
		timeCalculation = System.currentTimeMillis();
		// TODO Auto-generated method stub
		while (true) {
			LinkedList<ClassPath> pathsCandidate = augmentingPaths(network, s, t);
			if (pathsCandidate == null){
				break;
			}
			if (pathsCandidate.size() == 0){
				break;
			}

			// try to add as many candidate paths as possible
			for (ClassPath path : pathsCandidate) {
				System.out.println(path);
				if (network.addAugmentingPath(path) == true) {
					paths.add(path);
					value += path.flow();
				}
			}
		}

		System.out.println("maximum flow paths:");
		for (ClassPath path : paths)
		{
			System.out.println(path);
		}
		timeCalculation = System.currentTimeMillis() - timeCalculation;
	}

	public LinkedList<ClassPath> augmentingPaths(InterfaceNetwork network, int s, int t)
	{
		int K = PARAMS.K;
		int KSelected = K;

		// KSP collected in kPaths
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

		// KSP ordered by probability
		PriorityQueue<ClassPathAdaptorByProbability> kPathsByProbability = new PriorityQueue<ClassPathAdaptorByProbability>();
		double probabilityMax = Double.MIN_VALUE;
		while (kPaths.isEmpty() == false) {
			kPathsByProbability.add(new ClassPathAdaptorByProbability(kPaths.poll().path));
		}

		// select KSelected paths from kPathsByProbability
		// TODO, modify code to prevent: two selected paths use the same link. You can set KSelected=2, and use tinyFNMyDelayBeta1.txt

		LinkedList<ClassPath> ret = new LinkedList<ClassPath>();
		int k = 0;
		for (ClassPathAdaptorByProbability path : kPathsByProbability){
			if (path.path.DCMet() == false) {
				break;
			}
			if (k >= KSelected) {
				break;
			}
			ret.add(path.path);
			k += 1;
		}

		return ret;
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
		String fileAddress = "data/tinyFNMyDelayBeta1.txt";
		In in = new In(fileAddress);
		ClassNetworkDCNSG network = new ClassNetworkDCNSG(in);
		int s = 0;
		int t = network.V() - 1;
		StdOut.println(network);
		ClassAlgorithmDCFFKSPDinic maxflow = new ClassAlgorithmDCFFKSPDinic();
		maxflow.findMaximumFlow(network, s, t);
		StdOut.println("maximum flow: " + maxflow.value);
		StdOut.println(network);
	}
}
