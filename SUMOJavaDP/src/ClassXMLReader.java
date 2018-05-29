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
	    // step 1:���DOM����������
	    // �����������Ǵ�������Ľ�����
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	    // step 2����þ����dom������
	    DocumentBuilder db = dbf.newDocumentBuilder();

	    // step 3:����һ��xml�ĵ������Document���󣨸��ڵ㣩
	    // ���ĵ�������ĿĿ¼�¼���
	    //Document document = db.parse(new File("quickstart.net.xml"));
	    document = db.parse(new File(fileAddress));
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		ClassXMLReader readNetXML = new ClassXMLReader("data/quickstart.net.xml");
	}
    
}
