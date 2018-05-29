import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.LinkedList;


public class PARAMS {
	// completet graph information
	public static int speedDefault = (int) speedMS(30);
	public static int lengthDefault = 1000;		//for graphs of Algorithm book
	public static int K = 20;
	public static int KELEMENTS = 2;
	
	// delay constraint 
	public static int timeMaximum = 3000;
	public static double delayMaximum = 2500;
	public static double probabilityMinimum = 0.6;
	
	/*
	public static double delayMaximum = 6;
	public static double probabilityMinimum = 0.1;
	*/
	
	// stochasticity
	// factor for micro simulation
	public static double factorMicro = 1.0;
	// Gama
	public static double shape = 5.0;
	// Uniform
	public static double range = 0.3;
	
	
	// SUMO 
	public static double vehicleLength = 1.0;
	public static double tau = 5.0;
	public static double coefficientTauRelaxed = 1.2;
	public static int timeMaxSimulation = 3600 * 1;
	public static int speedMaximum = (int) speedMS(200);
	public static double speedPassingYellow = 4;
	public static double coefficientSpeedLimit = 0.9; // adjust the speed limit of all edges
	public static double coefficientPrioritizedJunction = 0.6;
	public static double vehicleAcceleration = 2.6;
	public static double vehicleDeceleration = 4.0;
	
	// shortcut, to be fixed
	public static Hashtable<String, String> hashtableConnectionType;

	// vss 
	public static int vssStepSize = 100;
	public static int seedVSS = 5678;
	
	// virtual edge
	public static double virtualEdgeCapacity = 1000;
	
	// loop
	public static int loopFreq = 100;
	
	// file path 
	//static String cityName = "Singapore";
	static String cityName = "Cologne";
	static String pathData = "data/" + cityName + "/";
	static String pathVSS = pathData + "vss/";
	
	static String fileNet = pathData + cityName + ".net.xml";
	static String fileRou = pathData + cityName + ".rou.xml";
	static String fileAdd = pathData + cityName + ".add.xml";
	static String fileCdf = pathData + "cdfCurve.csv";
	
	static String pathMatlab = pathData + "matlab/";
	static String fileDelayComparison = pathMatlab + "delays.csv";
	static String fileFlowComparison = pathMatlab + "flows.csv";
	static String fileFlowVsDelay = pathMatlab + "flowDelay.csv";
	// utils
	public static double speedMS(double speedKMH){
		return speedKMH * 1000 / 3600;
	}
	
	public static double speedKMH(double speedMS){
		return speedMS * 3600 / 1000;
	}
	
	public static PrintStream ps;
	
	public static void prepareLog() throws IOException {
		File f = new File("log.txt");
		FileOutputStream fos = new FileOutputStream(f);
		ps = new PrintStream(fos);
	}
	
}
