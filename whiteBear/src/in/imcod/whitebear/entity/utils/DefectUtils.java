package in.imcod.whitebear.entity.utils;

import in.imcod.whitebear.authentication.Authenticator;
import in.imcod.whitebear.common.utils.CommonUtils;
import in.imcod.whitebear.enums.EntityType;
import in.imcod.whitebear.rest.ConnectorResponse;
import in.imcod.whitebear.rest.HttpConnector;
import in.imcod.whitebear.stub.Entities;
import in.imcod.whitebear.stub.Entity;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefectUtils {

	private HttpConnector con;
	private Map<String, String> requestHeaders = new HashMap<String, String>();
	private Authenticator authenticator;

	public DefectUtils(Authenticator authenticator) {
		this.authenticator = authenticator;
		this.con = HttpConnector.getInstance();
	}

	/**
	 * @param status
	 *            - defect status to pull
	 * @param severity
	 *            - defect severity to pull
	 */
	public Entities listDefects(Map<String, String[]> filter) throws Exception {

		String filterVals[];
		StringBuilder queryStr = new StringBuilder("query={");

		if (filter != null) {
			Set<String> fields = filter.keySet();

			for (String field : fields) {
				queryStr.append(field).append("[");
				filterVals = filter.get(field);

				for (int i = 0; i < filterVals.length; i++) {
					queryStr.append(filterVals[i]);
					if (i < filterVals.length - 1) {
						queryStr.append("%20or%20");
					}
				}

				queryStr.append("];");
			}
		}

		queryStr.append("}");

		return listDefects(queryStr.toString());
	}

	/**
	 * Returns Entities(defects) returned based on the provided query String.
	 * 
	 * @param queryString
	 * @return
	 * @throws Exception
	 */
	public Entities listDefects(String queryString) throws Exception {

		// Check whether session is Authenticated.
		isAuthenticated();

		String defectsUrl = con.buildEntityCollectionUrl(EntityType.defect);

		requestHeaders.put("Accept", "application/xml");
		ConnectorResponse res = con.httpGet(defectsUrl, queryString,
				requestHeaders);

		// throw response error if any.
		if (res != null && res.getException() != null) {
			throw res.getException();
		}

		// unmarshall xml
		Entities defects = EntityMarshallingUtils.marshall(Entities.class,
				res.toString());

		return defects;

	}

	// Create a new defect
	public String createDefect(Map<String, String> fields) throws Exception {

		// Check whether session is Authenticated.
		isAuthenticated();

		// New Defect URL
		String defectsUrl = con.buildEntityCollectionUrl(EntityType.defect);

		// build entity
		String newDefect = CommonUtils.generateFieldXml(EntityType.defect,fields);

		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("Accept", "application/xml");
		ConnectorResponse response = con.httpPost(defectsUrl, newDefect.getBytes(),
				requestHeaders);

		// In case of exception
		if (response != null && response.getException() != null) {
			throw response.getException();
		}

		return response.getHeaders().get("Location").iterator().next();
	}

	// Update an existing defect
	public void updateDefect(String defectId, Map<String, String> fields)
			throws Exception {

		// Check whether session is Authenticated.
		isAuthenticated();

		// build defect url
		String defectUrl = con.buildEntityCollectionUrl(EntityType.defect) + "/"
				+ defectId;

		// generate updated fields xml
		String newFields = CommonUtils.generateFieldXml(EntityType.defect,fields);

		// lock the defect
		lock(defectUrl);

		// update defect
		ConnectorResponse resp = update(defectUrl, newFields);

		// unlock entity
		unlock(defectUrl);

		// throw in case of error
		if (resp != null && resp.getException() != null) {
			throw resp.getException();
		}

	}


	/**
	 * @param userLabel - User Label to appear in comments
	 * @param userId - ALM User Id
	 * @param oldComments - existing comments string
	 * @param newComments - New comments to add
	 * @return  - HTML string with new comments added.
	 */
	public String prepareComments(String userLabel, String userId,
			String oldComments, String newComments) {
		if(userId !=null && !userId.trim().equals("")){
			StringBuilder sb = new StringBuilder();
			sb.append("<div align='left'><font face='Arial'><span style='font-size:8pt'><br /></span></font><font face='Arial' color='#000080'><span style='font-size:8pt'><b>________________________________________</b></span></font><font face='Arial'><span style='font-size:8pt'><br /></span></font><font face='Arial' color='#000080'><span style='font-size:8pt'><b>");
			// append user label
			if (userLabel != null && !userLabel.trim().equals("")) {
				sb.append(userLabel);
			} else {
				sb.append(CommonUtils.toCamelcase(userId)).append(" ");
			}
			// append user id or email
			sb.append("&lt;").append(userId).append("&gt;");
			// append date
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			sb.append(dateFormat.format(new Date())).append(":");
	
			sb.append("</b></span></font><font face='Arial'><span style='font-size:8pt'>");
			newComments = newComments.replace("<", "&lt;").replace(">", "&gt;");
			sb.append(newComments);
			sb.append("</span></font></div>");
	
			oldComments = oldComments.replace("</body>", sb.toString() + "</body>");
	
			return "<![CDATA[" + oldComments + "]]>";
		}
		return "";
	}

	// read an existing defect
	public Entity readDefect(String defectId) throws Exception {

		// Check whether session is Authenticated.
		isAuthenticated();
		// build defect url
		String defectUrl = con.buildEntityCollectionUrl(EntityType.defect) + "/"
				+ defectId;
		requestHeaders.put("Accept", "text/html,application/xml;");
		ConnectorResponse res = con.httpGet(defectUrl, "", requestHeaders);

		// throw response error if any.
		if (res != null && res.getException() != null) {
			throw res.getException();
		}
		// unmarshall xml
		return EntityMarshallingUtils.marshall(Entity.class, res.toString());
	}

	/**
	 * @param entityUrl
	 *            to lock
	 * @return the locked entity xml
	 * @throws Exception
	 */
	public String lock(String entityUrl) throws Exception {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/xml");
		return con.httpPost(entityUrl + "/lock", null, requestHeaders)
				.toString();
	}

	/**
	 * @param entityUrl
	 *            to unlock
	 * @return
	 * @throws Exception
	 */
	public boolean unlock(String entityUrl) throws Exception {
		return con.httpDelete(entityUrl + "/lock", null).getStatusCode() == HttpURLConnection.HTTP_OK;
	}

	
	/**
	 * @param entityUrl
	 *            to update
	 * @param updatedEntityXml
	 *            new entity descripion. only lists updated fields. unmentioned
	 *            fields will not change.
	 * @return xml description of the entity on the serverside, after update.
	 * @throws Exception
	 */
	private ConnectorResponse update(String entityUrl, String updatedEntityXml)
			throws Exception {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("Accept", "application/xml");
		ConnectorResponse put = con.httpPut(entityUrl, updatedEntityXml.getBytes(),
				requestHeaders);
		return put;
	}

	private void isAuthenticated() throws Exception {
		if (authenticator.isAuthenticated() != null) {
			throw new Exception(
					"Session isn't Authenticated. Please authenticate the session using Authenticator Class.");
		}
	}
}