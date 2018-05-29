import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


public class ClassMainFlowComparison extends ClassMain{
	public void test() throws FileNotFoundException {
		// output csv files for Matlab analysis
		PrintStream ps;
		File f = new File(PARAMS.fileFlowComparison);
		FileOutputStream fos = new FileOutputStream(f);
		ps = new PrintStream(fos);
		
		InterfaceAlgorithm maxflow = null;
		int numAlgorithms = listAlgorithms.length;
		for (int i = 0; i < numAlgorithms; i++) {
			maxflow = listAlgorithms[i];
		
			network.reset();
			maxflow.findMaximumFlow(network, s, t);
			ps.print(maxflow.flow());
			if (i != numAlgorithms - 1) {
				ps.print(",");
			}
			else {
				ps.print("\n");
			}
		}
	}
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		ClassMainFlowComparison flowComparison = new ClassMainFlowComparison();
		flowComparison.settingMap();
		flowComparison.settingAlgorithms();
		flowComparison.test();
	}
}
