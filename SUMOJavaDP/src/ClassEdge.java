/*************************************************************************
 *  Compilation:  javac FlowEdge.java
 *  Execution:    java FlowEdge
 *
 *  Capacitated edge with a flow in a flow network.
 *
 *************************************************************************/

/**
 *  The <tt>FlowEdge</tt> class represents a capacitated edge with a 
  * flow in a {@link FlowNetwork}. Each edge consists of two integers
 *  (naming the two vertices), a real-valued capacity, and a real-valued
 *  flow. The data type provides methods for accessing the two endpoints
 *  of the directed edge and the weight. It also provides methods for
 *  changing the amount of flow on the edge and determining the residual
 *  capacity of the edge.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/64maxflow">Section 6.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;

public class ClassEdge {

    private final int v;             // from
    private final int w;             // to 
    private double capacity;   // capacity
    private double flow;             // flow
    
    private double delay = 0;
    private double[] pdf = new double[PARAMS.timeMaximum];
    private double[] cdf = new double[PARAMS.timeMaximum];
    private String name = "noname";
    private String nameReplicated = "noname";
    private double speed = PARAMS.speedDefault;
    private double length = 0;
    
    private Set<String> laneNames = new HashSet<String>();
    
    private InterfaceStochasticity stochasticity;
    
    private String edgeType;
    
    private double coefficientPrioritizedJunction = 1.0;

    /**
     * Initializes an edge from vertex <tt>v</tt> to vertex <tt>w</tt> with
     * the given <tt>capacity</tt> and zero flow.
     * @param v the tail vertex
     * @param w the head vertex
     * @param capacity the capacity of the edge
     * @throws java.lang.IndexOutOfBoundsException if either <tt>v</tt> or <tt>w</tt>
     *    is a negative integer
     * @throws java.lang.IllegalArgumentException if <tt>capacity</tt> is negative
     */
    public ClassEdge(int v, int w, double capacity) {
        if (v < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
        if (w < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
        if (!(capacity >= 0.0)) throw new IllegalArgumentException("Edge capacity must be nonnegaitve");
        this.v         = v;
        this.w         = w;  
        this.capacity  = capacity;
        this.flow      = 0.0;
        
        for (int t = 0; t < PARAMS.timeMaximum; t++){
        	pdf[t] = 0;
        	cdf[t] = 1;
        }
        pdf[0] = 1;
        delay = 0;
    }


    public ClassEdge(int v, int w, double capacity, InterfaceStochasticity stochasticity, String name, Set<String> laneNames, double length, double speed, String edgeType) {
        if (v < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer"); 
        if (w < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative integer");
        if (!(capacity >= 0.0))  throw new IllegalArgumentException("Edge capacity must be nonnegaitve");
        this.v         = v;
        this.w         = w;  
        this.capacity  = capacity;
        this.flow      = 0.0;
        
    	pdf = stochasticity.pdf();
    	cdf = stochasticity.cdf();
    	delay = stochasticity.delayExpected();
        if (!(delay >= 0)) throw new IllegalArgumentException("Delay must be non negative");
        this.name = name;
        this.nameReplicated = new String(name);
        this.length = length;
        this.speed = speed;
        
        this.stochasticity = stochasticity;
        if (laneNames != null) {
	        for (String laneName : laneNames) {
	        	this.laneNames.add(laneName);
	        }
        }
        
        this.edgeType = edgeType;
    }

    public ClassEdge(ClassEdge edge, int vNew, int wNew, String nameNew){
    	v = vNew;             // from
        w = wNew;             // to 
        capacity = edge.capacity;   // capacity
        flow = edge.flow;             // flow
        delay = edge.delay;
        
        pdf = new double[PARAMS.timeMaximum];
        cdf = new double[PARAMS.timeMaximum];
        
        for (int i = 0; i < pdf.length; i++) {
        	pdf[i] = edge.pdf[i];
        	cdf[i] = edge.cdf[i];
        }
        name = edge.name();
        nameReplicated = nameNew;
        speed = edge.speed;
        length = edge.length;
        
        
        laneNames = new HashSet<String>();
        for (String laneName : edge.laneNames) {
        	laneNames.add(laneName);
        }
        
        stochasticity = new ClassStochasticity(edge.stochasticity);
        edgeType = edge.edgeType;
        coefficientPrioritizedJunction = edge.coefficientPrioritizedJunction;
    }
    
    /**
     * Returns the tail vertex of the edge.
     * @return the tail vertex of the edge
     */
    public int from() {
        return v;
    }  

    /**
     * Returns the head vertex of the edge.
     * @return the head vertex of the edge
     */
    public int to() {
        return w;
    }  
 
    /**
     * Returns the capacity of the edge.
     * @return the capacity of the edge
     */
    public double capacity() {
        return capacity;
    }

    /**
     * Returns the flow on the edge.
     * @return the flow on the edge
     */
    public double flow() {
        return flow;
    }
    
    /**
     * Returns the delay on the edge.
     * @return the delay on the edge
     */
    public double delay() {
        return delay;
    }
    
    public double[] pdf()
    {
    	return pdf;
    }
    
    public double[] cdf()
    {
    	return cdf;
    }
    
    public String name() {
        return name;
    }  
    
    public String nameNoSharp() {
    	
        return name.replace('#', 's');
    }  
    
    public String nameReplicated() {
        return nameReplicated;
    }  
    
    public Set<String> laneNames() {
    	return laneNames;
    }
    
    public double length(){
    	return length;
    }

    public double speed(){
    	return speed;
    }
    /**
     * Returns the endpoint of the edge that is different from the given vertex
     * (unless the edge represents a self-loop in which case it returns the same vertex).
     * @param vertex one endpoint of the edge
     * @return the endpoint of the edge that is different from the given vertex
     *   (unless the edge represents a self-loop in which case it returns the same vertex)
     * @throws java.lang.IllegalArgumentException if <tt>vertex</tt> is not one of the endpoints
     *   of the edge
     */
    public int other(int vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new IllegalArgumentException("Illegal endpoint");
    }

    /**
     * Returns the residual capacity of the edge in the direction
     *  to the given <tt>vertex</tt>.
     * @param vertex one endpoint of the edge
     * @return the residual capacity of the edge in the direction to the given vertex
     *   If <tt>vertex</tt> is the tail vertex, the residual capacity equals
     *   <tt>capacity() - flow()</tt>; if <tt>vertex</tt> is the head vertex, the
     *   residual capacity equals <tt>flow()</tt>.
     * @throws java.lang.IllegalArgumentException if <tt>vertex</tt> is not one of the endpoints
     *   of the edge
     */
    public double residualCapacityTo(int vertex) {
        if      (vertex == v) return flow * coefficientPrioritizedJunction;              // backward edge
        else if (vertex == w) return (capacity - flow) * coefficientPrioritizedJunction;   // forward edge
        else throw new IllegalArgumentException("Illegal endpoint");
    }

    /**
     * Increases the flow on the edge in the direction to the given vertex.
     *   If <tt>vertex</tt> is the tail vertex, this increases the flow on the edge by <tt>delta</tt>;
     *   if <tt>vertex</tt> is the head vertex, this decreases the flow on the edge by <tt>delta</tt>.
     * @param vertex one endpoint of the edge
     * @throws java.lang.IllegalArgumentException if <tt>vertex</tt> is not one of the endpoints
     *   of the edge
     * @throws java.lang.IllegalArgumentException if <tt>delta</tt> makes the flow on
     *   on the edge either negative or larger than its capacity
     * @throws java.lang.IllegalArgumentException if <tt>delta</tt> is <tt>NaN</tt>
     */
    public void addResidualFlowTo(int vertex, double delta) {
    	delta = delta / coefficientPrioritizedJunction;
    	
        if      (vertex == v) flow -= delta;           // backward edge
        else if (vertex == w) flow += delta;           // forward edge
        else throw new IllegalArgumentException("Illegal endpoint");
        if (Double.isNaN(delta)) throw new IllegalArgumentException("Change in flow = NaN");
        if (!(flow >= 0.0))      throw new IllegalArgumentException("Flow is negative");
        if (!(flow <= capacity)) throw new IllegalArgumentException("Flow exceeds capacity");
    }
    
    public boolean isAbleToAddResidualFlowTo(int vertex, double delta) {
    	delta = delta / coefficientPrioritizedJunction;

        if      (vertex == v) {
        	if (flow - delta < 0)
        		return false;
        	else
        		return true;// backward edge
        }
        else if (vertex == w) {
        	if (flow + delta > capacity)
        		return false;
        	else
        		return true;
        }
        else throw new IllegalArgumentException("Illegal endpoint");
    }
    
    
    public double[] randomSpeed(int numSteps) {
    	// TODO Auto-generated method stub
    	Random random = new Random(PARAMS.seedVSS);
    	double[] ret = new double[numSteps];
		double[] cdfSpeed = stochasticity.cdfSpeed();
    	for (int i = 0; i < numSteps; i++) {
    		double probability = random.nextDouble();
    		int vSelected;
    		for (vSelected = 0; vSelected < cdfSpeed.length; vSelected++) {
    			if (probability <= cdfSpeed[vSelected]) {
    				break;
    			}
    		}
    		ret[i] = vSelected;
    	}
    	return ret;
    }
    
    public boolean isLink() {
    	return edgeType.endsWith("_link");
    }
    
    public void setLinkEdge() {
    	// length = 0; // you cannot set length to 0, because this would be used to set position of loop
    	delay = 0;
    	for (int t = 0; t < PARAMS.timeMaximum; t++) {
    		cdf[t] = 1;
    		pdf[t] = 0;
    	}
    	pdf[0] = 1;
    }

    public boolean isVirtual() {
    	return edgeType.equals("virtual");
    }
    
    public String showStochasticity() {
    	String ret = "";
    	ret += name + "\n";
    	ret += "cdf:\n";
    	for (int t = 0; t < PARAMS.timeMaximum; t++) {
    		ret += cdf[t] + ",\t";
    	}
    	ret += "\n";
    	ret += "pdf:\n";
    	for (int t = 0; t < PARAMS.timeMaximum; t++) {
    		ret += pdf[t] + ",\t";
    	}
    	return ret;
    }
    
    public void reset() {
    	flow = 0;
    }

    public void setCoefficientPrioritizedJunction(double coefficientPrioritizedJunction) {
    	// TODO Auto-generated method stub
    	capacity = 1;
    	this.coefficientPrioritizedJunction =  coefficientPrioritizedJunction;
    }

    public double coefficientPrioritizedJunction() {
    	return this.coefficientPrioritizedJunction;
    }

    /**
     * Returns a string representation of the edge.
     * @return a string representation of the edge
     */
    public String toString() {
        //return v + "->" + w + "\t" + flow + "/" + capacity + "\t" + delay;
        return name + "\t" + v + "->" + w + "\t" + flow + "/" + capacity + "\tcoef=" + coefficientPrioritizedJunction + "\t" + delay + "\t" + isLink();
    }
    

   /**
     * Unit tests the <tt>FlowEdge</tt> data type.
     */
    public static void main(String[] args) {
    	System.out.println("test normal edge");
    	ClassEdge e = new ClassEdge(12, 23, 3.14);
        System.out.println(e);
        
        System.out.println("test vss");
        Set<String> laneNames = new HashSet<String>();
        laneNames.add("lane_0");
    	e = new ClassEdge(0, 1, 10, new ClassStochasticitySpeedGama(PARAMS.shape, PARAMS.speedDefault, PARAMS.lengthDefault), "edgeTestVSS", laneNames, PARAMS.lengthDefault, PARAMS.speedDefault, "highway.trunk_link");
        System.out.println(e);
    	int numSteps = 10;
    	double[] randomSpeed = e.randomSpeed(numSteps);
    	for (int i = 0; i < numSteps; i++) {
    		System.out.print(randomSpeed[i] + ",");
    	}
    	System.out.println(e.isLink());
    }





}
