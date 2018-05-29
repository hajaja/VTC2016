import java.util.LinkedList;
import java.util.ListIterator;

public class ClassPath {
	protected double delay = 0;
	private double flow = 0;
	public LinkedList<ClassEdge> edges = new LinkedList<ClassEdge>();
	private double[] pdf = new double[PARAMS.timeMaximum];
	private double[] cdf = null;
	
	private boolean containsCircle = false;
	
	public ClassPath() {
		flow = Double.MAX_VALUE;
	}
	
	public ClassPath(ClassPath path) {
		this.delay = path.delay;
		this.flow = path.flow;
		for (ClassEdge e : path.edges)
		{
			edges.offer(e);
		}
		for (int t = 0; t < PARAMS.timeMaximum; t++){
			this.pdf[t] = path.pdf[t];
			this.cdf[t] = path.cdf[t];
		}
	}
	
	public ClassPath(ClassPath path, ClassEdge edge) {
		for (ClassEdge e : path.edges)
		{
			this.edges.offer(e);
		}
		
		if (edges.contains(edge))
			containsCircle = true;
		
		if (edge != null)
		{
			this.edges.offer(edge);
			this.delay = path.delay + edge.delay();
			this.flow = Math.min(path.flow(), edge.residualCapacityTo(edge.to()));
		}
		else
		{
			this.delay = path.delay;
		}
	}
	
	public ClassPath(ClassEdge[] edgeTo, int s, int t)
	{
		flow = edgeTo[t].residualCapacityTo(t);
		for (int v = t; v != s; v = edgeTo[v].other(v))
		{
			ClassEdge edge = edgeTo[v];
			edges.push(edge);
			delay += edgeTo[v].delay();
			flow = Math.min(edgeTo[v].residualCapacityTo(v), flow);
		}
	}
	
	public void calculateCDF() {
		if (cdf != null) {
			return;
		}
		cdf = new double[PARAMS.timeMaximum];
	    ListIterator iter = edges.listIterator(edges.size());
	    ClassEdge edge;
	    ClassEdge edgeNext = null;
	    while (iter.hasPrevious()) {
	    	edge = (ClassEdge) iter.previous();
	    	//PARAMS.ps.println(edge.showStochasticity());
			if (edge.equals(edges.getLast())) {
				// cdf = edge.cdf(); //error was here. assigning this way would actually change the edge.cdf forever
				for (int t = 0; t < PARAMS.timeMaximum; t++) {
					cdf[t] = edge.cdf()[t];
				}
				edgeNext = edge;
			}
			else {
				// int delayByAcceleration = (int) (Math.max(0, (edgeNext.speed() - edge.speed())) / PARAMS.vehicleAcceleration);
				int delayByAcceleration = (int) (Math.ceil( (Math.abs(edgeNext.speed() - edge.speed()) / PARAMS.vehicleAcceleration) ) / 0.998);
				
				// check whether the connection is controlled by yellow light
        		String connectionEdgeNamePair = edge.name() + "-" + edgeNext.name();
        		if (PARAMS.hashtableConnectionType.containsKey(connectionEdgeNamePair)) {
        			String connectionType = PARAMS.hashtableConnectionType.get(connectionEdgeNamePair);
        			if (connectionType.equals("o")) {
        				int delayByYellowLight = (int) Math.ceil(
        						Math.abs(edgeNext.speed() - PARAMS.speedPassingYellow) / PARAMS.vehicleAcceleration
        						+ Math.abs(edge.speed() - PARAMS.speedPassingYellow) / PARAMS.vehicleAcceleration
        						);
        				delayByAcceleration += delayByYellowLight;
        			}
        		}

				PARAMS.ps.println("delayByAcceleration=" + delayByAcceleration);
				for (int t = cdf.length - 1; t >= delayByAcceleration; t--) {
					cdf[t] = cdf[t - delayByAcceleration];
				}
				for (int t = 0; t < delayByAcceleration; t++) {
					cdf[t] = 0;
				}
				cdf = convolution(edge.pdf(), cdf);
				edgeNext = edge;
			}
	    }
	}
	
	public boolean DCMet() {
		// TODO Auto-generated method stub
		return probabilityOfDelay(PARAMS.delayMaximum) > PARAMS.probabilityMinimum;
	}
	
	public boolean containsCircle() {
		// TODO Auto-generated method stub
		return containsCircle;
	}
	
	private double[] convolution(double[] pdf, double[] cdf) {
		double ret[] = new double[pdf.length];
		for (int k = 0; k < pdf.length; k++)
		{
			double wTemp = 0;
			for (int i = 0; i <= k; i++)
			{
				wTemp += pdf[i] * cdf[k-i];
			}
			ret[k] = wTemp;
		}
		
		double sumTemp = 0;
		for (int i = 0; i < ret.length; i++)
			sumTemp += pdf[i];
		for (int i = 0; i < ret.length; i++)
			ret[i] = ret[i] / sumTemp;
		return ret;
	}
	
	public double probabilityOfDelay(double delay) {
		calculateCDF();
		return cdf[(int)delay];
	}
	
	public double delayAtProbability(double probability) {
		int ret = PARAMS.timeMaximum;
		for (int t = 0; t < PARAMS.timeMaximum; t++) {
			if (cdf[t] > probability) {
				return t;
			}
		}
		System.out.println("Warning: in delayAtProbability, return timeMaximum");
		return ret;
	}
	
	public double delay() {
		return delay;
	}
	
	public double flow() {
		return flow;
	}
	
	public boolean contains(ClassEdge edge) {
		// TODO Auto-generated method stub
		for (ClassEdge e : edges) {
			if (e.equals(edge)) 
				return true;
		}
		return false;
	}
	
    public String showStochasticity() {
    	String ret = "";
    	
    	//ret += "cdf:\n";
    	for (int t = 0; t < PARAMS.timeMaximum; t++) {
    		ret += cdf[t];
    		if (t != PARAMS.timeMaximum - 1)
    			ret += ",\t";
    		else
    			ret += "\n";
    	}
    	/*
    	ret += "pdf:\n";
    	
    	for (int t = 0; t < PARAMS.timeMaximum; t++) {
    		ret += pdf[t] + ",\t";
    	}
    	*/
    	return ret;
    }
    
    public boolean isCompatible(ClassPath path, InterfaceNetwork network) {
    	boolean ret = true;
    	for (ClassEdge eThis : edges) {
    		for (ClassEdge e : path.edges) {
    			if (eThis.name().equals(e.name())) {
    				ClassEdge edge = network.edge(eThis.from(), eThis.to());
    				if (edge.residualCapacityTo(eThis.to()) < path.flow + this.flow) {
    					/*
    					System.out.println(eThis);
    					System.out.println(e);
    					System.out.println(edge);
    					*/
    					ret = false;
    					return ret;
    				}
    			}
    		}
    	}
    	return ret;
    }
	
	public String toString() {
		String ret = "";
		ret += "delay:\t" + delay + "\t" + "flow:\t" + flow + "\t" + "probability:\t" + probabilityOfDelay(PARAMS.delayMaximum) + "\t";
		//ret += "delay:\t" + delay + "\t" + "flow:\t" + flow + "\t" + "probability:\t";
		for (ClassEdge e : edges) {
			ret += e.from() + "-" + e.name() + "-" + e.residualCapacityTo(e.to()) + "/" + e.capacity() + "coef=" + e.coefficientPrioritizedJunction() + "->";
			if (e.equals(edges.getLast()))
				ret += e.to();
		}
		return ret;
	}
}
