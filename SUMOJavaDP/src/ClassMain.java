import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


public class ClassMain {
	double[] coordinateOrigin;
	double[] coordinateDestination;
	double radius;
	ClassXMLReaderNet reader;
	ClassNetworkSG network;
	ClassArea origin;
	ClassArea destination;
	int s;
	int t;
	InterfaceAlgorithm[] listAlgorithms;
	
	double[][] coordinatesSingapore0 = {{85658.91,20884.69}, {94548.29,2011.73}};
	double[][] coordinatesCologne0 = {{7634, 8618}, {18667,6372}};
	double[][] coordinatesCologne1 = {{3465, 4114}, {18667,6372}};
	double[][] coordinatesCologne2 = {{5559, 6581}, {18667,6372}};
	double[][] coordinatesCologne3 = {{10673, 5854}, {18667,6372}};
	double radius1 = 500;
	double radius2 = 1000;
	double radius3 = 1500;
	double radius4 = 2000;
	double radius5 = 2500;
	
	public void settingMap() throws ParserConfigurationException, SAXException, IOException {
		if (PARAMS.cityName.equals("Singapore")) {
			coordinateOrigin = coordinatesSingapore0[0]; 
			coordinateDestination = coordinatesSingapore0[1]; 
			radius = radius5;
		}
		else if (PARAMS.cityName.equals("Cologne")) {
			coordinateOrigin = coordinatesCologne0[0]; 
			coordinateDestination = coordinatesCologne0[1]; 
			radius = radius5;
		}
		
		// read network
		reader = new ClassXMLReaderNet(PARAMS.fileNet);
		network = new ClassNetworkSG(reader);

		// construct intresting area
		origin = new ClassArea(network, coordinateOrigin, radius, 0);
		destination = new ClassArea(network, coordinateDestination, radius, 1);
		s = origin.virtualNode;
		t = destination.virtualNode;
	}
	
	public void settingAlgorithms() {
		int numAlgorithms = 4;
		listAlgorithms = new InterfaceAlgorithm[numAlgorithms];
		listAlgorithms[0] = new ClassAlgorithmDCFF();
		listAlgorithms[1] = new ClassAlgorithmDCFFKSP();
		listAlgorithms[2] = new ClassAlgorithmDCFFKSPDinic();
		listAlgorithms[3] = new ClassAlgorithmDCFFKSPDinicCompatible();
	}
	
}
