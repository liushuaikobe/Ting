package org.footoo.ting.entity;

public class MyFavoDbItem {
	private int _id;
	private String chapter_url;
	private String source_name;
	private String chapter_name;
	private String last_time;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getChapter_url() {
		return chapter_url;
	}

	public void setChapter_url(String chapter_url) {
		this.chapter_url = chapter_url;
	}

	public String getSource_name() {
		return source_name;
	}

	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}

	public String getChapter_name() {
		return chapter_name;
	}

	public void setChapter_name(String chapter_name) {
		this.chapter_name = chapter_name;
	}

	public String getLast_time() {
		return last_time;
	}

	public void setLast_time(String last_time) {
		this.last_time = last_time;
	}
}
