import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


public class ClassMainSUMO extends ClassMain{
	public void test() throws IOException, ParserConfigurationException, TransformerException, SAXException {
		// find maximum flow
		System.out.println(network);
		ClassAlgorithmDCFF maxflow = new ClassAlgorithmDCFF();
		//ClassAlgorithmDCFFKSP maxflow = new ClassAlgorithmDCFFKSP();
		//ClassAlgorithmDCFFKSPDinic maxflow = new ClassAlgorithmDCFFKSPDinic();
		//ClassAlgorithmDCFFKSPDinicCompatible maxflow = new ClassAlgorithmDCFFKSPDinicCompatible();
		maxflow.findMaximumFlow(network, s, t);
		LinkedList<ClassPath> paths = maxflow.paths();

		// output xml files for SUMO use

		//paths.remove(paths.get(2));
		ClassXMLWriterRou writerRou = new ClassXMLWriterRou(PARAMS.fileRou, paths);
		ClassXMLWriterAdditionalfile writerAdditional = new ClassXMLWriterAdditionalfile(paths);
		ClassXMLWriterLoops writerLoops = new ClassXMLWriterLoops(paths);
		writerLoops.writePaths();
		writerLoops.write(origin);
		writerLoops.write(destination);

		// output csv files for Matlab analysis
		PrintStream ps;
		File f = new File(PARAMS.fileCdf);
		FileOutputStream fos = new FileOutputStream(f);
		ps = new PrintStream(fos);
		for (ClassPath path : paths) {
			ps.print(path.showStochasticity());
		}
	}
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		PARAMS.prepareLog();
		ClassMainSUMO sumo = new ClassMainSUMO();
		sumo.settingMap();
		sumo.test();
	}
}
