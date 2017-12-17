package pac1;

import java.io.IOException;
import java.net.URL;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class GeneralAPI 
{
	
	protected String printHelp()
	{
		return "This API doesn't implement manual";
	}
	
	
	protected Document getXMLDoc(String strUrl) throws IOException, ParserConfigurationException, SAXException
	{
		URL url = new URL(strUrl);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.parse(url.openStream());
		return doc;
	}
}
