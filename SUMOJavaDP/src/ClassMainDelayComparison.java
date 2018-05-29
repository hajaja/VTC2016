import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


public class ClassMainDelayComparison extends ClassMain{
	public void test() throws FileNotFoundException {
		// output csv files for Matlab analysis
		PrintStream ps;
		File f = new File(PARAMS.fileDelayComparison);
		FileOutputStream fos = new FileOutputStream(f);
		ps = new PrintStream(fos);

		InterfaceAlgorithm maxflow = null;
		int numAlgorithms = listAlgorithms.length;
		for (int i = 0; i < numAlgorithms; i++) {
			maxflow = listAlgorithms[i];
			if (i == 0) {
				((ClassAlgorithmDCFF) maxflow).switchTraditionalFF = true;
			}

			network.reset();
			maxflow.findMaximumFlow(network, s, t);
			LinkedList<ClassPath> paths = maxflow.paths();
			for (ClassPath path : paths) {
				ps.print(path.delayAtProbability(PARAMS.probabilityMinimum) + ",");
				ps.print(path.delay + ",");
			}
			ps.print("\n");
		}
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		ClassMainDelayComparison delayComparison = new ClassMainDelayComparison();
		delayComparison.settingMap();
		delayComparison.settingAlgorithms();
		delayComparison.test();
	}
}
