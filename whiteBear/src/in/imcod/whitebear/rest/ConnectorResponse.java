package in.imcod.whitebear.rest;

import java.util.Map;

public class ConnectorResponse {
	
	private Map<String, ? extends Iterable<String>> headers;
	private byte[] data;
	private Exception exception;
	private int statusCode = 0;
	
	public ConnectorResponse() {}
	public ConnectorResponse(
			Map<String, ? extends Iterable<String>> headers,
					byte[] data,
					Exception exception,
					int statusCode){
		super();
		this.headers = headers;
		this.data = data;
		this.exception = exception;
		this.statusCode = statusCode;
	}
	public Map<String, ? extends Iterable<String>> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, ? extends Iterable<String>> headers) {
		this.headers = headers;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	@Override
	public String toString() {
		return new String(this.data);
	}
}
