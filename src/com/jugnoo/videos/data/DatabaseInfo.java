package com.jugnoo.videos.data;

public class DatabaseInfo {

	private int type;
	private String url;
	private String user;
	private String password;
	private int poolsize;
	private int retrytime;

	public DatabaseInfo(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public int getPoolsize() {
		return poolsize;
	}

	public int getRetrytime() {
		return retrytime;
	}

	public int getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public void setPoolsize(int poolsize) {
		this.poolsize = poolsize;
	}

	public void setRetrytime(int retrytime) {
		this.retrytime = retrytime;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "database type: " + this.type + ", url : " + this.url
				+ ", user : " + this.user + ", password : " + this.password
				+ ", poolsize : " + this.poolsize + ", retrytime : "
				+ this.retrytime;
	}

}
