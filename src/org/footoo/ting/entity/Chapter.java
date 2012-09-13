package org.footoo.ting.entity;

public class Chapter {
	private int chapterId;
	private String chapterName;
	private String chapterUrl;
	public Chapter(int chapterId, String chapterName, String chapterUrl) {
		super();
		this.chapterId = chapterId;
		this.chapterName = chapterName;
		this.chapterUrl = chapterUrl;
	}
	public int getChapterId() {
		return chapterId;
	}
	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}
	public String getChapterName() {
		return chapterName;
	}
	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}
	public String getChapterUrl() {
		return chapterUrl;
	}
	public void setChapterUrl(String chapterUrl) {
		this.chapterUrl = chapterUrl;
	}
	
}
