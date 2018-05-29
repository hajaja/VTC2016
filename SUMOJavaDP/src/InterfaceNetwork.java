
public interface InterfaceNetwork {
	public int V();
	public void addEdge(ClassEdge e);
	public Iterable<ClassEdge> adj(int v);
	public ClassEdge edge(int from, int to);
	public String toString();
	public boolean addAugmentingPath(ClassPath augmentingPath);
	public void reset();
}
