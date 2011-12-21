package com.jugnoo.videos.util;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Utility {

	private static ThreadLocal<SimpleDateFormat> xmlDateSdf = new ThreadLocal<SimpleDateFormat>();

	public static final String EMPTY_STRING = "-";

	public static final String ERROR_8001 = "8001";

	public static final String ERROR_8002 = "8002";

	public static final String ERROR_8003 = "8003";

	public static final String ERROR_8004 = "8004";

	public static final String ERROR_8005 = "8005";

	public static final String ERROR_8006 = "8006";

	public static final String ERROR_8007 = "8007";

	public final static Map<String, String> ERROR_MSGS;

	static {

		Map<String, String> map = new HashMap<String, String>();
		map.put(ERROR_8001, "template not found");
		map.put(ERROR_8002, "images not found");
		map.put(ERROR_8003, "operate database error");
		map.put(ERROR_8004, "job not found");
		map.put(ERROR_8005, "parameter error");
		map.put(ERROR_8006, "upload file fail");
		map.put(ERROR_8007, "data not found");

		ERROR_MSGS = Collections.unmodifiableMap(map);

	}

	@SuppressWarnings({ "rawtypes" })
	public static String formatHttpServletRequest(
			javax.servlet.http.HttpServletRequest request) {
		StringBuffer buf = new StringBuffer();
		if (request != null) {
			Enumeration en = null;
			String k = null;

			buf.append(request.getRemoteAddr()).append(" ");
			buf.append(request.getMethod()).append(" ");
			buf.append(request.getRequestURL()).append(" ");

			for (en = request.getHeaderNames(); en.hasMoreElements();) {
				k = en.nextElement().toString();
				buf.append("h:").append(k).append("=")
						.append(request.getHeader(k)).append(" ");
			}

			for (en = request.getParameterNames(); en.hasMoreElements();) {
				k = en.nextElement().toString();
				buf.append("p:")
						.append(k)
						.append("=")
						.append(java.util.Arrays.toString(request
								.getParameterValues(k))).append(" ");
			}
		}
		return buf.toString();
	}

	public static SimpleDateFormat getXmlDateSdf() {
		SimpleDateFormat sdf = xmlDateSdf.get();
		if (sdf == null) {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			xmlDateSdf.set(sdf);
		}
		return sdf;
	}

	public static boolean isEmptyValue(String param) {

		return (param == null) || (param.trim().length() == 0)
				|| (param.equals(EMPTY_STRING));
	}

	public static long isNull(String v, long d) {
		try {
			return Long.parseLong(v);
		} catch (Exception e) {
			return d;
		}
	}

	public static String isNull(String v, String d) {
		return (v == null) || (v.length() == 0) ? d : v;
	}
}
