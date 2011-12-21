package com.jugnoo.videos.data;

public class VideoAccess {

	private String remoteid;
	private String accessToken;
	private String tokenSecret;

	public VideoAccess(String remote_id, String user_token, String secret) {
		setRemoteid(remote_id);
		setAccessToken(user_token);
		setTokenSecret(secret);
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRemoteid() {
		return remoteid;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setRemoteid(String remoteid) {
		this.remoteid = remoteid;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[remoteid:" + remoteid
				+ ", accessToken:" + accessToken + ", tokenSecret:"
				+ tokenSecret + "]";
	}

}
