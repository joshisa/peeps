package example.nosql;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.http.HttpClient;
import org.ektorp.http.HttpResponse;
import org.ektorp.http.StdHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

@WebServlet(urlPatterns ="/faces", loadOnStartup=1)
public class Faces extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	// set default db connection credentials 
	String databaseHost = "user.cloudant.com";
	String user = "user";
	String password = "password";
	Integer port = 443;
	HttpClient httpClient;
	CouchDbConnector couchConnector;

	@Override
	public void init() throws ServletException {
		// 'VCAP_SERVICES' contains all the credentials of services bound to this application.
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		
		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			JSONObject obj;
			try {
				obj = (JSONObject) JSON.parse(VCAP_SERVICES);
			} catch (IOException e) {
				throw new ServletException(e);
			}
			String dbKey = null;
			Set<String> keys = obj.keySet();
			// Look for the VCAP key that holds the cloudant no sql db information
			for (String eachkey : keys) {				
				if (eachkey.contains("cloudantNoSQLDB")) {
					dbKey = eachkey;
					break;
				}
			}
			if (dbKey == null) {				
				throw new ServletException("Could not find cloudantNoSQLDB key in VCAP_SERVICES env variable");
			}

			JSONArray list = (JSONArray) obj.get(dbKey);
			obj = (JSONObject) list.get(0);		
			String serviceName = (String)obj.get("name");
			System.out.println("Service Name - "+serviceName);
			
			obj = (JSONObject) obj.get("credentials");

			databaseHost = (String) obj.get("host");
			user = (String) obj.get("username");
			password = (String) obj.get("password");
			port = ((Long) obj.get("port")).intValue();
		}
		else {
			System.out.println("VCAP_SERVICES not found, using hard-coded defaults");
		}
		
		httpClient = new StdHttpClient.Builder()
			.host(databaseHost)
			.port(port)
			.username(user)
			.password(password)
			.enableSSL(true)
			.relaxedSSLSettings(true)
			.build();
//		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
//		couchConnector = new StdCouchDbConnector("faces-small", dbInstance);		
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String inputQuery = request.getParameter("q");
		if (!inputQuery.endsWith("\"")){
			inputQuery += "*";
		}
		
		String query = URLEncoder.encode(request.getParameter("q"), "UTF-8");
		
		response.setContentType("application/json");
		response.setHeader("debug", "/faces100k/_design/employees/_search/faces?q="+query+"&limit=36");
		HttpResponse httpResponse = httpClient.get("/faces100k/_design/employees/_search/faces?q="+query+"&limit=36");
		IOUtils.copy(httpResponse.getContent(), response.getOutputStream());	
	}	
	
}
