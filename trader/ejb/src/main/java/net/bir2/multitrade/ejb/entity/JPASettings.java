package net.bir2.multitrade.ejb.entity;

import java.io.ByteArrayInputStream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.JAXBException;


@Entity
@Table(name = "Settings")
public class JPASettings  implements java.io.Serializable {
	
	private static final long serialVersionUID = -9154068267892580295L;

	private long id;

	private String settings;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@Lob
	public String getSettings() {
		return settings;
	}
	
	public void setSettings(String settings) {
		this.settings = settings;
		if(settings!=null) {
				try {
					systemSettings = SystemSettings.unmarshall(new ByteArrayInputStream(settings.getBytes("UTF-8")));
					System.out.println("SystemSettings.unmarshall: " + systemSettings);
				} catch (Exception e) {
					System.out.println("SystemSettings.unmarshall, error:" + e.getMessage());
				} 
		}
	}
	
	
	private SystemSettings systemSettings;
	
	@Transient
	public SystemSettings getSystemSettings() {
		return systemSettings;
	}
	
	public void setSystemSettings(SystemSettings systemSettings) {
		this.systemSettings = systemSettings;		
		try {
			this.settings = systemSettings.marshall();
			System.out.println("systemSettings.marshall: " + this.settings);
		} catch (JAXBException e) {
			
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "JPASettings [systemSettings=" + systemSettings + ']';
	}
	
	

}
