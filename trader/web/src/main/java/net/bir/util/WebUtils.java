package net.bir.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class WebUtils {

	public static void addContextError(String msg) {
		addContextMessage(msg, FacesMessage.SEVERITY_ERROR);
	}

	public static void addContextInfoMessage(String msg) {
		addContextMessage(msg, FacesMessage.SEVERITY_INFO);
	}

	public static void addContextWarnMessage(String msg) {
		addContextMessage(msg, FacesMessage.SEVERITY_WARN);

	}

	public static void addContextMessage(String msg, FacesMessage.Severity type) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(type, msg, ""));
	}

	public static Object getManagedBean(final String theBeanName) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (ctx == null) {
		 System.out.println("FacesContext is null");	
		}
		return (ctx == null)? null: getManagedBean(theBeanName, ctx);
	}

	public static Object getManagedBean(final String theBeanName,
			FacesContext ctx) {
		System.out.println ("looking up managed bean "+ theBeanName + " ...");
		
		final Object returnObject = ctx.getELContext().getELResolver()
				.getValue(ctx.getELContext(), null, theBeanName);

		
//		final Object returnObject =	ctx.getApplication().getVariableResolver().resolveVariable(ctx, theBeanName);

		if (returnObject == null) {
			throw new RuntimeException("Bean with name " + theBeanName
					+ " was not found.");
		}
		return returnObject;
	}

}
