package net.bir.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;


public class LongConverter implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		System.out.println ("getAsObject: arg1=" + arg1 + ", arg2=" + arg2);
		return Long.valueOf(arg2);
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		System.out.println ("getAsString: arg1=" + arg1 + ", arg2=" + arg2);
		return arg2.toString(); 
	}

}
