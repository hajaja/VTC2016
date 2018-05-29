import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class ClassXMLWriterAdditionalfile extends ClassXMLWriter{
	//private Document doc;
	//private Element rootElement;
	public ClassXMLWriterAdditionalfile(LinkedList<ClassPath> paths) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Attr attr;
		
		// root
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("additional");
		doc.appendChild(rootElement);
		setAttr(doc, rootElement, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		setAttr(doc, rootElement, "xsi:noNamespaceSchemaLocation", "http://sumo.dlr.de/xsd/additional_file.xsd");
		
		// collect all edges
		Set<ClassEdge> edges = new HashSet<ClassEdge>();
		for (ClassPath path : paths) {
			for (ClassEdge e : path.edges) {
				edges.add(e);
			}
		}
		
		// dump edge
		for (ClassEdge e : edges) {
			if (e.isVirtual() == false)
				dumpEdge(doc, rootElement, e);
		}
		write(doc, PARAMS.fileAdd);
		
		// modify sumocfg
		/*
		Document docCFG;
		Element elementCFG;
		NodeList listCFG;
		ClassXMLReader reader = new ClassXMLReader("variable_speed_signs.sumocfg.xml");
		docCFG = reader.document;
		elementCFG = docCFG.getElementById("configuration");
		listCFG = elementCFG.getElementsByTagName("input");
		elementCFG = (Element) listCFG.item(0);
		listCFG = elementCFG.getElementsByTagName("additional-files");
		elementCFG = (Element) listCFG.item(0);
		*/
	}
	
	public void dumpEdge(Document doc, Element element, ClassEdge e) throws ParserConfigurationException, TransformerException {
		// vss elements
		Element vssElement = doc.createElement("variableSpeedSign");
		element.appendChild(vssElement);
		setAttr(doc, vssElement, "id", "vss" + e.name());
		String strLaneNames = "";
		Set<String> laneNames = e.laneNames();
		int i = 0;
		for (String laneName : laneNames) {
			strLaneNames += laneName;
			if (i < laneNames.size() - 1) {
				strLaneNames += " ";
				i++;
			}
		}
		setAttr(doc, vssElement, "lanes", strLaneNames);
		setAttr(doc, vssElement, "file", "vss\\edge" + e.nameNoSharp() + ".def.xml");
		
		// write def.xml
		writeDef(e);
	}
	
	public void writeDef(ClassEdge e) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Attr attr;
		
		// root
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("vss");
		doc.appendChild(rootElement);
		
		// step elements
		int numSteps = (int) (PARAMS.timeMaxSimulation * 2 / PARAMS.vssStepSize);
		double[] randomSpeed = e.randomSpeed(numSteps);
		for (int t = 0; t < PARAMS.timeMaxSimulation * 2; t += PARAMS.vssStepSize) {
			Element stepElement = doc.createElement("step");
			rootElement.appendChild(stepElement);
			setAttr(doc, stepElement, "time", String.valueOf(t));
			double randomSpeedValue = randomSpeed[t / PARAMS.vssStepSize];
			setAttr(doc, stepElement, "speed", String.valueOf(randomSpeedValue));
		}

		// create VSS directory if not exists
		new File(PARAMS.pathVSS).mkdirs();
		

		write(doc, PARAMS.pathVSS + "edge" + e.nameNoSharp() + ".def.xml");		
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		
		ClassXMLReaderNet reader = new ClassXMLReaderNet("data/Singapore.net.xml");
		ClassNetworkSG network = new ClassNetworkSG(reader);
		int s = network.VertexFromEdgeInteger("44818682#1");
		int t = network.VertexFromEdgeInteger("33793661#1");
    	
        StdOut.println(network);
		ClassAlgorithmDCFFKSP maxflow = new ClassAlgorithmDCFFKSP();
		maxflow.findMaximumFlow(network, s, t);
		LinkedList<ClassPath> paths = maxflow.paths();

		ClassXMLWriterRou writerRou = new ClassXMLWriterRou("data/Singapore.rou.xml", paths);
		ClassXMLWriterAdditionalfile writerAdditional = new ClassXMLWriterAdditionalfile(paths);
	}
}
