package in.imcod.whitebear.authentication;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import sun.misc.BASE64Encoder;

import in.imcod.whitebear.rest.ConnectorResponse;
import in.imcod.whitebear.rest.HttpConnector;

public class Authenticator {

	private static final String DEF_PORT = "80";
	private HttpConnector con;
	private String host;
	private String port;
	private String domain;
	private String project;
	private Map<String, String> cookies = new HashMap<String, String>();
	
	
	public Authenticator(String host,String port,String domain,String project){
		this.host = host;
		this.port = port;
		this.domain = domain;
		this.project = project;
		
		con = HttpConnector.getInstance().init(cookies, host, port, domain, project);
	}
	
	public Authenticator(String host,String domain,String project){
		this(host,DEF_PORT,domain,project);
	}
	
	public boolean login(String userName,String password) throws Exception{
		String authenticationPoint = this.isAuthenticated();
		if(authenticationPoint != null){
			return this.login(authenticationPoint,userName, password);
		}
		return true;
	}
	
	public String isAuthenticated() throws Exception {
		String isAuthenticateUrl = con.buildUrl("qcbin/rest/is-authenticated");
		String ret;
		ConnectorResponse response = con.httpGet(isAuthenticateUrl, null, null);
		//authenticated
		if(response.getStatusCode() == HttpURLConnection.HTTP_OK){
			ret = null;
		}
		//If not authenticated
		else if(response.getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED){
			Iterable<String> authenticationHeader = 
					response.getHeaders().get("WWW-Authenticate");
			String url = authenticationHeader.iterator().next().split("=")[1];
			url = url.replace("\"", "");
			url += "/authenticate";
			ret = url;
		}
		//other errors
		else{
			throw response.getException();
		}
		return ret;
	}

	public boolean login(String loginUrl,String userName,String password) throws Exception{
		byte[] credentials = (userName + ":" + password).getBytes();
		String encodCreds = "Basic "+ new BASE64Encoder().encode(credentials);
		Map<String, String> map = new HashMap<String, String>();
		map.put("Authorization", encodCreds);
		ConnectorResponse response = con.httpGet(loginUrl, null, map);
		boolean ret = response.getStatusCode() == HttpURLConnection.HTTP_OK;
		return ret;
	}
	
	public boolean logout() throws Exception{
		ConnectorResponse response = con.httpGet(con.buildUrl("qcbin/authentication-point/logout"), null, null);
		boolean ret = response.getStatusCode() == HttpURLConnection.HTTP_OK;
		return ret;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}
}
