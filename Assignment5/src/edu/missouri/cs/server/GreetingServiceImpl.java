package edu.missouri.cs.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.missouri.cs.client.GreetingService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private StreamResult result;

	public String greetServer(String input) throws IllegalArgumentException {
		URL url;
		try {
			url = new URL(
					"http://rss.accuweather.com/rss/liveweather_rss.asp?metric=1&locCode="
							+ input);
			URLConnection conn = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String inputLine;
			StringBuilder builder = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				builder.append(inputLine.trim());
			}
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(
					"data.xsl"));
			Writer outWriter = new StringWriter();
			result = new StreamResult(outWriter);
			transformer.transform(new StreamSource(new StringReader(builder
					.toString())), result);
		} catch (MalformedURLException e) {
			return e.getLocalizedMessage();
		} catch (IOException e) {
			return e.getLocalizedMessage();
		} catch (TransformerException e) {
			return e.getLocalizedMessage();
		}

		/*
		 * returnescapeHtml(
		 * "<div style='width: 160px; height: 600px; background-image: url( http://vortex.accuweather.com/adcbin/netweather_v2/backgrounds/clouds_160x600_bg.jpg ); background-repeat: no-repeat; background-color: #;' ><div id='NetweatherContainer' style='height: 585px;' ><script src='http://netweather.accuweather.com/adcbin/netweather_v2/netweatherV2ex.asp?partner=netweather&tStyle=normal&logo=1&zipcode="
		 * + request +
		 * "&lang=eng&size=15&theme=clouds&metric=0&target=_self'></script></div><div style='text-align: center; font-family: arial, helvetica, verdana, sans-serif; font-size: 10px; line-height: 15px; color: #;' ><a style='color: #' href='http://www.accuweather.com/us/MO/COLUMBIA/65201/city-weather-forecast.asp?partner=accuweather&traveler=0' >Weather Forecast</a> | <a style='color: #' href='http://www.accuweather.com/maps-satellite.asp' >Weather Maps</a></div></div>"
		 * );
		 */
		return escapeHtml(result.getWriter().toString());
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
