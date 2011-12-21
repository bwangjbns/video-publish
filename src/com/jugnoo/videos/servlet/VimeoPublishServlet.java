package com.jugnoo.videos.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;
import com.jugnoo.videos.data.Status;
import com.jugnoo.videos.data.Task;
import com.jugnoo.videos.db.TaskDao;
import com.jugnoo.videos.util.Utility;
import com.jugnoo.videos.util.oauth.v1.VimeoUtil;

@SuppressWarnings("serial")
public class VimeoPublishServlet extends HttpServlet {

	String consumer_key = "";
	String consumer_secret = "";
	TaskDao dao = new TaskDao();

	String downloadUrl = "";

	Logger logger = LoggerManager.getLogger(VimeoPublishServlet.class);

	String url = "http://vimeo.com/api/rest/v2";

	void doPublish(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		long dbg = System.currentTimeMillis();
		String socialid = request.getParameter("user_social_account_id");
		String authsub = request.getParameter("authsub");
		String access_token = request.getParameter("oauth_token");
		String token_secret = request.getParameter("token_secret");
		String consumer_key = request.getParameter("consumer_key");
		String consumer_secret = request.getParameter("consumer_secret");
		String user = request.getParameter("siteuser");
		String pwd = request.getParameter("sitepwd");
		String token = request.getParameter("token");
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		String category = request.getParameter("category");
		String keywords = request.getParameter("keywords");
		String tags = request.getParameter("tags");
		String callback = request.getParameter("callback");

		response.setContentType("text/xml; charset=UTF-8");

		if (logger.isDebugEnabled()) {
			logger.debug(dbg + " prepare publishing "
					+ Utility.formatHttpServletRequest(request));
		}

		PrintWriter out = response.getWriter();

		Task task = new Task();

		task.setChannel("vimeo");
		task.setType("video");
		task.setStatus(Status.PROCESS);
		task.setRemoteId(Utility.EMPTY_STRING);
		task.setSpaceId(Utility.EMPTY_STRING);

		task.setPlayUrl(Utility.EMPTY_STRING);
		task.setSocialId(Utility.isNull(socialid, 0));
		task.setToken(token);
		task.setUser(user);
		task.setPassword(pwd);

		task.setTitle(Utility.isNull(title, "untitled"));
		task.setDescription(Utility.isNull(description, Utility.EMPTY_STRING));
		task.setCategory(Utility.isNull(category, "Film"));
		task.setKeywords(Utility.isNull(keywords, "jugnoo"));
		task.setTags(Utility.isNull(tags, "tags"));

		task.setAuthSub(authsub);
		task.setConsumerKey(consumer_key == null? this.consumer_key : consumer_key);
		task.setConsumerSecret(consumer_secret == null? this.consumer_secret : consumer_secret);
		task.setAccessToken(access_token);
		task.setTokenSecret(token_secret);
		task.setCallback(callback);

		out.println("<?xml version='1.0' encoding='utf-8'?>");
		out.println("<JUIF>");

		String video_Id;
		long id = -1;
		try {
			id = dao.addTask(task);
			if (logger.isDebugEnabled()) {
				logger.debug(dbg + " start publishing "
						+ Utility.formatHttpServletRequest(request));
			}
			VimeoUtil vu = new VimeoUtil(consumer_key, consumer_secret,
					access_token, token_secret);
			video_Id = vu.upload(title, description, new URL(downloadUrl
					+ "?token=" + task.getToken()).openStream());

			if (logger.isDebugEnabled()) {
				logger.debug(dbg + " finish publishing & start response "
						+ Utility.formatHttpServletRequest(request));
			}
			out.println(formatEntry(String.valueOf(id), video_Id));
			if (logger.isDebugEnabled()) {
				logger.debug(dbg + " finish response "
						+ Utility.formatHttpServletRequest(request));
			}
			task.setPlayUrl("http://vimeo.com/" + video_Id);
			task.setRemoteId(video_Id);
			task.setStatus(Status.FINISH);
			dao.updateTask(id, task);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("doPublish[taskid=" + id + ", flag=" + dbg + "]",
						e);
			}
			try {
				task.setStatus(Status.FAIL);
				dao.updateTask(id, task);
			} catch (SQLException ex) {
				if (logger.isErrorEnabled()) {
					logger.error("doPublish[taskid=" + id + ", flag=" + dbg
							+ "]", ex);
				}
			}
			out.println("<failure><code>8000</code><description><![CDATA[" + e
					+ "]]></description></failure>");
		}
		out.println("</JUIF>");

	}

	String formatEntry(String token, String video_Id) {
		StringBuffer buff = new StringBuffer();

		buff.append("<success>");
		buff.append("<publish-id>").append(token).append("</publish-id>");
		buff.append("<remote-id>").append(video_Id).append("</remote-id>");
		buff.append("<remote-url><![CDATA[").append("http://vimeo.com/")
				.append(video_Id).append("]]></remote-url>");
		buff.append("</success>");

		return buff.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPublish(req, resp);
		// super.doGet(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPublish(req, resp);
		// super.doPost(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		downloadUrl = config.getInitParameter("Download-URL");
		if (downloadUrl == null) {
			downloadUrl = config.getServletContext().getInitParameter(
					"Download-URL");
		}
		consumer_key = config.getInitParameter("Vimeo_Consumer_Key");
		if (consumer_key == null) {
			consumer_key = config.getServletContext().getInitParameter(
					"Vimeo_Consumer_Key");
		}
		consumer_secret = config.getInitParameter("Vimeo_Consumer_Secret");
		if (consumer_secret == null) {
			consumer_secret = config.getServletContext().getInitParameter(
					"Vimeo_Consumer_Secret");
		}
	}

}
