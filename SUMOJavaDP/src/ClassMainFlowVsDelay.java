import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


public class ClassMainFlowVsDelay extends ClassMain{
	public void test() throws FileNotFoundException {
		PrintStream ps;
		File f = new File(PARAMS.fileFlowVsDelay);
		FileOutputStream fos = new FileOutputStream(f);
		ps = new PrintStream(fos);
		
		
		double delayMinimum = 790;
		double delayMaximum = 930;
		double delayStep = 5;
		
		/*
		double delayMinimum = 600;
		double delayMaximum = 2300;
		double delayStep = 100;
		*/
		int numDelays = (int) Math.floor((delayMaximum - delayMinimum) / delayStep);
		double[] arrayDelay = new double[numDelays];
		for (int n = 0; n < numDelays; n++) {
			arrayDelay[n] = delayMinimum + delayStep * n;
		}
		for (int n = 0; n < numDelays; n++) {
			double delay = arrayDelay[n];
			ps.print(delay);
			if (n < numDelays - 1) {
				ps.print(",");
			}
			else {
				ps.print("\n");
			}
		}
		
		InterfaceAlgorithm maxflow = null;
		int numAlgorithms = listAlgorithms.length;
		for (int i = 0; i < numAlgorithms; i++) {
			maxflow = listAlgorithms[i];
		
			for (int n = 0; n < numDelays; n++) {
				double delay = arrayDelay[n];
				PARAMS.delayMaximum = delay;
				network.reset();
				maxflow.reset();
				maxflow.findMaximumFlow(network, s, t);
				ps.print(maxflow.flow() + "," + maxflow.timeCalculation());
				if (n < numDelays - 1) {
					ps.print(",");
				}
				else {
					ps.print("\n");
				}
			}
		}
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		PARAMS.prepareLog();
		ClassMainFlowVsDelay flowVsDelay = new ClassMainFlowVsDelay();
		flowVsDelay.settingMap();
		flowVsDelay.settingAlgorithms();
		flowVsDelay.test();
	}
}
