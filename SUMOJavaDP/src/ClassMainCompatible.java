import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


public class ClassMainCompatible extends ClassMain{
	public void test() throws FileNotFoundException {
		// output csv files for Matlab analysis
		InterfaceAlgorithm maxflow = null;
		maxflow = new ClassAlgorithmDCFFKSPDinicCompatible();
		maxflow.findMaximumFlow(network, s, t);
		System.out.println(maxflow.flow());
	}
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		ClassMainCompatible flowComparison = new ClassMainCompatible();
		flowComparison.settingMap();
		flowComparison.settingAlgorithms();
		flowComparison.test();
	}
}
