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
	
	public String currencyPrice(String code, String date) throws IOException, ParserConfigurationException, SAXException
	{
		Document doc = getXMLDoc("http://api.nbp.pl/api/exchangerates/rates/A/" + code.toUpperCase() + "/" + date + "/?format=xml");
		doc.getDocumentElement().normalize();
		String currency = ((Element) doc.getDocumentElement()).getElementsByTagName("Currency").item(0).getTextContent();
		String ab = ((Element) doc.getDocumentElement()).getElementsByTagName("Code").item(0).getTextContent();
		Node rateNode = doc.getElementsByTagName("Rates").item(0);
		Element element = (Element) rateNode;
		String effectiveDate = element.getElementsByTagName("EffectiveDate").item(0).getTextContent();
		String mid = element.getElementsByTagName("Mid").item(0).getTextContent();
		return "Waluta: " + currency + " (" + ab + ")\nData: " + effectiveDate + "\nCena: " + mid;
	}
	
	public String avgGoldPrice(String startDate, String endDate) throws IOException, ParserConfigurationException, SAXException 
	{
		Document doc = getXMLDoc("http://api.nbp.pl/api/cenyzlota/" + startDate + "/" + endDate + "/?format=xml");
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName("CenaZlota");
		Node node = nodeList.item(0);
		double sum = 0;
		int counter = 0;
		while (node!=null)
		{
			Element element = (Element) node;
			counter++;
			sum+= Double.parseDouble(element.getElementsByTagName("Cena").item(0).getTextContent());
			node = node.getNextSibling();
		}
		return "Œrednia cena z³ota w przedziale: " + startDate + " - " + endDate + " wynosi: " + sum/counter;	
	}
	
	
}
