import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.PriorityQueue;

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
import org.xml.sax.SAXException;


public class ClassXMLWriterRou extends ClassXMLWriter{
	private Document doc;
	private Element rootElement;
	public ClassXMLWriterRou(String fileAddress, LinkedList<ClassPath> paths) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Attr attr;
		
		// root elements
		doc = docBuilder.newDocument();
		rootElement = doc.createElement("routes");
		doc.appendChild(rootElement);
		setAttr(doc, rootElement, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		setAttr(doc, rootElement, "xsi:noNamespaceSchemaLocation", "http://sumo.dlr.de/xsd/routes_file.xsd");
		
		// vType elements
		Element vTypeElement = doc.createElement("vType");
		rootElement.appendChild(vTypeElement);
		setAttr(doc, vTypeElement, "id", "type1");
		setAttr(doc, vTypeElement, "length", String.valueOf(PARAMS.vehicleLength));
		
		// carFollowing element
		Element carFollowingElement = doc.createElement("carFollowing-Krauss");
		vTypeElement.appendChild(carFollowingElement);
		setAttr(doc, carFollowingElement, "tau", String.valueOf(PARAMS.tau / 2));
		
		// collect all <vehicle></vehicle> and sort chronologically
		int nPath = 0;
		PriorityQueue<ClassXMLVehicle> vehicles = new PriorityQueue<ClassXMLVehicle>();
		for (ClassPath path : paths) {
			/*
			LinkedList<ClassXMLVehicle> vehiclesList = dump(path, nPath);
			for (ClassXMLVehicle vehicle : vehiclesList) {
				vehicles.add(vehicle);
			}
			*/
			vehicles.addAll(dump(path, nPath));
			nPath++;
		}
		// write the <vehicle> in the doc
		while (vehicles.isEmpty() == false) {
			ClassXMLVehicle vehicle = vehicles.poll();
			dump(vehicle);
		}
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");  
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(fileAddress));
		transformer.transform(source, result);
		
	}
	
	public LinkedList<ClassXMLVehicle> dump(ClassPath path, int nPath) {
		LinkedList<ClassXMLVehicle> ret = new LinkedList<ClassXMLVehicle>();
		double period = PARAMS.tau / path.flow() * PARAMS.coefficientTauRelaxed;
		for (double t = 0; t < PARAMS.timeMaxSimulation; t += period) {

			String id = "veh_" + nPath + "_" + String.valueOf(t);
			int departTime = (int) t;
			String strRoute = "";
			for (ClassEdge e : path.edges) {
				if (e.isVirtual() == false)
					strRoute += (e.name() + " ") ;
			}
			ret.add(new ClassXMLVehicle(id, departTime, strRoute));			
		}
		return ret;
	}
	
	public void dump(ClassXMLVehicle vehicle) {
		Element vehicleElement = doc.createElement("vehicle");
		rootElement.appendChild(vehicleElement);
		setAttr(doc, vehicleElement, "id", vehicle.id());
		setAttr(doc, vehicleElement, "type", "type1");
		setAttr(doc, vehicleElement, "depart", vehicle.t());
		setAttr(doc, vehicleElement, "departLane", "random");
		setAttr(doc, vehicleElement, "departPos", "random");
		setAttr(doc, vehicleElement, "arrivalLane", "current");
		Element routeElement = doc.createElement("route");
		vehicleElement.appendChild(routeElement);
		setAttr(doc, routeElement, "edges", vehicle.strRoute());
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		
		ClassXMLReaderNet reader = new ClassXMLReaderNet("data/Singapore.net.xml");
		ClassNetworkSG network = new ClassNetworkSG(reader);
		int s = network.VertexFromEdgeInteger("44818682#1");
		int t = network.VertexFromEdgeInteger("33793661#1");
    	
        StdOut.println(network);
		ClassAlgorithmDCFFKSP maxflow = new ClassAlgorithmDCFFKSP();
		maxflow.findMaximumFlow(network, s, t);
		
		ClassXMLWriterRou writer = new ClassXMLWriterRou("data/Singapore.rou.xml", maxflow.paths());
		//writer.dump(maxflow.paths());		
	}
}
