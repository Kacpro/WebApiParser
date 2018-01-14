package pac1;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class NBPApiTest {
	
	NBPApi api = new NBPApi();

	@Test
	public void test_currentGoldPrice() throws IOException, ParserConfigurationException, SAXException 
	{
		assertEquals("CurrentGoldPrice", "Cena z³ota w dniu 2018-01-08 wynosi 146.06z³", api._currentGoldPrice());  //na dzieñ 08-01-2017
	}

	@Test
	public void test_currencyPrice() throws IOException, ParserConfigurationException, SAXException 
	{
		assertEquals("CurrencyPrice",  "Waluta: dolar amerykañski (USD)\n" + 
									  "Data: 2017-08-07\n" + 
									  "Cena: 3.5995", api._currencyPrice("usd", "2017-08-07"));

	}

	@Test
	public void test_avgGoldPrice() throws IOException, ParserConfigurationException, SAXException 
	{
		assertEquals("avgGoldPrice", "Œrednia cena z³ota w przedziale: 2017-12-01 - 2017-12-30 wynosi: 144,34" , api._avgGoldPrice("2017-12-01", "2017-12-30"));
		assertEquals("avgGoldPrice", "Œrednia cena z³ota w przedziale: 2017-11-01 - 2017-11-30 wynosi: 148,60" , api._avgGoldPrice("2017-11-01", "2017-11-30"));
	}

	@Test
	public void test_biggestAmplitude() throws IOException, ParserConfigurationException, SAXException 
	{
		assertEquals("avgGoldPrice", "Maksymalna amplituda ceny waluty\n" + 
									  "Okres: 2017-12-01 - 2018-01-08\n" + 
									  "Waluta: funt szterling (GBP)\n" + 
									  "Wartoœæ amplitudy: 0.1598000000000006" , api._biggestAmplitude("2017-12-01"));
		
		assertEquals("avgGoldPrice", "Maksymalna amplituda ceny waluty\n" + 
									  "Okres: 2017-12-30 - 2018-01-08\n" + 
									  "Waluta: funt szterling (GBP)\n" + 
									  "Wartoœæ amplitudy: 0.03670000000000062" , api._biggestAmplitude("2017-12-30"));
}

	@Test
	public void test_cheapestCurrency() throws IOException, ParserConfigurationException, SAXException 
	{
		assertEquals("CheapestCurrency", "Najtañsza waluta w danym dniu\n" + 
										  "Data: 2017-12-01\n" + 
										  "Waluta: forint (Wêgry) (HUF) \n" + 
										  "Cena: 0.013284" , api._cheapestCurrency("2017-12-01"));

	}

	@Test
	public void test_profitSort() throws IOException, ParserConfigurationException, SAXException 
	{
		assertEquals("profitSort", "SDR (MFW) (XDR)  - 0.10060000000000002\n" + 
				"funt szterling (GBP)  - 0.09520000000000017\n" + 
				"euro (EUR)  - 0.08399999999999963\n" , api._profitSort("2017-12-01", "3"));
		
		assertEquals("profitSort", "" , api._profitSort("2017-12-01", "0"));
	}

	@Test
	public void test_bestAndWorstDayToBuy() throws IOException, ParserConfigurationException, SAXException 
	{
		assertEquals("bestAndWorstDayToBuy", "Cena max: 2017-01-09  -  4.1592\n" + 
				                             "Cena min: 2018-01-04  -  3.4472" , api._bestAndWorstDayToBuy("usd"));
	}

}
