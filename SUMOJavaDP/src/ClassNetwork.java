import java.util.LinkedList;

public class ClassNetwork implements InterfaceNetwork{
	protected int V;
	protected int E;
	protected LinkedList<ClassEdge>[] adj;
    
    public ClassNetwork (int V){
        if (V < 0) throw new IllegalArgumentException("Number of vertices in a Graph must be nonnegative");
        this.V = V;
        adj = (LinkedList<ClassEdge>[]) new LinkedList[V];
        for (int v = 0; v < V; v++)
            adj[v] = new LinkedList<ClassEdge>();
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
    }

	@Override
	public int V() {
		// TODO Auto-generated method stub
		return V;
	}

	@Override
	public void addEdge(ClassEdge e) {
		// TODO Auto-generated method stub
        int v = e.from();
        int w = e.to();
        validateVertex(v);
        validateVertex(w);
        adj[v].add(e);
        adj[w].add(e);
	}

	@Override
	public Iterable<ClassEdge> adj(int v) {
		// TODO Auto-generated method stub
		return adj[v];
	}

	@Override
	public ClassEdge edge(int from, int to) {
		// TODO Auto-generated method stub
		for (ClassEdge edge : adj(from))
		{
			if (edge.from() == from && edge.to() == to)
				return edge;
		}
		throw new IllegalArgumentException("no edge from " + from + " to " + to);
	}
	
	public void addVertex(LinkedList<ClassEdge> edges) {
		this.V = this.V + 1;
		LinkedList<ClassEdge>[] adjTemp = (LinkedList<ClassEdge>[]) new LinkedList[V];
		for (int v = 0; v < V - 1; v++) {
			adjTemp[v] = adj[v];
		}
		adjTemp[V-1] = new LinkedList<ClassEdge>();
		for (ClassEdge e : edges) {
			System.out.println(e);
			adjTemp[V-1].add(e);
			if (e.from() == V-1) {
				adjTemp[e.to()].add(e);
			}
			else {
				adjTemp[e.from()].add(e);
			}
		}
		adj = adjTemp;
	}
	
	@Override
	public boolean addAugmentingPath(ClassPath path) {
		// TODO Auto-generated method stub
		if (tryToAddAugmentingPath(path) == false) {
			return false;
		}
		
		for (ClassEdge e : path.edges) {
			e.addResidualFlowTo(e.to(), path.flow());
		}
		return true;
	}
	
	public boolean tryToAddAugmentingPath(ClassPath path) {
		// TODO Auto-generated method stub
		boolean ret = false;
		for (ClassEdge e : path.edges) {
			ret = e.isAbleToAddResidualFlowTo(e.to(), path.flow());
			if (ret == false) {
				break;
			}
		}
		return ret;
	}
	
	public void reset() {
		for (int v = 0; v < V; v++) {
			for (ClassEdge e: adj[v]) {
				e.reset();
			}
		}
		
	}
	
	public String toString(){
		String s = "";
        s += V + " " + E + "\n";
        for (int v = 0; v < V; v++) {
            s += v + ":  ";
            for (ClassEdge e : adj[v]) {
                if (e.to() != v) 
                	s += e + "\t";
            }
            s += "\n";
        }
        return s.toString() + "\n";
	}

}
