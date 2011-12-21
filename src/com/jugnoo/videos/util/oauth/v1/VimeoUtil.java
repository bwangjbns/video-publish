package com.jugnoo.videos.util.oauth.v1;

import java.io.InputStream;
import java.util.TreeMap;

import com.jbns.util.XmlUtil;
import com.jugnoo.videos.data.Stats;

public class VimeoUtil {

	OAuth auth = null;

	String url = "http://vimeo.com/api/rest/v2";

	public VimeoUtil(String oauth_consumer_key, String oauth_consumer_secret,
			String oauth_token, String oauth_token_secret) {
		auth = new OAuth(oauth_consumer_key, oauth_consumer_secret,
				oauth_token, oauth_token_secret);
	}

	private String getTextFromXml(String xml, String node, String def)
			throws Exception {
		try {
			return XmlUtil.searchChildNode(XmlUtil.getXmlRootNode(xml), node)
					.getTextContent();
		} catch (Exception e) {
			return def;
		}
	}

	private String getValueFromXml(String xml, String node, String attr) {
		return XmlUtil.getNodeAttr(
				XmlUtil.searchChildNode(XmlUtil.getXmlRootNode(xml), node),
				attr, null);
	}

	long formatLong(String v, long d) {
		try {
			return Long.parseLong(v);
		} catch (Exception e) {
			return d;
		}
	}

	public Stats getVideoStat(String video_id) throws Exception {
		TreeMap<String, String> http_params = new TreeMap<String, String>();
		http_params.put("method", "vimeo.videos.getInfo");
		http_params.put("video_id", video_id);
		String xml = auth.call(url, "GET", http_params, true, null);
		String code = getValueFromXml(xml, "err", "code");
		if ("1".equals(code)) {
			throw new com.jugnoo.videos.ex.ResourceNotFoundException("Video ["
					+ video_id + "] not found :  " + xml);
		} else if (code != null) {
			throw new Exception("vimeo.videos.getInfo " + xml);
		}
		String likeCounts = getTextFromXml(xml, "number_of_likes", null);
		String viewCounts = getTextFromXml(xml, "number_of_plays", null);
		String commentCounts = getTextFromXml(xml, "number_of_comments", null);
		// System.out.println();
		Stats s = new Stats();
		s.setLikeCount(formatLong(likeCounts, 0));
		s.setDislikeCount(0);
		s.setCommentCount(formatLong(commentCounts, 0));
		s.setViewCount(formatLong(viewCounts, 0));
		return s;
	}

	public String upload(String title, String descr, InputStream istream)
			throws Exception {
		TreeMap<String, String> http_params = new TreeMap<String, String>();
		http_params.put("method", "vimeo.videos.upload.getTicket");
		http_params.put("upload_method", "streaming");
		String xml = auth.call(url, "GET", http_params, true, null);
		// System.out.println("xml.1=" + xml);
		String ticket_id = getValueFromXml(xml, "ticket", "id");
		// System.out.println("ticket_id=" + ticket_id);
		String endpoint = getValueFromXml(xml, "ticket", "endpoint");
		if ((ticket_id == null) || (endpoint == null)) {
			throw new Exception("vimeo.videos.upload.getTicket " + xml);
		}
		http_params.clear();
		xml = auth.call(endpoint, "PUT", http_params, true, istream);
		// System.out.println("xml.2=" + xml);
		http_params.clear();
		http_params.put("method", "vimeo.videos.upload.complete");
		http_params.put("filename", title);
		http_params.put("ticket_id", ticket_id);
		xml = auth.call(url, "GET", http_params, true, null);
		// System.out.println("xml.3=" + xml);
		String video_id = getValueFromXml(xml, "ticket", "video_id");
		if (video_id == null) {
			throw new Exception("vimeo.videos.upload.complete " + xml);
		}
		// System.out.println("video_id" + video_id);
		if (title != null) {
			http_params.clear();
			http_params.put("method", "vimeo.videos.setTitle");
			http_params.put("title", title);
			http_params.put("video_id", video_id);
			auth.call(url, "GET", http_params, true, null);
			// System.out.println("title= " + title + "\nxml.4=" + xml);
		}
		if (descr != null) {
			http_params.clear();
			http_params.put("method", "vimeo.videos.setDescription");
			http_params.put("description", descr);
			http_params.put("video_id", video_id);
			auth.call(url, "GET", http_params, true, null);
			// System.out.println("description= " + descr + "\nxml.5=" + xml);
		}
		return video_id;
	}
}
