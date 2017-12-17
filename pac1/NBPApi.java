package pac1;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NBPApi extends GeneralAPI
{
	public String printhelp()
	{
		return "Help for NBP API";
	}
	
	public String currentGoldPrice() throws IOException, ParserConfigurationException, SAXException
	{
		Document doc = getXMLDoc("http://api.nbp.pl/api/cenyzlota/?format=xml");
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName("CenaZlota");
		Node node = nodeList.item(0);
		Element element = (Element) node;
		return "Cena z³ota w dniu " + 
		element.getElementsByTagName("Data").item(0).getTextContent() + 
		" wynosi " + element.getElementsByTagName("Cena").item(0).getTextContent() + "z³";	
	}
}
