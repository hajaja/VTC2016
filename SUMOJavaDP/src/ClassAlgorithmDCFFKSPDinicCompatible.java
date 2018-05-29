import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


public class ClassAlgorithmDCFFKSPDinicCompatible implements InterfaceAlgorithmDC {
	private int s;
	private int t;
	private double value;
	private double timeCalculation;

	private LinkedList<ClassPath> paths = new LinkedList<ClassPath>();	

	@Override
	public void findMaximumFlow(InterfaceNetwork network, int s, int t) {
		timeCalculation = System.currentTimeMillis();
		// EK 
		double flowEK = 0;
		InterfaceAlgorithm maxflow = null;
		maxflow = new ClassAlgorithmDCFF();
		maxflow.findMaximumFlow(network, s, t);
		LinkedList<ClassPath> pathsCandidateEK = maxflow.paths();
		LinkedList<ClassPath> pathsEK = new LinkedList<ClassPath>();
		for (ClassPath path : pathsCandidateEK) {
			if(path.DCMet()) { 
				flowEK += path.flow();
				pathsEK.add(path);
			}
		}
		network.reset();
		
		// TODO Auto-generated method stub
		while (true) {
			System.out.println("new round");
			// Dinic algorithm 
			LinkedList<ClassPath> pathsCandidate = augmentingPaths(network, s, t);
			if (pathsCandidate == null){
				break;
			}
			if (pathsCandidate.size() == 0){
				break;
			}

			// try to add as many candidate paths as possible
			for (ClassPath path : pathsCandidate) {
				//System.out.println(path);
				if (network.addAugmentingPath(path) == true) {
					paths.add(path);
					value += path.flow();
				}
				else {
					//System.out.println("Error: in Compatible, the final paths are not able to be added to the network");
				}
			}
			System.out.println(value);
		}

		System.out.println("maximum flow paths:");
		for (ClassPath path : paths)
		{
			System.out.println(path);
		}
		
		// compare EK and Compatible
		if (flowEK > value) {
			value = flowEK;
			paths = pathsEK;
		}
		
		timeCalculation = System.currentTimeMillis() - timeCalculation;

	}

	public LinkedList<ClassPath> augmentingPaths(InterfaceNetwork network, int s, int t)
	{
		LinkedList<ClassPath> ret = new LinkedList<ClassPath>();
		int K = PARAMS.K;

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
		
		// filter those paths that do not meet delay constraint
		// KSP ordered by probability
		PriorityQueue<ClassPathAdaptorByProbability> kPathsByProbability = new PriorityQueue<ClassPathAdaptorByProbability>();
		double probabilityMax = Double.MIN_VALUE;
		while (kPaths.isEmpty() == false) {
			kPathsByProbability.add(new ClassPathAdaptorByProbability(kPaths.poll().path));
		}

		// select KSelected paths from kPathsByProbability
		LinkedList<ClassPath> pathsFiltered = new LinkedList<ClassPath>();
		int k = 0;
		for (ClassPathAdaptorByProbability path : kPathsByProbability){
			if (path.path.DCMet() == false) {
				break;
			}
			if (k >= K) {
				break;
			}
			pathsFiltered.add(path.path);
			k += 1;
		}
		
		//// find the combination
		// generate the compatible matrix
		int KSelected = pathsFiltered.size();
		
		ClassPath[] arrayKPaths = new ClassPath[KSelected];
		for (int n = 0; n < arrayKPaths.length; n++) {
			arrayKPaths[n] = pathsFiltered.poll();
			System.out.println(arrayKPaths[n].toString());
		}
		
		if (KSelected == 0) {
			return null;
		}
		else if (KSelected == 1) {
			ret = new LinkedList<ClassPath>();
			ret.add(arrayKPaths[0]);
			return ret;
		}
		
		boolean[][] matrixCompatible = new boolean[KSelected][KSelected];
		for (int i = 1; i < KSelected; i++) {
			for (int j = 0; j < i; j++) {
				matrixCompatible[i][j] = arrayKPaths[i].isCompatible(arrayKPaths[j], network);
			}
		}
	
		int KElements = PARAMS.KELEMENTS;
		ClassCombination combinationMax = null;
		LinkedList<ClassCombination> listCombination = new LinkedList<ClassCombination>();
		LinkedList<ClassCombination> listCombinationNew = new LinkedList<ClassCombination>();
		for (int kElements = 2; kElements <= KElements; kElements++) {
			// find k element set
			if (kElements == 2) {
				for (int i = 1; i < KSelected; i++) {
					for (int j = 0; j < i; j++) {
						if (matrixCompatible[i][j] == true) {
							listCombinationNew.add(new ClassCombination(arrayKPaths[i], arrayKPaths[j]));
						}
					}
				}
				if (listCombinationNew.isEmpty()) {
					System.out.println("no compatible paths");
					break;
				}
				else {
					combinationMax = listCombinationNew.getFirst();
				}
			}
			else {
				listCombination = listCombinationNew;
				listCombinationNew = new LinkedList<ClassCombination>();
				System.out.println("To be implemented");
			}
			
			// determine whether the k element set is compatible
			
		}

		if (combinationMax == null) {
			ret.add(arrayKPaths[0]);
		}
		else {
			for (ClassPath path : combinationMax.paths()) {
				ret.add(path);
			}
		}
		// add the left paths;
		for (int n = 0; n < arrayKPaths.length; n++) {
			ret.addLast(arrayKPaths[n]);
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
		
	/*
	public static void main(String[] args) {
		String fileAddress = "data/tinyFNMyDelayBeta1.txt";
		In in = new In(fileAddress);
		ClassNetworkDCNSG network = new ClassNetworkDCNSG(in);
		int s = 0;
		int t = network.V() - 1;
		StdOut.println(network);
		ClassAlgorithmDCFFKSPDinicCompatible maxflow = new ClassAlgorithmDCFFKSPDinicCompatible();
		maxflow.findMaximumFlow(network, s, t);
		StdOut.println("maximum flow: " + maxflow.value);
		StdOut.println(network);
	}
	*/
}
