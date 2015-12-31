package net.bir2.multitrade.ejb.entity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import java.io.StringWriter;

@XmlRootElement(name="ExchangeSettings")
public class ExchangeSettings {


	public static ExchangeSettings unmarshall(InputStream in) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ExchangeSettings.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

        return (ExchangeSettings)unmarshaller.unmarshal(in);
	}
	
	public String marshall() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ExchangeSettings.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter wr = new StringWriter();
		marshaller.marshal(this, wr);
		String  result = wr.getBuffer().toString();
		System.out.println(result);
		return result;
	}
	
	public static void main(String[] args) throws JAXBException {
		System.out.println(new ExchangeSettings().marshall());
	}


	
}