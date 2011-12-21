/**
 * 2011-9-29 ÉÏÎç9:09:32
 */
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

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.geo.impl.GeoRssWhere;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.data.media.MediaStreamSource;
import com.google.gdata.data.media.mediarss.MediaCategory;
import com.google.gdata.data.media.mediarss.MediaDescription;
import com.google.gdata.data.media.mediarss.MediaKeywords;
import com.google.gdata.data.media.mediarss.MediaTitle;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.data.youtube.YouTubeNamespace;
import com.google.gdata.data.youtube.YtPublicationState;
import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;
import com.jugnoo.videos.data.Status;
import com.jugnoo.videos.data.Task;
import com.jugnoo.videos.db.TaskDao;
import com.jugnoo.videos.util.Utility;

/**
 * @author Fang
 * 
 *         publish files to youtube
 * 
 */
@SuppressWarnings("serial")
public class YoutubePublishServlet extends HttpServlet {

	String clientId = "gdataSample-YouTubeAuth-1";
	String consumer_key = "";

	String consumer_secret = "";
	TaskDao dao = new TaskDao();
	String devKey = "AI39si4-CN0DOXTNFlsvYmmyEF4c7agys-_zgDWff7nghS-qeRr2LOcvIppwtlcKHnAqOYaFaFBYSPpcoGBYCcDoDaoPq2Q70g";
	String downloadUrl = "";
	Logger logger = LoggerManager.getLogger(YoutubePublishServlet.class);
	String uploadUrl = "http://uploads.gdata.youtube.com/feeds/api/users/default/uploads";

	void doPublish(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		long dbg = System.currentTimeMillis();
		String socialid = request.getParameter("user_social_account_id");
		String authsub = request.getParameter("authsub");
		String access_token = request.getParameter("oauth_token");
		String token_secret = request.getParameter("oauth_secret");
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

		task.setChannel("youtube");
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
		long id = -1;
		try {
			id = dao.addTask(task);
			if (logger.isDebugEnabled()) {
				logger.debug(dbg + " start publishing "
						+ Utility.formatHttpServletRequest(request));
			}
			VideoEntry entry = uploadToYoutube(task);
			if (logger.isDebugEnabled()) {
				logger.debug(dbg + " finish publishing & start response "
						+ Utility.formatHttpServletRequest(request));
			}
			out.println(formatEntry(String.valueOf(id), entry));
			if (logger.isDebugEnabled()) {
				logger.debug(dbg + " finish response "
						+ Utility.formatHttpServletRequest(request));
			}
			task.setPlayUrl(entry.getMediaGroup().getPlayer().getUrl());
			task.setRemoteId(entry.getMediaGroup().getVideoId());
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

	String formatEntry(String token, VideoEntry entry) {
		StringBuffer buff = new StringBuffer();

		buff.append("<success>");
		buff.append("<publish-id>").append(token).append("</publish-id>");
		buff.append("<remote-id>").append(entry.getMediaGroup().getVideoId())
				.append("</remote-id>");
		if (entry.getMediaGroup().getPlayer() != null) {
			buff.append("<remote-url><![CDATA[")
					.append(Utility.isNull(entry.getMediaGroup().getPlayer()
							.getUrl(), "")).append("]]></remote-url>");
		}
		buff.append("</success>");

		return buff.toString();
	}

	VideoEntry uploadToYoutube(Task task) throws Exception {

		YouTubeService service = new YouTubeService(clientId, devKey);
		if (task.getAccessToken() != null) {
			GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
			oauthParameters.setOAuthConsumerKey(task.getConsumerKey());
			oauthParameters.setOAuthConsumerSecret(task.getConsumerSecret());
			oauthParameters.setOAuthToken(task.getAccessToken());
			oauthParameters.setOAuthTokenSecret(task.getTokenSecret());
			service.setOAuthCredentials(oauthParameters,
					new OAuthHmacSha1Signer());
		} else if (task.getAuthSub() != null) {
			service.setAuthSubToken(task.getAuthSub(), null);
		} else {
			service.setUserCredentials(task.getUser(), task.getPassword());
		}

		VideoEntry newEntry = new VideoEntry();

		YouTubeMediaGroup mg = newEntry.getOrCreateMediaGroup();
		mg.setTitle(new MediaTitle());
		mg.getTitle().setPlainTextContent(task.getTitle());
		mg.addCategory(new MediaCategory(YouTubeNamespace.CATEGORY_SCHEME, task
				.getCategory()));
		mg.setKeywords(new MediaKeywords());
		mg.getKeywords().addKeyword(task.getKeywords());
		mg.setDescription(new MediaDescription());
		mg.getDescription().setPlainTextContent(task.getDescription());
		mg.setPrivate(false);
		newEntry.setGeoCoordinates(new GeoRssWhere(37.0, -122.0));

		MediaSource ms = new MediaStreamSource(new URL(downloadUrl + "?token="
				+ task.getToken()).openStream(), "video/video");
		newEntry.setMediaSource(ms);

		service.setHeader("Slug", task.getToken());
		VideoEntry createdEntry = service.insert(new URL(uploadUrl), newEntry);

		if (createdEntry.isDraft()
				&& (createdEntry.getPublicationState().getState() != YtPublicationState.State.PROCESSING)) {
			throw new Exception("status is "
					+ createdEntry.getPublicationState().getState());
		}

		return createdEntry;
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
		consumer_key = config.getInitParameter("Youtube_Consumer_Key");
		if (consumer_key == null) {
			consumer_key = config.getServletContext().getInitParameter(
					"Youtube_Consumer_Key");
		}
		consumer_secret = config.getInitParameter("Youtube_Consumer_Secret");
		if (consumer_secret == null) {
			consumer_secret = config.getServletContext().getInitParameter(
					"Youtube_Consumer_Secret");
		}
	}

}
