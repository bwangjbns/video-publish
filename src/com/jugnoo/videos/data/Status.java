package com.jugnoo.videos.data;

/**
 * ������Ƶ�����г��ֵļ���״̬
 * 
 * @author bwang
 * 
 */
public enum Status {

	WAIT("waiting"),

	PROCESS("processing"),

	FINISH("finished"),

	FAIL("failed"),

	LOST("lost");

	private String s;

	private Status(String s) {
		this.s = s;
	}

	public String getStatus() {
		return this.s;
	}

}
