package net.bir.ejb.session.settings;

import javax.ejb.Local;
import net.bir2.multitrade.ejb.entity.JPASettings;

@Local
public interface SettingsService {
	
	JPASettings getJPASettings();
	
	void saveJPASettings(JPASettings settings);
	
}
