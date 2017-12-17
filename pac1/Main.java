package pac1;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main {

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException 
	{
		NBPApi api = null;
		try {
			api = new NBPApi();
		} catch (IOException | ParserConfigurationException | SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			api.currentGoldPrice();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.out.println(api.bestAndWorstDayToBuy("usd"));
		} catch (IOException | ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		api.printGraph("usd", "2017-01-10", "2017-10-01");
	}
}
