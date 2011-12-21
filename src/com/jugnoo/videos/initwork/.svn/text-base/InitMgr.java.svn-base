package com.jugnoo.videos.initwork;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;

/**
 * init template
 * 
 * @author bwang
 * 
 */
public class InitMgr implements ServletContextListener {

	private static boolean isInited = false;
	private static Logger logger;
	private Object hisTimer = null;

	private void init(ServletContext ctx) {
		if (System.getProperty("jbns.conf.base.path") == null) {
			System.setProperty("jbns.conf.base.path", ctx.getRealPath("/")
					+ "META-INF/conf");
		}
		if (System.getProperty("jbns.logs.base.path") == null) {
			LoggerManager.initialize("../logs");
		} else {
			LoggerManager.initialize(System.getProperty("jbns.logs.base.path"));
		}
		LoggerManager.initApacheCommLog();
		logger = LoggerManager.getLogger(InitMgr.class);
		try {
			Class.forName("com.jugnoo.videos.initwork.InitDatabase");
		} catch (ClassNotFoundException e) {
			logger.info("int database or templates error.", e);
		}

		try {
			Class<?> c = Class.forName("com.jugnoo.videos.job.HistoryTimer");
			hisTimer = c.getConstructor().newInstance();
			Method m = c.getMethod("addJob", String.class, String.class,
					String.class, String.class);
			String[] jobs = ctx.getInitParameter("Scheduler-Jobs").split(
					"[^a-z0-9-A-Z_]+");
			for (String job : jobs) {
				m.invoke(hisTimer,
						"com.jugnoo.videos.job." + job + "JobDetail",
						ctx.getInitParameter(job + "_Consumer_Key"),
						ctx.getInitParameter(job + "_Consumer_Secret"),
						ctx.getInitParameter(job + "_Scheduler"));
			}
		} catch (Exception e) {
			logger.info("int HistoryTimer error.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		try {
			if (hisTimer != null) {
				hisTimer.getClass().getMethod("destory").invoke(hisTimer);
				hisTimer = null;
			}
		} catch (Exception e) {
			logger.info("destory HistoryTimer error.", e);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		if (!isInited) {
			try {
				ServletContext ctx = evt.getServletContext();
				init(ctx);
				isInited = true;
			} catch (Exception e) {
				logger.info("InitMgr init error.", e);
			}
		}
	}

}
