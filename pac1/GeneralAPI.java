package pac1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
		BufferedReader reader = new BufferedReader(new InputStreamReader( url.openStream()));
		String file = "";
		String line;
		while ((line = reader.readLine()) != null)
		{
			file += line;
		}
		reader.close();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(file);
		return doc;
	}
}
