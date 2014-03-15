package in.imcod.whitebear.common.utils;

import in.imcod.whitebear.enums.EntityType;

import java.util.Map;
import java.util.Set;

public class CommonUtils {
	
	public static String generateFieldXml(EntityType entity, Map<String, String> fields) {

		if (fields != null) {
			Set<String> keys = fields.keySet();
			StringBuilder updateXml = new StringBuilder();
			updateXml.append("<Entity Type=\"").append(entity).append("\"><Fields>");
			for (String fieldName : keys) {
				updateXml.append("<Field Name=\"").append(fieldName)
						.append("\"><Value>").append(fields.get(fieldName))
						.append("</Value></Field>");
			}
			updateXml.append("</Fields></Entity>");

			return updateXml.toString();
		}
		return "<Entity Type=\""+entity+"\"><Fields></Fields></Entity>";
	}

	
	public static String toCamelcase(String word) {
		if (word != null && !word.trim().equals("")) {
			return word.substring(0, 1).toUpperCase()
					+ word.substring(1).toLowerCase();
		}
		return word;
	}


}
