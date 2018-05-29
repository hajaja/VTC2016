import java.io.File;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ClassXMLWriter {
	public void setAttr(Document doc, Element element, String attrName, String attrValue) {
		Attr attr = doc.createAttribute(attrName);
		attr.setValue(attrValue);
		element.setAttributeNode(attr);
	}
	
	public void write(Document doc, String fileAddress) throws TransformerException {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");  
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(fileAddress));
		transformer.transform(source, result);
		//System.out.println("File saved!");
	}
}
