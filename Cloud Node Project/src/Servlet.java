import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Servlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private HashMap<String, HashMap<String, String>> buckets;

	public Servlet() {
		super();
		initDatabase();
	}

	private void initDatabase() {
		buckets = new HashMap<String, HashMap<String, String>>();
		String bucket1 = "kausBucket1";
		HashMap<String, String> innerMap = new HashMap<String, String>();
		innerMap.put("kausResource1", "This is resource 1");
		innerMap.put("kausResource2", "This is resource 2");
		buckets.put(bucket1, innerMap);
	}

	private void initNode(HttpServletResponse resp) {
		HttpURLConnection connection = null;
		try {
			String trackerURL = "http://virtual40.cs.missouri.edu:12345/wakeup";
			String nodeURL = "node=http://virtual33.cs.missouri.edu:8080/project6/";
			String fullURL = trackerURL + "?" + nodeURL;
			URL URLtracker = new URL(fullURL);
			connection = (HttpURLConnection) URLtracker.openConnection();
			connection.setRequestMethod("POST");
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String inputLine, inputLines = "";
			while ((inputLine = in.readLine()) != null)
				inputLines += inputLine + "<br />";
			in.close();
			resp.setContentType("text/html");
			PrintWriter output = resp.getWriter();
			output.println(inputLines);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String servletPath = req.getServletPath();
		PrintWriter out = resp.getWriter();
		resp.setContentType("text/html");

		if (servletPath.equals("/")) {
			resp.setStatus(HttpServletResponse.SC_OK);
			out.println("<h1>Buckets on this node are</h1><ul>");
			for (String bucket : buckets.keySet()) {
				out.println("<li><a href=" + bucket + ">" + bucket
						+ "</a></li>");
				out.println("<br/>");
			}
			out.println("</ul>");

		} else if (servletPath.equals("/register")) {
			initNode(resp);
		} else if (servletPath.equalsIgnoreCase("/registerBuckets")) {
			declareBuckets(req, resp);
		} else if (servletPath.startsWith("/whereis/")) {
			resp.sendRedirect("http://virtual40.cs.missouri.edu:12345"
					+ servletPath);
		} else {
			String[] split = servletPath.split("/");
			if (split.length > 3) {
				// resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				out.println("<h1>404");
				out.println("<br/>");
				out
						.println("no resource by that name " + servletPath
								+ "</h1>");
			} else if (split.length > 2) {
				String resourceName = split[2];
				String bucketName = split[1];
				for (String bucket : buckets.keySet()) {
					if (bucket.equals(bucketName)) {
						HashMap<String, String> resourceMap = buckets
								.get(bucketName);
						if (resourceMap.containsKey(resourceName))
							out.println(resourceMap.get(resourceName));
					}
				}
			} else if (split.length > 1) {
				resp.setStatus(HttpServletResponse.SC_OK);
				String bucketName = split[1];
				if (buckets.containsKey(bucketName)) {
					out.println("<h1>Resource in bucket " + bucketName
							+ "</h1><ul>");
					HashMap<String, String> resourceMap = buckets
							.get(bucketName);
					for (String resource : resourceMap.keySet()) {
						out.println("<li><a href=\"/project6/" + bucketName
								+ "/" + resource + "\">" + resource
								+ "</a></li>");
						out.println("<br/>");
					}
					out.println("</ul>");
				}
			}
		}
		out.println();
		out.flush();
		out.close();

	}

	private int declareBuckets(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		HttpURLConnection connection = null;
		for (String bucket : buckets.keySet()) {

			try {
				String trackerURL = "http://virtual40.cs.missouri.edu:12345/mine/";
				String bucketURL = "node=http://virtual33.cs.missouri.edu:8080/project6/";
				String fullURL = trackerURL + bucket + "?" + bucketURL;
				URL URLtracker = new URL(fullURL);
				connection = (HttpURLConnection) URLtracker.openConnection();
				connection.setRequestMethod("POST");
				connection.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String inputLine, inputLines = "";

				while ((inputLine = in.readLine()) != null)
					inputLines += inputLine + "<br />";
				in.close();
				resp.setContentType("text/html");
				PrintWriter output = resp.getWriter();
				output.println(inputLines);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return connection.getResponseCode();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// super.doPost(req, resp);
		String[] servletPath = req.getServletPath().split("\\/");
		String bucketName = servletPath[1];
		PrintWriter output = resp.getWriter();
		resp.setContentType("text/html");
		if (servletPath.length > 2 || servletPath.length < 2) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} else if (buckets.containsKey(bucketName)) {
			resp.setStatus(HttpServletResponse.SC_OK);
			output.println("<h2>The bucket: " + bucketName
					+ " already exists on this node.</h2>");
		} else {
			String trackerURL = "http://virtual40.cs.missouri.edu:12345/mine/";
			String bucketURL = "node=http://virtual33.cs.missouri.edu:8080/project6/";
			String fullURL = trackerURL + bucketName + "?" + bucketURL;
			URL URLtracker = new URL(fullURL);
			HttpURLConnection trackerConn = (HttpURLConnection) URLtracker
					.openConnection();
			trackerConn.setRequestMethod("POST");
			trackerConn.connect();
			int responseCode = trackerConn.getResponseCode();

			if (responseCode == HttpServletResponse.SC_OK) {
				buckets.put(bucketName, new HashMap<String, String>());
				resp.setStatus(HttpServletResponse.SC_CREATED);
				output.println("<h2>The bucket: " + bucketName
						+ " has been created successfully.</h2>");
			} else if (responseCode == HttpServletResponse.SC_CONFLICT) {

				resp.setStatus(HttpServletResponse.SC_CONFLICT);
				output.println("<h2>The bucket: " + bucketName
						+ " already exists on another node.</h2>");
			}
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// super.doPost(req, resp);
		String[] servletPath = req.getServletPath().split("\\/");
		if (servletPath.length > 3 || servletPath.length == 2) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.setContentType("text/html");
			PrintWriter output = resp.getWriter();
			output.println("<h2>BAD request formed.</h2>");

		} else if (servletPath.length == 3) {
			String bucketName = servletPath[1];
			String resourceName = servletPath[2];
			if (buckets.containsKey(bucketName)) {
				HashMap<String, String> bucket = buckets.get(bucketName);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						req.getInputStream()));
				String inputLine, inputLines = "";
				while ((inputLine = in.readLine()) != null)
					inputLines += inputLine;
				in.close();

				if (bucket.containsKey(resourceName)) {
					bucket.put(resourceName, inputLines);
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setContentType("text/html");
					PrintWriter output = resp.getWriter();
					output.println("<h2>The content in resource: "
							+ resourceName + " on bucket: " + bucket
							+ " has been updated.</h2>");
				} else {
					bucket.put(resourceName, inputLines);
					resp.setStatus(HttpServletResponse.SC_CREATED);
					resp.setContentType("text/html");
					PrintWriter output = resp.getWriter();
					output
							.println("<h2>New resource: " + resourceName
									+ " is created on the bucket: " + bucket
									+ " </h2>");

				}
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				resp.setContentType("text/html");
				PrintWriter output = resp.getWriter();
				output.println("<h2>Bucket: " + bucketName
						+ " not exist on this node.</h2>");
			}
		}
	}
}