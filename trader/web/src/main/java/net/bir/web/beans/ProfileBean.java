package net.bir.web.beans;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlInputHidden;

import net.bir2.multitrade.ejb.entity.JPASettings;
import net.bir2.multitrade.ejb.entity.SystemSettings;
import net.bir2.multitrade.ejb.entity.Uzer;
import net.bir.ejb.session.settings.SettingsService;

import net.bir.util.WebUtils;

@ManagedBean
@SessionScoped
public class ProfileBean extends BaseBean {

	private Uzer profileUser;
	private JPASettings settings;

	@EJB(name = "BIR/SettingsServiceBean/local")
	protected SettingsService settingsService;

	private int messagePanelSelector ;
	
	public int getMessagePanelSelector() {
		return messagePanelSelector;
	}

	public void setMessagePanelSelector(int messagePanelSelector) {
		this.messagePanelSelector = messagePanelSelector;
	}

	public Uzer getProfileUser() {
		if (profileUser == null)
			profileUser = marketService.getCurrentUser();	
		return profileUser;
	}

	public void setProfileUser(Uzer profileUser) {
		this.profileUser = profileUser;
	}

	public void saveProfileUser() {
	 log.info("save user profile..");
	 try {
	 this.profileUser = marketService.merge(this.profileUser);
	 log.info("user profile saved.");
	 WebUtils.addContextInfoMessage("user profile saved.");
	 } catch (Exception e) {
		log.severe(e.getMessage());
		WebUtils.addContextError("Uzer Profile saving error: " + e.getMessage() );
	 }
	}
	
	public JPASettings getSettings() {
		if (settings == null) {
			settings = settingsService.getJPASettings();
			
		   if (settings == null) {
			   log.info("initialize system settings..");
			   settings = new JPASettings();
			   SystemSettings systemSettings = new SystemSettings();
			   log.info(systemSettings.toString());
			   settings.setSystemSettings(systemSettings);
			    settingsService.saveJPASettings(settings);
			    settings = settingsService.getJPASettings();
		   } else
		   
		 log.info("load settings: " + settings);	
		}	
		return settings;
	}

	public void setSettings(JPASettings settings) {
		this.settings = settings;
	}

	public void saveSettings() {
		 log.info("save system settings..");
		 this.settings.setSystemSettings(this.settings.getSystemSettings());
		 settingsService.saveJPASettings(this.settings);
		 log.info("system settings saved.");
		 WebUtils.addContextInfoMessage("system settings saved.");
	}

	private HtmlInputHidden selectorValue = new HtmlInputHidden();

	
	public HtmlInputHidden getSelectorValue() {
		return selectorValue;
		
	}

	public void setSelectorValue(HtmlInputHidden selectorValue) {
		this.selectorValue = selectorValue;
		System.out.println ("this.selectorValue.getValue()="+ this.selectorValue) ;
	}

	public void changeUserPassword() {
		messagePanelSelector = 2;
		 log.info("change Uzer Password..");
		if (this.password != null && this.password.trim().length()> 0) {  
			if (this.password.equals(profileUser.getPassword())) {
			  if (this.newPassword != null && this.repeatNewPassword != null && this.newPassword.equals(this.repeatNewPassword)) {
				  profileUser.setPassword(this.newPassword);
				  profileUser = marketService.merge(profileUser);
				  log.info("Uzer Password changed.");
				  WebUtils.addContextInfoMessage("Uzer Password changed.");
			  } else {
				  WebUtils.addContextError("New Password and Repeat New Password mismatch!");
			  }
			} else {
				WebUtils.addContextError("Old Uzer Password mismatch!");
			}
		} else {
		  WebUtils.addContextError("Old Uzer Password is empty!");	
		}
	}

	public void changeExternalPassword() {
		messagePanelSelector = 3;
		
			if (this.password != null && this.password.trim().length()> 0) {  
				if (this.password.equals(profileUser.getExPassword())) {
				  if (this.newPassword != null && this.repeatNewPassword != null && this.newPassword.equals(this.repeatNewPassword)) {
					  log.info("change External Password..");
					  profileUser.setExPasswordDec(this.newPassword);
					  profileUser = marketService.merge(profileUser);
					  log.info("External Password changed.");
					  WebUtils.addContextInfoMessage("External Password changed.");
				  } else {
					  WebUtils.addContextError("New Password and Repeat New Password mismatch!");
				  }
				} else {
					WebUtils.addContextError("Old External Password mismatch!");
				}
			} else {
			  WebUtils.addContextError("Old External Password is empty!");	
			}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getRepeatNewPassword() {
		return repeatNewPassword;
	}

	public void setRepeatNewPassword(String repeatNewPassword) {
		this.repeatNewPassword = repeatNewPassword;
	}

	private String password;
	private String newPassword;
	private String repeatNewPassword;
	
}
