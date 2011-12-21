/**
 * 2011-10-17 ÏÂÎç1:38:18
 */
package com.jugnoo.videos.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.youtube.VideoEntry;
import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;
import com.jugnoo.videos.data.Stats;
import com.jugnoo.videos.db.StatsDao;
import com.jugnoo.videos.db.TaskDao;

/**
 * @author Fang
 * 
 *         Query information of remote video file in youtube
 * 
 */
@SuppressWarnings("serial")
public class YoutubeQueryServlet extends HttpServlet {

	String clientId = "gdataSample-YouTubeAuth-1";
	String consumer_key = "";
	String consumer_secret = "";

	String devKey = "AI39si4-CN0DOXTNFlsvYmmyEF4c7agys-_zgDWff7nghS-qeRr2LOcvIppwtlcKHnAqOYaFaFBYSPpcoGBYCcDoDaoPq2Q70g";
	String feedUrl = "http://gdata.youtube.com/feeds/api/videos/";

	Logger logger = LoggerManager.getLogger(YoutubeQueryServlet.class);
	StatsDao sdao = new StatsDao();
	TaskDao tdao = new TaskDao();

	void doQuery(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/xml; charset=UTF-8");

		PrintWriter out = response.getWriter();

		Stats stats = new Stats();
		String remoteid = request.getParameter("remoteid");
		String user = request.getParameter("siteuser");
		String pwd = request.getParameter("sitepwd");
		String authsub = request.getParameter("authsub");
		String access_token = request.getParameter("oauth_token");
		String token_secret = request.getParameter("oauth_secret");
		String consumer_key = request.getParameter("consumer_key");
		String consumer_secret = request.getParameter("consumer_secret");

		String txt = "";

		stats.setRemoteId(remoteid);
		stats.setAuthSub(authsub);
		stats.setConsumerKey(consumer_key == null? this.consumer_key : consumer_key);
		stats.setConsumerSecret(consumer_secret == null? this.consumer_secret : consumer_secret);
		stats.setAccessToken(access_token);
		stats.setTokenSecret(token_secret);
		stats.setUser(user);
		stats.setPassword(pwd);
		stats.setChannel("youtube");

		out.println("<?xml version='1.0' encoding='utf-8'?>");
		out.println("<JUIF>");
		try {
			if (stats.getRemoteId() == null) {
				throw new IllegalArgumentException("remoteid not found");
			}

			YouTubeService service = new YouTubeService(clientId, devKey);
			if (stats.getAccessToken() != null) {
				GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
				oauthParameters.setOAuthConsumerKey(stats.getConsumerKey());
				oauthParameters.setOAuthConsumerSecret(stats.getConsumerSecret());
				oauthParameters.setOAuthToken(stats.getAccessToken());
				oauthParameters.setOAuthTokenSecret(stats.getTokenSecret());
				service.setOAuthCredentials(oauthParameters,
						new OAuthHmacSha1Signer());
			} else if (stats.getAuthSub() != null) {
				service.setAuthSubToken(stats.getAuthSub(), null);
			} else {
				service.setUserCredentials(stats.getUser(), stats.getPassword());
			}
			VideoEntry ve = service.getEntry(
					new URL(feedUrl + stats.getRemoteId()), VideoEntry.class);
			if (ve == null) {
				throw new IllegalArgumentException();
			}
			if (ve.getStatistics() != null) {
				stats.setViewCount(ve.getStatistics().getViewCount());
			}
			if (ve.getYtRating() != null) {
				stats.setLikeCount(ve.getYtRating().getNumLikes());
				stats.setDislikeCount(ve.getYtRating().getNumDislikes());
			}
			stats.setCommentCount(ve.getComments().getFeedLink().getCountHint());
			txt += "<entry>";
			txt += "<viewcount>" + stats.getViewCount() + "</viewcount>";
			txt += "<likecount>" + stats.getLikeCount() + "</likecount>";
			txt += "<dislikecount>" + stats.getDislikeCount()
					+ "</dislikecount>";
			txt += "<commentcount>" + stats.getCommentCount()
					+ "</commentcount>";
			txt += "</entry>";
			out.println(txt);
			sdao.saveStats(stats);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("doQuery[" + stats.getRemoteId() + "]", e);
			}
			out.println("<failure><code>8000</code><description><![CDATA[" + e
					+ "]]></description></failure>");
		}
		out.println("</JUIF>");

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
		doQuery(req, resp);
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
		doQuery(req, resp);
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
