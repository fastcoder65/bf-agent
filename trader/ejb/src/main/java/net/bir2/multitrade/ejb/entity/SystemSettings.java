package net.bir2.multitrade.ejb.entity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import java.io.StringWriter;

@XmlRootElement(name="SystemSettings")
public class SystemSettings {

	private Integer delayPerMarket;
	
	private Double maxLossPerSelection;

	private Integer turnOnTimeOffsetHours;
	private Integer turnOnTimeOffsetMinutes;
	
	private Integer turnOffTimeOffsetHours;
	private Integer turnOffTimeOffsetMinutes;

	public Double getMaxLossPerSelection() {
		if (maxLossPerSelection == null)
			maxLossPerSelection = 10.0;

		return maxLossPerSelection;
	}

	public void setMaxLossPerSelection(Double maxLossPerSelection) {
		this.maxLossPerSelection = maxLossPerSelection;
	}

	public Integer getDelayPerMarket() {
/*		if (delayPerMarket == null) 
			delayPerMarket = 2;
*/		return delayPerMarket;
	}

	public void setDelayPerMarket(Integer delayPerMarket) {
		this.delayPerMarket = delayPerMarket;
	}

	public Integer getTurnOnTimeOffsetHours() {
		if (turnOnTimeOffsetHours == null)
			turnOnTimeOffsetHours = -2;
		return turnOnTimeOffsetHours;
	}

	public void setTurnOnTimeOffsetHours(Integer turnOnTimeOffsetHours) {
		this.turnOnTimeOffsetHours = turnOnTimeOffsetHours;
	}

	public Integer getTurnOnTimeOffsetMinutes() {
		if (turnOnTimeOffsetMinutes == null)
			turnOnTimeOffsetMinutes = -30;
		
		return turnOnTimeOffsetMinutes;
	}

	public void setTurnOnTimeOffsetMinutes(Integer turnOnTimeOffsetMinutes) {
		this.turnOnTimeOffsetMinutes = turnOnTimeOffsetMinutes;
	}

	public Integer getTurnOffTimeOffsetHours() {
		if (turnOffTimeOffsetHours == null)
			turnOffTimeOffsetHours = 0;
		
		return turnOffTimeOffsetHours;
	}

	public void setTurnOffTimeOffsetHours(Integer turnOffTimeOffsetHours) {
		this.turnOffTimeOffsetHours = turnOffTimeOffsetHours;
	}

	public Integer getTurnOffTimeOffsetMinutes() {
		if (turnOffTimeOffsetMinutes == null)
			turnOffTimeOffsetMinutes = -11;
		return turnOffTimeOffsetMinutes;
	}

	public void setTurnOffTimeOffsetMinutes(Integer turnOffTimeOffsetMinutes) {
		this.turnOffTimeOffsetMinutes = turnOffTimeOffsetMinutes;
	}
	

	public static SystemSettings unmarshall(InputStream in) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(SystemSettings.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

        return (SystemSettings)unmarshaller.unmarshal(in);
	}
	
	public String marshall() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(SystemSettings.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter wr = new StringWriter();
		marshaller.marshal(this, wr);
		String  result = wr.getBuffer().toString();
		System.out.println(result);
		return result;
	}
	
	public static void main(String[] args) throws JAXBException {
		System.out.println(new SystemSettings().marshall());
	}

	@Override
	public String toString() {
		return "SystemSettings [delayPerMarket=" + delayPerMarket
				+ ", maxLossPerSelection=" + maxLossPerSelection
				+ ", turnOffTimeOffsetHours=" + turnOffTimeOffsetHours
				+ ", turnOffTimeOffsetMinutes=" + turnOffTimeOffsetMinutes
				+ ", turnOnTimeOffsetHours=" + turnOnTimeOffsetHours
				+ ", turnOnTimeOffsetMinutes=" + turnOnTimeOffsetMinutes + ']';
	}
	
	
}