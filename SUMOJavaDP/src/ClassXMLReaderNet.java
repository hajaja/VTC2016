import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ClassXMLReaderNet extends ClassXMLReader{
	public int numJunctions; 

	public ClassXMLReaderNet(String fileAddress) throws ParserConfigurationException, SAXException, IOException
	{
	    super(fileAddress);
	    
	    // construct graph
	    NodeList list = document.getElementsByTagName("junction");
	    numJunctions = 0;
        for (int i = 0; i < list.getLength(); i++)
        {
        	Element elementJunction = (Element) list.item(i);
        	if (elementJunction.getAttribute("type").equals("internal") == false)
        	{
        		numJunctions++;
        	}
        }
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		ClassXMLReaderNet readNetXML = new ClassXMLReaderNet("data/quickstart.net.xml");
	}
    
}
