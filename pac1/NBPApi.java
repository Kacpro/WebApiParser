package pac1;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Klasa odpowiedzialna za przetwarzanie
 * zapytañ do api.NBP oraz prezentowanie
 * wyników u¿ytkownikowi
 */


public class NBPApi extends GeneralAPI
{
	private List<String> codeList;
	
	/**
	 * Konstruktor, inicjalizuje listê kodów walut
	 */
	public NBPApi() 
	{
		Document doc;
		doc = getXMLDoc("http://api.nbp.pl/api/exchangerates/tables/a/?format=xml");
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
	
	/**
	 * Funkcja wypisuj¹ca pomoc w przypadku nie podania ¿adnych parametrów
	 */
	
	@Override
	public String printHelp()
	{
		return "Manual\n"
				+ "A - currentGoldPrice\n"
				+ "B - currencyPrice <Kod> <Data>\n"
				+ "C - avgGoldPrice <Data> <Data>\n"
				+ "D - biggestAmplitude <Data>\n"
				+ "E - cheapestCurrency <Data>\n"
				+ "F - profitSort <Data> <Liczba>\n"
				+ "G - bestAndWorstDayToBuy <Kod>\n"
				+ "H - printGraph <Kod> <Data> <Data>\n";
	}
	
	
	/*
	 * Funkcja odpowiedzialna za wywo³anie ¿¹danej metody
	 */
	public void execute(String[] argv) 
	{
		parseAndInvoke(argv, this.getClass());
	}
	
	/**
	 * Funkcja inicjalizuj¹ca listê dostêpnych funkcji i ich parametrów wywo³ania
	 */
	
	@Override
	protected List<Triple<String, String, String[]>> createFunctionList() 
	{
		List<Triple<String, String, String[]>> functionList = new LinkedList<>();
		functionList.add(new Triple<String, String, String[]>("currentGoldPrice", "A", new String[]{}));
		functionList.add(new Triple<String, String, String[]>("currencyPrice", "B", new String[]{"[a-zA-Z]{3}", "\\d{4}-\\d{2}-\\d{2}"}));
		functionList.add(new Triple<String, String, String[]>("avgGoldPrice", "C", new String[]{"\\d{4}-\\d{2}-\\d{2}", "\\d{4}-\\d{2}-\\d{2}"}));
		functionList.add(new Triple<String, String, String[]>("biggestAmplitude", "D", new String[]{"\\d{4}-\\d{2}-\\d{2}"}));
		functionList.add(new Triple<String, String, String[]>("cheapestCurrency", "E", new String[]{"\\d{4}-\\d{2}-\\d{2}"}));
		functionList.add(new Triple<String, String, String[]>("profitSort", "F", new String[]{"\\d{4}-\\d{2}-\\d{2}", "\\d+"}));
		functionList.add(new Triple<String, String, String[]>("bestAndWorstDayToBuy", "G", new String[]{"[a-zA-Z]{3}"}));
		functionList.add(new Triple<String, String, String[]>("printGraph", "H", new String[]{"[a-zA-Z]{3}", "\\d{4}-\\d{2}-\\d{2}", "\\d{4}-\\d{2}-\\d{2}"}));
		
		return functionList;
	}
	
	/**
	 * Funkcja wypisuj¹ca obecn¹ cenê z³ota
	 *  
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void currentGoldPrice() throws IOException, ParserConfigurationException, SAXException
	{
		Document doc = getXMLDoc("http://api.nbp.pl/api/cenyzlota/?format=xml");
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName("CenaZlota");
		Node node = nodeList.item(0);
		Element element = (Element) node;
		System.out.println("Cena z³ota w dniu " + 
		element.getElementsByTagName("Data").item(0).getTextContent() + 
		" wynosi " + element.getElementsByTagName("Cena").item(0).getTextContent() + "z³");	
	}
	
	/**
	 * Funkcja wypisuje cenê podanej waluty w podanym dniu
	 * 
	 * @param code kod waluty
	 * @param date data
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	 
	public void currencyPrice(String code, String date) throws IOException, ParserConfigurationException, SAXException
	{
		Document doc = getXMLDoc("http://api.nbp.pl/api/exchangerates/rates/A/" + code + "/" + date + "/?format=xml");
		doc.getDocumentElement().normalize();
		String currency = ((Element) doc.getDocumentElement()).getElementsByTagName("Currency").item(0).getTextContent();
		String ab = ((Element) doc.getDocumentElement()).getElementsByTagName("Code").item(0).getTextContent();
		Node rateNode = doc.getElementsByTagName("Rates").item(0);
		Element element = (Element) rateNode;
		String effectiveDate = element.getElementsByTagName("EffectiveDate").item(0).getTextContent();
		String mid = element.getElementsByTagName("Mid").item(0).getTextContent();
		System.out.println("Waluta: " + currency + " (" + ab + ")\nData: " + effectiveDate + "\nCena: " + mid);
	}
	
	/**
	 * Funkcja wypisuje œredni¹ cenê z³ota w podanym przedziale czasu
	 * 
	 * @param startDate Data pocz¹tkowa
	 * @param endDate Data koncowa
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void avgGoldPrice(String startDate, String endDate) throws IOException, ParserConfigurationException, SAXException 
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
		System.out.println(String.format("Œrednia cena z³ota w przedziale: " + startDate + " - " + endDate + " wynosi: " + "%1$.2f", sum/counter ));	
	}
	
	/**
	 * Funkcja wypisuje walutê, kktórej amplituda zmian ceny by³a najwiêksza od podanej daty
	 * 
	 * @param startDate Data pocz¹tkowa
	 * @throws IOException Data koñcowa
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void biggestAmplitude(String startDate) throws IOException, ParserConfigurationException, SAXException
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
		System.out.println("Maksymalna amplituda ceny waluty\nOkres: " +  startDate + " - " + currentDate + "\nWaluta: " + maximalAmpCurrency + " (" + maximalAmpCode + ")\nWartoœæ amplitudy: " + maximalAmp);
	}
	
	/**
	 * Funkcja wypisuje najtañsz¹ walutê w podanym dniu
	 * 
	 * @param date Data
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void cheapestCurrency(String date) throws IOException, ParserConfigurationException, SAXException
	{
		double min = Double.MAX_VALUE;
		String currencyName = "";
		String currencyCode = "";
		for (String code : codeList)
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
		System.out.println("Najtañsza waluta w danym dniu\nData: " + date + "\nWaluta: " + currencyName + " (" + currencyCode + ") "+ "\nCena: " + min);
	}
	
	/**
	 * Funkcja wypisuje podan¹ iloœæ walut, których ró¿nica miêdzy cen¹ zakupu i sprzeda¿y by³a najwiêksza w podanym dniu
	 * 
	 * @param date Data
	 * @param num Iloœæ walut do wypisania
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void profitSort(String date, String num) throws IOException, ParserConfigurationException, SAXException
	{
		SortedMap<Double, String> profitMap = new TreeMap<>();
		int number = Integer.parseInt(num);
		for (String code : codeList)
		{
			Document doc = getXMLDoc("http://api.nbp.pl/api/exchangerates/rates/c/" + code + "/" + date + "/?format=xml");
			Double bid = Double.parseDouble((((Element)((Element)((Element) doc.getElementsByTagName("ExchangeRatesSeries").item(0)).getElementsByTagName("Rates").item(0)).getElementsByTagName("Rate").item(0)).getElementsByTagName("Bid").item(0).getTextContent()));
			Double ask = Double.parseDouble((((Element)((Element)((Element) doc.getElementsByTagName("ExchangeRatesSeries").item(0)).getElementsByTagName("Rates").item(0)).getElementsByTagName("Rate").item(0)).getElementsByTagName("Ask").item(0).getTextContent()));
			Double value = ask - bid;
			String name = ((Element)doc.getElementsByTagName("ExchangeRatesSeries").item(0)).getElementsByTagName("Currency").item(0).getTextContent() + " (" + code + ") ";
			profitMap.put(value, name);
		}
		String result = "";
		for (int i=0;i<number;i++)
		{
			result += profitMap.get(profitMap.lastKey()) + " - " + profitMap.lastKey() + "\n";
			profitMap = profitMap.headMap(profitMap.lastKey());
		}
		System.out.println(result);
	}
	
	/**
	 * Funkcja wypisuje kiedy dana waluta by³a najtañsza, a kiedy najdro¿sza
	 * 
	 * @param currency kod waluty
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void bestAndWorstDayToBuy(String currency) throws IOException, ParserConfigurationException, SAXException
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal  = new GregorianCalendar();
		Date currentDate = cal.getTime();
		cal.add(Calendar.DATE, -367);
		Date startDate = cal.getTime();
		String now = dateFormat.format(currentDate);
		String then = dateFormat.format(startDate);
		
		Document doc = getXMLDoc("http://api.nbp.pl/api/exchangerates/rates/a/" + currency + "/" + then + "/" + now +"/?format=xml");
		NodeList rateList = ((Element)((Element)doc.getElementsByTagName("ExchangeRatesSeries").item(0)).getElementsByTagName("Rates").item(0)).getElementsByTagName("Rate");
		SortedMap<Double, String> rateMap = new TreeMap<>();
		Node rate = rateList.item(0);
		while (rate != null)
		{
			String date = ((Element) rate).getElementsByTagName("EffectiveDate").item(0).getTextContent();
			Double price = Double.parseDouble(((Element) rate).getElementsByTagName("Mid").item(0).getTextContent());
			rateMap.put(price, date);
			rate = rate.getNextSibling();
		}
		System.out.println("Cena max: " + rateMap.get(rateMap.lastKey()) + "  -  " + rateMap.lastKey() + "\nCena min: " + rateMap.get(rateMap.firstKey()) + "  -  " + rateMap.firstKey());
	}
	
	/**
	 * Funkcja rysuje wykres zmian wartoœci podanej waluty w podanym zakresie z podzia³em na dni tygodnia
	 * 
	 * @param currency Kod waluty
	 * @param startDate Data pocz¹tkowa
	 * @param endDate Data koñcowa
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void printGraph(String currency, String startDate, String endDate) throws IOException, ParserConfigurationException, SAXException
	{
		Document doc = getXMLDoc("http://api.nbp.pl/api/exchangerates/rates/a/" + currency + "/" + startDate + "/" + endDate +"/?format=xml");
		NodeList rateList = ((Element)((Element)doc.getElementsByTagName("ExchangeRatesSeries").item(0)).getElementsByTagName("Rates").item(0)).getElementsByTagName("Rate");
		SortedMap<Double, String> rateMap = new TreeMap<>();
		Node rate = rateList.item(0);
		List<Triple<String, Double, Integer>> daysOfWeek = new LinkedList<>();
		while (rate != null)
		{
			String date = ((Element) rate).getElementsByTagName("EffectiveDate").item(0).getTextContent();
			Double price = Double.parseDouble(((Element) rate).getElementsByTagName("Mid").item(0).getTextContent());
			rateMap.put(price, date);
			rate = rate.getNextSibling();
			
			Date day = null;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try 
			{
				day = dateFormat.parse(date);
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
			
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(day);
			daysOfWeek.add(new Triple<String, Double, Integer>(date, price, cal.get(Calendar.DAY_OF_WEEK)));
			
		}
		rate = rateList.item(0);
		Double step = (rateMap.lastKey() - rateMap.firstKey())/100;
				
		for (int i=2; i<= 6; i++)
		{
			switch(i)
			{
			case 2: System.out.println("Poniedzia³ek\n"); break;
			case 3: System.out.println("Wtorek\n"); break;
			case 4: System.out.println("Œroda\n"); break;
			case 5: System.out.println("Czwartek\n"); break;
			case 6: System.out.println("Pi¹tek\n"); break;
			}
			for (Triple<String, Double, Integer> record : daysOfWeek)
			{
				if(record.third() == i)
				{
					Double value = record.second() - rateMap.firstKey();
					System.out.print(record.first() +"    X");
					for (int j=0; j< value*1.0/step ; j++)
					{
						System.out.print("X");
					}
					System.out.print("   " + record.second() + "\n");	
				}
			}
			System.out.println("\n\n");
		}
	}
	
}





























