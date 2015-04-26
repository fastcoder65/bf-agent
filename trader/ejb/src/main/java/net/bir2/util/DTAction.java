package net.bir2.util;

import java.util.Calendar;
import java.util.Date;

public class DTAction {

	public static Date getTimeFormBegin(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		setTimeFromBegin(c);
		return c.getTime();
	}
	

	public static Date getTimeToEnd(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		setTimeToEnd(c);
		return c.getTime();
	}
	

	public static Date getTimeToNoon(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		setTimeToNoon(c);
		return c.getTime();
	}
	
	/**
	 * ���������� ����� � 00:00:00
	 * 
	 * @param c
	 *            ��������� � ������������� �����
	 */
	public static void setTimeFromBegin(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 00);
		c.set(Calendar.MINUTE, 00);
		c.set(Calendar.SECOND, 00);
		c.set(Calendar.MILLISECOND, 00);
	}
	
	public static Date setTimeFromBegin(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		setTimeFromBegin(c);
		return c.getTime();
	}
	/**
	 * ���������� ����� � 23:59:59
	 * 
	 * @param c
	 *            ��������� � ������������� �����
	 */
	
	public static void setTimeToEnd(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
	}
	
	public static Date setTimeToEnd(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		setTimeToEnd(c);
		return c.getTime();	
	}
	
	/**
	 * ���������� ����� � 12:00:00
	 * 
	 * @param c
	 *            ��������� � ������������� �����
	 */
	
	public static void setTimeToNoon(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 12);
		c.set(Calendar.MINUTE, 00);
		c.set(Calendar.SECOND, 00);
		c.set(Calendar.MILLISECOND, 00);
		
	}
	
}
