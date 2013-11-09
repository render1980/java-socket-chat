package xml;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XmlParser {
	private static String host;
	private static int port;
	private static String dbhost;
	private static int dbport;
	private static Logger logger = Logger.getLogger(XmlParser.class.getName());

	private XmlParser() {
		
	}
	
	public static String getDBhost() {
		return dbhost;
	}

	public static void setDBhost(String dBhost) {
		dbhost = dBhost;
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		XmlParser.host = host;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		XmlParser.port = port;
	}

	public static int getDBport() {
		return dbport;
	}

	public static void setDBport(int dBport) {
		dbport = dBport;
	}
	
	public static void parseXmlFile() {

		try {

			File fXmlFile = new File("data.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("Host");
			Node nNode = nList.item(0);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				setHost(eElement.getTextContent());

			}

			NodeList nList2 = doc.getElementsByTagName("Port");
			nNode = nList2.item(0);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				setPort(Integer.parseInt(eElement.getTextContent()));

			}
			
			NodeList nList3 = doc.getElementsByTagName("DBHost");
			nNode = nList3.item(0);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				setDBhost(eElement.getTextContent());

			}
			
			NodeList nList4 = doc.getElementsByTagName("DBPort");
			nNode = nList4.item(0);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				setDBport(Integer.parseInt(eElement.getTextContent()));

			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}