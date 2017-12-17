package pac1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NBPApi extends GeneralAPI
{
	private List<String> codeList;
	
	
	public NBPApi() throws IOException, ParserConfigurationException, SAXException
	{
		Document doc = getXMLDoc("http://api.nbp.pl/api/exchangerates/tables/a/?format=xml");
		doc.getDocumentElement().normalize();
		codeList = new LinkedList<>();
		NodeList nodeList = ((Element)(((Element) doc.getElementsByTagName("ExchangeRatesTable").item(0)).getElementsByTagName("Rates").item(0))).getElementsByTagName("Rate");
		Node node = nodeList.item(0);
		while (node != null)
		{
			String code = ((Element) node).getElementsByTagName("Code").item(0).getTextContent();
			codeList.add(code);
			node = node.getNextSibling();
		}
	}
	
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
		Document doc = getXMLDoc("http://api.nbp.pl/api/exchangerates/rates/A/" + code + "/" + date + "/?format=xml");
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
	
	public String biggestAmplitude(String startDate) throws IOException, ParserConfigurationException, SAXException
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		
		
		double maximalAmp = 0;
		String maximalAmpCurrency = "";
		String maximalAmpCode = "";
		for ( String code : codeList)
		{
			Document doc = getXMLDoc("http://api.nbp.pl/api/exchangerates/rates/a/"+ code + "/" + startDate + "/" + currentDate + "/?format=xml");
			double min = Double.MAX_VALUE;
			double max = Double.MIN_NORMAL;
			NodeList list = ((Element)(((Element) doc.getElementsByTagName("ExchangeRatesSeries").item(0)).getElementsByTagName("Rates").item(0))).getElementsByTagName("Rate");
			
			Node rate = list.item(0);
			while (rate != null)
			{
				double buffer = Double.parseDouble((((Element)rate).getElementsByTagName("Mid").item(0).getTextContent()));
				if (buffer < min)
				{
					min = buffer;
				}
				if (buffer > max)
				{
					max = buffer;
				}
				rate = rate.getNextSibling();
			}
			if ((max - min) >= maximalAmp)
			{
				maximalAmp = max - min;
				
				maximalAmpCurrency = doc.getElementsByTagName("Currency").item(0).getTextContent();
				maximalAmpCode = doc.getElementsByTagName("Code").item(0).getTextContent();
			}
		}
		return "Maksymalna amplituda ceny waluty\nOkres: " +  startDate + " - " + currentDate + "\nWaluta: " + maximalAmpCurrency + " (" + maximalAmpCode + ")\nWartoœæ amplitudy: " + maximalAmp;
	}
	
	public String cheapestCurrency(String date) throws IOException, ParserConfigurationException, SAXException
	{
		double min = Double.MAX_VALUE;
		String currencyName = "";
		String currencyCode = "";
		for (String code : codeList)
		{
			try 
			{
				Document doc  = getXMLDoc("http://api.nbp.pl/api/exchangerates/rates/c/" + code + "/" + date + "/?format=xml");
				double buffer = Double.parseDouble((((Element)((Element)((Element) doc.getElementsByTagName("ExchangeRatesSeries").item(0)).getElementsByTagName("Rates").item(0)).getElementsByTagName("Rate").item(0)).getElementsByTagName("Bid").item(0).getTextContent()));
				if (buffer < min)
				{
					min = buffer;
					currencyName = ((Element)doc.getElementsByTagName("ExchangeRatesSeries").item(0)).getElementsByTagName("Currency").item(0).getTextContent();
					currencyCode = code;
				}
			}
			catch(FileNotFoundException e)
			{
			}
		}
		return "Najtañsza waluta w danym dniu\nData: " + date + "\nWaluta: " + currencyName + " (" + currencyCode + ") "+ "\nCena: " + min;
	}
	
	public String profitSort(String date, int number) throws IOException, ParserConfigurationException, SAXException
	{
		SortedMap<Double, String> profitMap = new TreeMap<>();
		for (String code : codeList)
		{
			try
			{
			Document doc = getXMLDoc("http://api.nbp.pl/api/exchangerates/rates/c/" + code + "/" + date + "/?format=xml");
			Double bid = Double.parseDouble((((Element)((Element)((Element) doc.getElementsByTagName("ExchangeRatesSeries").item(0)).getElementsByTagName("Rates").item(0)).getElementsByTagName("Rate").item(0)).getElementsByTagName("Bid").item(0).getTextContent()));
			Double ask = Double.parseDouble((((Element)((Element)((Element) doc.getElementsByTagName("ExchangeRatesSeries").item(0)).getElementsByTagName("Rates").item(0)).getElementsByTagName("Rate").item(0)).getElementsByTagName("Ask").item(0).getTextContent()));
			Double value = ask - bid;
			String name = ((Element)doc.getElementsByTagName("ExchangeRatesSeries").item(0)).getElementsByTagName("Currency").item(0).getTextContent() + " (" + code + ") ";
			profitMap.put(value, name);
			}
			catch(FileNotFoundException e)
			{
			}
		}
		String result = "";
		for (int i=0;i<number;i++)
		{
			result += profitMap.get(profitMap.lastKey()) + " - " + profitMap.lastKey() + "\n";
			profitMap = profitMap.headMap(profitMap.lastKey());
		}
		return result;
	}
	
	
}





























