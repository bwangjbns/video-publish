package com.jugnoo.videos.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;
import com.jugnoo.videos.data.Stats;
import com.jugnoo.videos.db.StatsDao;
import com.jugnoo.videos.util.oauth.v1.VimeoUtil;

@SuppressWarnings("serial")
public class VimeoQueryServlet extends HttpServlet {

	private String consumer_key = "1c168532b90b2fe18fec45640e78c62b";
	private String consumer_secret = "c5c3d0c8981e180";
	Logger logger = LoggerManager.getLogger(VimeoQueryServlet.class);
	StatsDao sdao = new StatsDao();

	String url = "http://vimeo.com/api/rest/v2";

	void doQuery(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/xml; charset=UTF-8");

		PrintWriter out = response.getWriter();

		Stats stats = new Stats();

		String access_token = request.getParameter("oauth_token");
		String token_secret = request.getParameter("oauth_secret");
		String consumer_key = request.getParameter("consumer_key");
		String consumer_secret = request.getParameter("consumer_secret");
		String video_id = request.getParameter("remoteid");

		stats.setConsumerKey(consumer_key == null? this.consumer_key : consumer_key);
		stats.setConsumerSecret(consumer_secret == null? this.consumer_secret : consumer_secret);
		stats.setAccessToken(access_token);
		stats.setTokenSecret(token_secret);
		stats.setRemoteId(video_id);
		stats.setChannel("vimeo");

		out.println("<?xml version='1.0' encoding='utf-8'?>");
		out.println("<JUIF>");

		try {
			VimeoUtil vu = new VimeoUtil(stats.getConsumerKey(), stats.getConsumerSecret(),
					stats.getAccessToken(), stats.getTokenSecret());
			Stats s = vu.getVideoStat(video_id);
			// System.out.println("video_id=" + video_id);
			stats.setCommentCount(s.getCommentCount());
			stats.setLikeCount(s.getLikeCount());
			stats.setDislikeCount(s.getDislikeCount());
			stats.setViewCount(s.getViewCount());
			String txt = "";
			txt += "<video>";
			txt += "<viewcounts>" + stats.getViewCount() + "</viewcounts>";
			txt += "<likecounts>" + stats.getLikeCount() + "</likecounts>";
			txt += "<dislikecounts>" + stats.getDislikeCount()
					+ "</dislikecounts>";
			txt += "<commentcounts>" + stats.getCommentCount()
					+ "</commentcounts>";
			txt += "</video>";
			out.println(txt);
			// System.out.println(txt);
			sdao.saveStats(stats);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("doQuery[" + stats.getRemoteId() + "]", e);
			}
			out.println("<failure><code>8000</code><description><![CDATA["
					+ e + "]]></description></failure>");
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
