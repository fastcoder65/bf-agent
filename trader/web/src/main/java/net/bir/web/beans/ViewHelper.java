package net.bir.web.beans;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class ViewHelper {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	public static enum RichSkins {
		DEFAULT("Исходная"), emeraldTown("Изумрудный город"), blueSky(
				"Голубое небо"), wine("Вино"), japanCherry("Японская вишня"), ruby(
				"Рубин"), classic("Классика"), deepMarine("Бирюза");

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

	public ViewHelper() {
	}

	private String skin = null;

	public String getSkin() {
		if (this.skin == null) {
			String _skin = getUserSkin();
			if (_skin != null && _skin.trim().length() > 0) {
				log.fine("reading skin from cookie..");
				skin = _skin;
			} else {
				log.info("*** !!! try to set 'empty' skin !!!");
			}
		}
		return skin;
	}

	public void setSkin(String _skin) {
		if (_skin != null && _skin.trim().length() > 0) {
			String cookieSkin = getUserSkin();
			if (!_skin.equals(this.skin)  ) {

				if (this.skin == null) {
					if (cookieSkin != null) {
						this.skin = cookieSkin;
						log.info("set skin from '" +( this.skin == null ? "" :  this.skin.toString()) + "' to: '"
								+ cookieSkin.toString() + "'");

					} else {
						this.skin = _skin;
					}
				} else {
					log.info("set skin from '" +( this.skin == null ? "" :  this.skin.toString()) + "' to: '"
							+ _skin.toString() + "'");

					this.skin = _skin;
				}

			}/* else {
				log.info("*** !!! try to set the same skin !!!");
			}*/
		} else {
			log.info("*** !!! try to set 'empty' skin !!!");
		}
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
		log.info("theme changed to: \"" + newTheme + "\"");
		FacesContext context = FacesContext.getCurrentInstance();

		HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();

		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		Cookie themeCookie = null;
		try {
			themeCookie = new Cookie(RF_THEME, newTheme);
			themeCookie.setMaxAge(Integer.MAX_VALUE);
			String contextPath = (req!= null)? req.getContextPath():"credit";
			log.info("contextPath=" + contextPath);
			themeCookie.setPath(contextPath);

			response.addCookie(themeCookie);
			log.info("New theme \"" + newTheme + "\" saved in cookie.");
		} catch (Exception ex) {
			log.severe(ex.getMessage());
		}
	}

	public String getUserSkin() {
		String skinValue = "";
		String result = "DEFAULT";
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest) context
				.getExternalContext().getRequest();
		Cookie cookie[] = req.getCookies();

		if (cookie != null && cookie.length > 0) {
			log.fine("found cookies: " + cookie.length);

			for (int i = 0; i < cookie.length; i++) {
				log.fine("found cookie: " + cookie[i].getName());
				if (RF_THEME.equals(cookie[i].getName())) {
					skinValue = cookie[i].getValue() != null && cookie[i].getValue().trim().length()> 0 ? cookie[i].getValue(): null;
					log.fine("read theme \"" + skinValue + "\" from cookies");
					result = (skinValue != null) ? skinValue : result;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "ViewHelper [skin=" + skin + "]";
	}


}
