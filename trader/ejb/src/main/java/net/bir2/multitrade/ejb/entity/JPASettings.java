package net.bir2.multitrade.ejb.entity;

import javax.persistence.*;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

@Entity
@Table(name = "Settings")
public class JPASettings {

    @Transient
    private static final Logger log = Logger.getLogger(JPASettings.class.getName());

    private long id;
    @Lob
    private String settings;
    private SystemSettings systemSettings;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic(fetch = FetchType.EAGER)
    public String getSettings() {
        return settings;
    }

    public void setSettings(String _settings) {
        log.info("method 'JPASettings.setSettings' called, String _settings: " + _settings);
        if (_settings != null) {
            try {
                this.settings = _settings;
                log.info("this.settings: " + this.settings);
                systemSettings = SystemSettings.unmarshall(new ByteArrayInputStream(this.settings.getBytes("UTF-8")));
                log.info("SystemSettings.unmarshall: " + systemSettings);
            } catch (Exception e) {
                //log.log(Level.SEVERE, "Error on unmarshalling system settings, error:" , e);
                e.printStackTrace();
            }
        }
    }

    @Transient
    public SystemSettings getSystemSettings() {
        return systemSettings;
    }

    public void setSystemSettings(SystemSettings systemSettings) {
        this.systemSettings = systemSettings;
        try {
            this.settings = systemSettings.marshall();
            log.info("systemSettings.marshall: " + this.settings);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "JPASettings [systemSettings=" + systemSettings + ']';
    }


}
