package pac1;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class GeneralAPI 
{
	
	protected List<Triple<String, String, String[]>> functions = createFunctionList();
	
	protected abstract List<Triple<String, String, String[]>> createFunctionList();
	
	protected  String printHelp()
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
	
	
	protected void parseAndInvoke(String[] argv, Class api) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
	{
		for (Triple<String, String, String[]> function : functions)
		{
			if(argv[0].equals(function.second()))
			{
				if(argv.length != function.third().length + 1)
				{
					throw new InputMismatchException();
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
						throw new InputMismatchException();
					}
				}
				Class<?> _api = Class.forName(api.getName());
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
				method.invoke(api.newInstance(), obj);
				break;
			}
		}
	}
	

}
