package edu.missouri.cs.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Test {

	public static void main(String[] args) throws TransformerException,
			IOException, ParserConfigurationException, SAXException {
		URL url = new URL(
				"http://rss.accuweather.com/rss/liveweather_rss.asp?metric=1&locCode=65201");
		URLConnection conn = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn
				.getInputStream()));
		String inputLine;
		StringBuilder builder = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			builder.append(inputLine.trim());
		}
		DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
		Document dom = dBuilder.parse(new InputSource(new StringReader(builder.toString())));
		Element rssElement = dom.getDocumentElement();
		NodeList channels = rssElement.getChildNodes();
		NodeList nodeList = ((Element)channels.item(0)).getElementsByTagName("item");
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < nodeList.getLength()-1; i++) {
			Node item = nodeList.item(i);
			NodeList childNodes = item.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node child = childNodes.item(j);
				if(child.getNodeName().equalsIgnoreCase("title")){
					String nodeValue = child.getFirstChild().getNodeValue();
					output.append(nodeValue+" : ");
				}
				if(child.getNodeName().equalsIgnoreCase("description")){
					String nodeValue = child.getFirstChild().getNodeValue();
					String str = nodeValue.substring(0, nodeValue.indexOf("<"));
					output.append(str);
					output.append("\n");
				}								
			}
			
		}
		System.out.println(output.toString());
	}
}
