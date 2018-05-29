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

public class ClassXMLReader {
	public Document document;
	
	public ClassXMLReader(String fileAddress) throws ParserConfigurationException, SAXException, IOException
	{
	    // step 1:获得DOM解析器工厂
	    // 工厂的作用是创建具体的解析器
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	    // step 2：获得具体的dom解析器
	    DocumentBuilder db = dbf.newDocumentBuilder();

	    // step 3:解析一个xml文档，获得Document对象（根节点）
	    // 此文档放在项目目录下即可
	    //Document document = db.parse(new File("quickstart.net.xml"));
	    document = db.parse(new File(fileAddress));
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		ClassXMLReader readNetXML = new ClassXMLReader("data/quickstart.net.xml");
	}
    
}
