package com.jugnoo.videos.initwork;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.w3c.dom.Node;

import com.jbns.util.LoadConfig;
import com.jbns.util.XmlUtil;
import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;
import com.jugnoo.videos.data.DatabaseInfo;

public class InitDatabase {

	private static DatabaseInfo dbInfo = null;

	private static Logger logger = LoggerManager.getLogger(InitDatabase.class);

	static {
		try {
			init();
		} catch (Exception e) {
			logger.info("init dbconfig failure!", e);
		}
	}

	private static void init() throws Exception {

		Map<String, Collection<URL>> map = LoadConfig.findConfigure("dbconfig");
		logger.info("dbconfig map : " + map);
		URL url = map.values().iterator().next().iterator().next();
		logger.info("dbconfig file url : " + url);
		Node root = XmlUtil.getXmlRootNode(url);

		String type = XmlUtil.getChildNodeValue(root, "type");
		String dbUrl = XmlUtil.getChildNodeValue(root, "url");
		String dbUser = XmlUtil.getChildNodeValue(root, "user");
		String dbPwd = XmlUtil.getChildNodeValue(root, "password");
		String poolSize = XmlUtil.getChildNodeValue(root, "poolsize");
		String retrytime = XmlUtil.getChildNodeValue(root, "retrytime");

		dbInfo = new DatabaseInfo(dbUrl, dbUser, dbPwd);

		dbInfo.setType(Integer.parseInt(type));
		dbInfo.setPoolsize(Integer.parseInt(poolSize));
		dbInfo.setRetrytime(Integer.parseInt(retrytime));

	}

	public static DatabaseInfo getDatabaseInfo() {
		return dbInfo;
	}
}
