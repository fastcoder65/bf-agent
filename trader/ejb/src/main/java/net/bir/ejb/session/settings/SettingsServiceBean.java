package net.bir.ejb.session.settings;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.bir2.multitrade.ejb.entity.JPASettings;

@Stateless
@Local({SettingsService.class})
public class SettingsServiceBean implements SettingsService {
	
    @PersistenceContext
    private EntityManager em;
    
	public JPASettings getJPASettings() {
        return em.find(JPASettings.class, (long) 1);
	}
	
	public void saveJPASettings(JPASettings settings) {
		em.merge(settings);
	}
	
}
