package test;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import pac1.NBPApi;

public class NBPApiTest {

	@Test
	public void testavgGoldPriceTest() throws IOException, ParserConfigurationException, SAXException 
	{
		NBPApi api = new NBPApi();
//		assertEquals("Œrednia cena z³ota w przedziale: 2017-12-01 - 2017-12-05 wynosi: 145,53", api.avgGoldPrice("2017-12-01", "2017-12-05"));
//		assertEquals("Œrednia cena z³ota w przedziale: 2017-12-10 - 2017-12-15 wynosi: 143,27", api.avgGoldPrice("2017-12-10", "2017-12-15"));
//		assertEquals("Œrednia cena z³ota w przedziale: 2017-12-15 - 2017-12-15 wynosi: 143,65", api.avgGoldPrice("2017-12-15", "2017-12-15"));
	}
	
	@Test(expected = IOException.class)
    public void testDateError() throws IOException, ParserConfigurationException, SAXException 
    {
		NBPApi api = new NBPApi();
		api.avgGoldPrice("2017-12-05", "2017-12-04");
    }
	
	@Test(expected = IOException.class)
    public void testToEarly() throws IOException, ParserConfigurationException, SAXException 
    {
		NBPApi api = new NBPApi();
		api.avgGoldPrice("2015-12-05", "2017-12-04");
    }
	
	
    

}
