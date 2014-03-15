package in.imcod.whitebear.rest;

import in.imcod.whitebear.enums.EntityType;
import in.imcod.whitebear.enums.RequestType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpConnector {
	
	private static HttpConnector connector = new HttpConnector();
	
	private Map<String, String> cookies = new HashMap<String, String>();
	private String host;
	private String port;
	private String domain;
	private String project;
	
	private HttpConnector() {}
	
	public HttpConnector init(
			Map<String, String> cookies,String host,
				String port,String domain,String project){
		this.cookies = cookies;
		this.host = host;
		this.port = port;
		this.domain = domain;
		this.project = project;
		return this;
	}
	
	
	public static HttpConnector getInstance(){
		return connector;
	}
	

	public String buildEntityCollectionUrl(EntityType entity){
		StringBuilder path = new StringBuilder();
		path.append("qcbin/rest/domains/").append(this.domain)
		.append("/projects/").append(this.project)
		.append("/").append(entity).append("s");
		
		return buildUrl(path.toString());
	}
	
	public String buildUrl(String path){
		return String.format("http://%1$s:%2$s/%3$s",host,port,path);
	}
	
	public ConnectorResponse httpPut(String url, byte[] data, Map<String, String> headers) throws Exception{
		return doHttp(RequestType.PUT,url,null,data,headers,cookies);
	}
	
	public ConnectorResponse httpGet(String url, String queryString, Map<String, String> headers) throws Exception{
		return doHttp(RequestType.GET,url,queryString,null,headers,cookies);
	}
	
	public ConnectorResponse httpPost(String url, byte[] data, Map<String, String> headers) throws Exception{
		return doHttp(RequestType.POST,url,null,data,headers,cookies);
	}
	
	public ConnectorResponse httpDelete(String url,Map<String, String> headers) throws Exception{
		return doHttp(RequestType.DELETE,url,null,null,headers,cookies);
	}
	
	private ConnectorResponse doHttp(RequestType type,
			String url, String queryString, byte[] data,
				Map<String, String> headers, Map<String, String> cookies)throws Exception{
		
		if(queryString != null && !queryString.isEmpty()){
			url+= "?"+queryString;
		}
		
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setRequestMethod(type.name());
		prepareHttpRequest(con,headers,data,getCookieString());
		con.connect();
		
		ConnectorResponse resp = getResponse(con);
		updateCookies(resp);
		
		return resp;
	}

	private void updateCookies(ConnectorResponse resp) {
		Iterable<String> newCookies = resp.getHeaders().get("Set-Cookie");
		if(newCookies != null){
			for(String cookie : newCookies){
				int equalIndex = cookie.indexOf("=");
				int semicolonIndex = cookie.indexOf(";");
				String cookieName = cookie.substring(0, equalIndex);
				String cookieValue = cookie.substring(equalIndex+1,semicolonIndex);
				cookies.put(cookieName, cookieValue);
			}
		}
	}

	private ConnectorResponse getResponse(HttpURLConnection con)throws Exception {
		ConnectorResponse resp = new ConnectorResponse();
		resp.setStatusCode(con.getResponseCode());
		resp.setHeaders(con.getHeaderFields());
		InputStream in;
		
		try{
			in = con.getInputStream();
		}catch(Exception ex){
			in = con.getErrorStream();
			resp.setException(ex);
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int read;
		while((read = in.read(buf, 0, 1024)) > 0){
			bos.write(buf, 0, read);
		}
		resp.setData(bos.toByteArray());
		
		return resp;
	}

	/**
	 * @param con
	 * @param headers
	 * @param data
	 * @param cookieString
	 * @throws IOException
	 */
	private void prepareHttpRequest(HttpURLConnection con,
			Map<String, String> headers, byte[] data, String cookieString)throws IOException {
		String contentType = null;
		if(cookieString != null && !cookieString.isEmpty()){
			con.setRequestProperty("Cookie", cookieString);
		}
		
		if(headers != null){
			contentType = headers.remove("Content-Type");
			Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String, String> header = iterator.next();
				con.setRequestProperty(header.getKey(), header.getValue());
			}
		}
		
		if((data != null) && (data.length > 0)){
			con.setDoOutput(true);
			if(contentType != null){
				con.setRequestProperty("Content-Type", contentType);
			}
			OutputStream out = con.getOutputStream();
			out.write(data);
			out.flush();
			out.close();
		}
	}

	private String getCookieString() {
		StringBuilder br = new StringBuilder();
		if(cookies!=null && !cookies.isEmpty()){
			Set<Entry<String, String>> entries = cookies.entrySet();
			for(Entry<String, String> entry : entries){
				br.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
			}
		}
		return br.toString();
	}
	
	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
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
}
