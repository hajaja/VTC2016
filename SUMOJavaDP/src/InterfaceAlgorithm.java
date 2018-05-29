import java.util.LinkedList;
import java.util.PriorityQueue;


public interface InterfaceAlgorithm {
	public void findMaximumFlow(InterfaceNetwork network, int s, int t);
	public LinkedList<ClassPath> paths();
	public double flow();
	public double timeCalculation();
	public void reset();
}
