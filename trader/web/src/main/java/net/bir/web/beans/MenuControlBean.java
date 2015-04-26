package net.bir.web.beans;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class MenuControlBean extends BaseBean {

	public static final String MAIN = "main";
	public static final String PROFILE = "profile";
	
	public String main() {
		return MAIN; 	
	}

	public String profile () {
		return PROFILE;	
	}
	
	public String exit() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		try {
			session.invalidate();
			log.info("user logout.");
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
		return "login";
	}

}
