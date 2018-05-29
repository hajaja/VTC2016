import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public class ClassXMLWriterLoops extends ClassXMLWriter{
	Document doc;
	LinkedList<ClassPath> paths;
	public ClassXMLWriterLoops(LinkedList<ClassPath> paths) throws ParserConfigurationException, SAXException, IOException {
		this.paths = paths;
	}
	
	public void write(ClassArea area) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// TODO Auto-generated method stub
		ClassXMLReader reader = new ClassXMLReader(PARAMS.fileAdd);
		doc = reader.document;
		
		LinkedList<ClassEdge> edgesToWrite = new LinkedList<ClassEdge>(); 
		for (ClassEdge e : area.edges) {
			for (ClassPath path : paths) {
				if (path.contains(e)) {
					edgesToWrite.add(e);
				}
			}
		}
		
		// write to doc
		for (ClassEdge e : edgesToWrite) {
			dumpEdge(area, e);
		}
		
		// write loop names to a txt file
		PrintWriter writer = new PrintWriter(PARAMS.pathData + "loops_" + area.indicator + ".txt");
		
		for (ClassEdge e : edgesToWrite) {
			ClassEdge edge = area.findLinkEdgeFromVirtualEdgeName(e.name());
			for (String laneName : edge.laneNames()) {
				String loopName = "loop_" + area.indicator + "_" + laneName ;
				writer.println(loopName);
			}
		}
		writer.close();
		
		// flush doc to file
		write(doc, PARAMS.fileAdd);
	}

	public void dumpEdge(ClassArea area, ClassEdge e) {
		ClassEdge edge = area.findLinkEdgeFromVirtualEdgeName(e.name());
		for (String laneName : edge.laneNames()) {
			String loopName = "loop_" + area.indicator + "_" + laneName ;
			Element loopElement = doc.createElement("inductionLoop");
			Element rootElement = (Element) doc.getFirstChild();
			rootElement.appendChild(loopElement);
			setAttr(doc, loopElement, "id", loopName);
			setAttr(doc, loopElement, "lane", laneName);
			if (area.indicator == 0) {
				setAttr(doc, loopElement, "pos", String.valueOf(edge.length()));
			}
			else if (area.indicator == 1){
				setAttr(doc, loopElement, "pos", String.valueOf(0));
			}
			else {
				System.out.println("Error: incorrect position in ClassXMLWriterLoops");
				System.exit(0);
			}
			setAttr(doc, loopElement, "freq", String.valueOf(PARAMS.loopFreq));
			setAttr(doc, loopElement, "file", "loop.out.xml");
		}
	}

	public void writePaths() {
		// TODO Auto-generated method stub
		
	}

}
