package pac1;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 * Klasa zawieraj¹ca metody wspólne dla wszystkich api obs³ugiwanych przed ten program
 *
 */

public abstract class GeneralAPI 
{
	
	protected List<Triple<String, String, String[]>> functions = createFunctionList();
	
	protected abstract List<Triple<String, String, String[]>> createFunctionList();
	
	protected  abstract String printHelp();
	
	/**
	 * FUnkcja tworzy strukturê dokumentu na podstawie danych pobranych w podanego adresu URL
	 * 
	 * @param strUrl
	 * @return
	 */
	protected Document getXMLDoc(String strUrl)
	{
		URL url = null;
		try 
		{
			url = new URL(strUrl);
		} 
		catch (MalformedURLException e) 
		{
			System.out.println("B³êdny adres URL");
			e.printStackTrace();
			System.exit(-1);
		}
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		Document doc = null;
		try
		{
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(url.openStream());
		}
		catch(ParserConfigurationException | SAXException | IOException e)
		{
			System.out.println("B³¹d podczas pobierania i parsowania danych");
			e.printStackTrace();
			System.exit(-1);
		}
		return doc;
	}
	
	/**
	 * Funkcja parsuje parametry z linii komend oraz wywo³uje ¿¹dane metody
	 * 
	 * @param argv
	 * @param api
	 */
	protected void parseAndInvoke(String[] argv, Class<?> api) 
	{
		if (argv.length == 0)
		{
			System.out.println(printHelp());
			return;
		}
		for (Triple<String, String, String[]> function : functions)
		{
			if(argv[0].equals(function.second()))
			{
				if(argv.length != function.third().length + 1)
				{
					System.out.println("B³êdna liczba parametrów. Oczekiwana: " + function.third().length + ", aktualna: " + (argv.length -1) );
					System.exit(-1);
				}
				String[] arguments = new String[function.third().length];
				for (int i=0; i< function.third().length; i++)
				{
					Matcher matcher = Pattern.compile(function.third()[i]).matcher(argv[i+1]);
					if (matcher.matches())
					{
						arguments[i] = argv[i+1];
					}
					else
					{
						System.out.println("B³êdny format parametrów. Oczekiwany format: " + function.third()[i] + ", wartoœæ aktualna: " + argv[i+1]);
						System.exit(-1);
					}
				}
				Class<?> _api = null;
				
				try 
				{
					_api = Class.forName(api.getName());
				} 
				catch (ClassNotFoundException e)
				{
					System.out.println("B³¹d przy dostêpie do api");
					e.printStackTrace();
					System.exit(-1);
				}
				
				Method[] methods = _api.getDeclaredMethods();
				Method method = null;
				for (Method m : methods)
				{
					if (m.getName().equals(function.first()))
					{
						method = m;
					}
				}
				Object[] obj = new Object[arguments.length];
				for (int i=0; i<arguments.length; i++)
				{
					obj[i] = arguments[i];
				}
				try 
				{
					method.invoke(api.newInstance(), obj);
				} 
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| InstantiationException e) 
				{
					System.out.println("Nie mo¿na wywo³aæ ¿¹danej metody");
					e.printStackTrace();
					System.exit(-1);
				}
				break;
			}
		}
	}
	

}
