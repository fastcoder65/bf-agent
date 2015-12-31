package net.bir2.multitrade.ejb.entity;

import java.io.ByteArrayInputStream;

import javax.persistence.*;
import javax.xml.bind.JAXBException;

@Entity
@Table(name = "Settings")
public class JPASettings {

	private long id;



	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Lob
	@Basic(fetch=FetchType.EAGER)
	private String settings;

	public String getSettings() {
		return settings;
	}

	public void setSettings(String _settings) {
		System.out.println("String _settings: " + _settings);
		this.settings = _settings;
		if(this.settings != null) {
			try {
				systemSettings = SystemSettings.unmarshall(new ByteArrayInputStream(this.settings.getBytes("UTF-8")));

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
