package in.imcod.whitebear.entity.utils;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class EntityMarshallingUtils {
	
	private EntityMarshallingUtils() {}
	
	@SuppressWarnings("unchecked")
	public static <T> T marshall(Class<T> c, String xml) throws JAXBException{
		T res;
		if(c == xml.getClass()){
			res = (T) xml;
		}else{
			JAXBContext ctx = JAXBContext.newInstance(c);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			res = (T) unmarshaller.unmarshal(new StringReader(xml));
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> String unmarshall(Class<T> c, Object o) throws JAXBException{
		JAXBContext ctx = JAXBContext.newInstance(c);
		Marshaller marshaller = ctx.createMarshaller();
		StringWriter entityXml = new StringWriter();
		marshaller.marshal(o, entityXml);
		
		return entityXml.toString();
	}
}
