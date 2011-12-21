package com.jugnoo.videos.util.oauth.v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;

public class OAuth {

	private String oauth_consumer_key;

	private String oauth_consumer_secret;

	private String oauth_token;

	private String oauth_token_secret;

	public OAuth(String oauth_consumer_key,
			String oauth_consumer_secret, String oauth_token,
			String oauth_token_secret) {
		this.oauth_consumer_key = oauth_consumer_key;
		this.oauth_consumer_secret = oauth_consumer_secret;
		this.oauth_token = oauth_token;
		this.oauth_token_secret = oauth_token_secret;
	}

	private String base64_encode(byte[] b) {
		String base64_encode = new BASE64Encoder().encode(b);
		// System.out.println("base64_encode=" + base64_encode);
		return base64_encode;
	}

	private String createBaseString(String http_method, String url,
			TreeMap<String, String> oauth_params,
			TreeMap<String, String> http_params) {
		String base_string = null;
		TreeMap<String, String> all_params = new TreeMap<String, String>();
		all_params.putAll(oauth_params);
		all_params.putAll(http_params);
		for (Map.Entry<String, String> entry : all_params.entrySet()) {
			if (base_string == null) {
				base_string = urlencode(entry.getKey()) + "="
						+ urlencode(entry.getValue());
			} else {
				base_string += "&" + urlencode(entry.getKey()) + "="
						+ urlencode(entry.getValue());
			}
		}
		base_string = http_method + "&" + urlencode(url) + "&"
				+ urlencode(base_string);
		// System.out.println("base_string=" + base_string);
		return base_string;
	}

	private String createHttpUrl(String url, TreeMap<String, String> http_params) {
		String get_url = null;
		if ((http_params == null) || http_params.isEmpty()) {
			return url;
		}
		for (Map.Entry<String, String> entry : http_params.entrySet()) {
			if (get_url == null) {
				get_url = urlencode(entry.getKey()) + "="
						+ urlencode(entry.getValue());
			} else {
				get_url += "&" + urlencode(entry.getKey()) + "="
						+ urlencode(entry.getValue());
			}
		}
		get_url = url + "?" + get_url;
		// System.out.println("get_url=" + get_url);
		return get_url;

	}

	private String createOAuthString(TreeMap<String, String> oauth_params,
			String oauth_signature) {
		String oauth_string = null;
		for (Map.Entry<String, String> entry : oauth_params.entrySet()) {
			if (oauth_string == null) {
				oauth_string = urlencode(entry.getKey()) + "=\""
						+ urlencode(entry.getValue()) + "\"";
			} else {
				oauth_string += ", " + urlencode(entry.getKey()) + "=\""
						+ urlencode(entry.getValue()) + "\"";
			}
		}
		oauth_string = "OAuth realm=\"\", " + oauth_string
				+ ", oauth_signature=\"" + urlencode(oauth_signature) + "\"";
		// System.out.println("oauth_string=" + oauth_string);
		return oauth_string;
	}

	private byte[] hash_hmac(String base_string, String hmac_key) {
		byte[] byteHMAC = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec spec = new SecretKeySpec(hmac_key.getBytes(),
					"HmacSHA1");
			mac.init(spec);
			byteHMAC = mac.doFinal(base_string.getBytes());
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException ignore) {
		}
		return byteHMAC;
	}

	private String urlencode(String v) {
		try {
			return URLEncoder.encode(v, "utf-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	String getUrlContent(String url, String http_method, String oauth_string,
			InputStream istream) throws MalformedURLException, IOException {
		int size = 32768;
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		conn.setRequestMethod(http_method);
		if (oauth_string != null) {
			conn.setRequestProperty("Authorization", oauth_string);
		}
		if (istream != null) {
			conn.setDoOutput(true);
			conn.setChunkedStreamingMode(size);
			int bytesRead = 0;
			byte b[] = new byte[size];
			OutputStream ostream = conn.getOutputStream();
			while ((bytesRead = istream.read(b, 0, bytesRead)) > 0) {
				ostream.write(b, 0, bytesRead);
			}
			ostream.flush();
			ostream.close();
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "UTF-8"));
		String line;
		StringBuffer buf = new StringBuffer();
		while ((line = in.readLine()) != null) {
			buf.append(line).append("\n");
		}
		in.close();
		conn.disconnect();
		return buf.toString();
	}

	public String call(String url, String http_method,
			TreeMap<String, String> http_params, boolean need_oauth,
			InputStream istream) throws MalformedURLException, IOException {
		String oauth_string = null;
		if (need_oauth) {
			TreeMap<String, String> oauth_params = new TreeMap<String, String>();
			oauth_params.put("oauth_consumer_key", oauth_consumer_key);
			oauth_params.put("oauth_token", oauth_token);

			oauth_params.put("oauth_signature_method", "HMAC-SHA1");
			oauth_params.put("oauth_timestamp",
					String.valueOf(System.currentTimeMillis() / 1000));
			oauth_params.put("oauth_nonce",
					String.valueOf(System.currentTimeMillis()));
			oauth_params.put("oauth_version", "1.0");

			String base_string = createBaseString(http_method, url,
					oauth_params, http_params);
			String hmac_key = urlencode(oauth_consumer_secret) + "&"
					+ urlencode(oauth_token_secret);
			String oauth_signature = base64_encode(hash_hmac(base_string,
					hmac_key));
			oauth_string = createOAuthString(oauth_params, oauth_signature);
		}
		String urlcontent = getUrlContent(createHttpUrl(url, http_params),
				http_method, oauth_string, istream);
		return urlcontent;
	}

}
