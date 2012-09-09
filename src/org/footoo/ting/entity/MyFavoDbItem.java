package org.footoo.ting.entity;

public class MyFavoDbItem {
	private int _id;
	private int category_id;
	private int source_id;
	private String source_url;
	private String source_desc;
	private String cover_path;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public int getSource_id() {
		return source_id;
	}

	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}

	public String getSource_url() {
		return source_url;
	}

	public void setSource_url(String source_url) {
		this.source_url = source_url;
	}

	public String getSource_desc() {
		return source_desc;
	}

	public void setSource_desc(String source_desc) {
		this.source_desc = source_desc;
	}

	public String getCover_path() {
		return cover_path;
	}

	public void setCover_path(String cover_path) {
		this.cover_path = cover_path;
	}
}
