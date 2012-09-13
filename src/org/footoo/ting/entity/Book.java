package org.footoo.ting.entity;

public class Book {
	private int categoryId;
	private int sourceId;
	private String sourceName;
	private String imgUrl;
	private String source_describe;
	private String last_modify;
	private String chapterJonUrl;

	public Book(int categoryId, int sourceId, String sourceName, String imgUrl,
			String source_describe, String last_modify, String chapterJonUrl) {
		super();
		this.categoryId = categoryId;
		this.sourceId = sourceId;
		this.sourceName = sourceName;
		this.imgUrl = imgUrl;
		this.source_describe = source_describe;
		this.last_modify = last_modify;
		this.chapterJonUrl = chapterJonUrl;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getSource_describe() {
		return source_describe;
	}

	public void setSource_describe(String source_describe) {
		this.source_describe = source_describe;
	}

	public String getLast_modify() {
		return last_modify;
	}

	public void setLast_modify(String last_modify) {
		this.last_modify = last_modify;
	}

	public String getChapterJonUrl() {
		return chapterJonUrl;
	}

	public void setChapterJonUrl(String chapterJonUrl) {
		this.chapterJonUrl = chapterJonUrl;
	}

}
