import java.util.LinkedList;

public class ClassNetworkDCNSG extends ClassNetwork{
    public ClassNetworkDCNSG(In in) {
        super(in.readInt());
        int E = in.readInt();
        if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
        for (int i = 0; i < E; i++) {
            int v = in.readInt();
            int w = in.readInt();
            if (v < 0 || v >= V()) throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V()-1));
            if (w < 0 || w >= V()) throw new IndexOutOfBoundsException("vertex " + w + " is not between 0 and " + (V()-1));
            double capacity = in.readDouble();
            double shape = in.readDouble();
            double scale = in.readDouble();
            //addEdge(new ClassEdge(v, w, capacity, new ClassStochasticityTimeGama(shape, (shape * scale)), "noname", 0, 0));
            addEdge(new ClassEdge(v, w, capacity, new ClassStochasticitySpeedGama(PARAMS.shape, PARAMS.speedDefault, PARAMS.lengthDefault), "noname", null, 0, 0, ""));
        }
    }

    public static void main(String[] args) {
    	String fileAddress = "C:/tools/eclipse/workspace/SUMOJavaDP/data/tinyFNMyDelayBeta1.txt";
        In in = new In(fileAddress);
        ClassNetworkDCNSG G = new ClassNetworkDCNSG(in);
        StdOut.println(G);
    }

}
