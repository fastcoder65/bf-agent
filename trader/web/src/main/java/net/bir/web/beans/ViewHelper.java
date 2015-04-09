 package net.bir.web.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ViewHelper extends BaseBean implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public ViewHelper() {
	}

	private String skin = null;

	public String getSkin() {
		if (skin != null) {
			log.fine("Get: found: " + skin);
		}

		if (getUserSkin() != null) { // && !skin.equals(getUserSkin())
		  log.fine("reading skin from cookie..");	
		  skin = getUserSkin();	
		}
		return skin;
	}

	public void setSkin(String skin) {
		if (skin != null) {
			log.fine("Set: found: " + skin);
		}

		log.fine("set skin to: "+ skin);
		this.skin = skin;
	}

	private List<SelectItem> skins = null;

	public List<SelectItem> getSkins() {

		if (skins == null) {
			skins = new ArrayList<SelectItem>();
			skins.add(new SelectItem(RichSkins.DEFAULT, RichSkins.DEFAULT
					.getName()));
			skins.add(new SelectItem(RichSkins.emeraldTown,
					RichSkins.emeraldTown.getName()));
			skins.add(new SelectItem(RichSkins.blueSky, RichSkins.blueSky
					.getName()));
			skins.add(new SelectItem(RichSkins.wine, RichSkins.wine.getName())); // wine
			skins.add(new SelectItem(RichSkins.japanCherry,
					RichSkins.japanCherry.getName())); // japanCherry
			skins.add(new SelectItem(RichSkins.ruby, RichSkins.ruby.getName()));// ruby
			skins.add(new SelectItem(RichSkins.classic, RichSkins.classic
					.getName()));// classic
			skins.add(new SelectItem(RichSkins.deepMarine, RichSkins.deepMarine
					.getName()));// deepMarine
		}
		return skins;
	}

	/*
	 * predefined Rich Faces skins: DEFAULT, plain, emeraldTown,
	 * blueSky,wine,japanCherry,ruby,classic,deepMarine
	 */

	public static enum RichSkins {
		DEFAULT("DEFAULT"), emeraldTown("emeraldTown"), blueSky(
				"Blue Sky"), wine("wine"), japanCherry("Japan cherry"), ruby(
				"Ruby"), classic("classic"), deepMarine("Deep marine");

		private String name;

		private RichSkins(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private Map<String, Integer> rowCounts = new HashMap<String, Integer>();

	public Map<String, Integer> getRowCounts() {
		return rowCounts;
	}

	public void setRowCounts(Map<String, Integer> rowCounts) {
		this.rowCounts = Collections.unmodifiableMap(rowCounts);
	}

	public static String RF_THEME = "RF_THEME";

	public void selectThemeChange(ValueChangeEvent vce) {
		String newTheme = (String) vce.getNewValue();
		log.fine("theme changed to: \"" + newTheme + "\"");
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest) context
				.getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		Cookie themeCookie;
		try {
			themeCookie = new Cookie(RF_THEME, newTheme);
			themeCookie.setMaxAge(Integer.MAX_VALUE);
			String contextPath = (req != null ? req.getContextPath() : "");
			// log.debug("contextPath=" + contextPath);
			themeCookie.setPath(contextPath);
			response.addCookie(themeCookie);
			log.info("New theme \"" + newTheme + "\" saved in cookie.");
		} catch (Exception ex) {
			log.severe(ex.getMessage());
		}
	}

	public String getUserSkin() {
		String skinValue = null;
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest) context
				.getExternalContext().getRequest();
		Cookie cookie[] = req.getCookies();

		if (cookie != null && cookie.length > 0) {
			log.fine("found cookies: " + cookie.length);
            for (Cookie aCookie : cookie) {
                log.fine("found cookie: " + aCookie.getName());
                if (RF_THEME.equals(aCookie.getName())) {
                    skinValue = aCookie.getValue();
                    log.fine("read theme \"" + skinValue + "\" from cookies");
                    break;
                }
            }
		}
		return skinValue;
	}

}
