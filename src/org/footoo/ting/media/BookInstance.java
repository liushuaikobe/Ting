package org.footoo.ting.media;

import android.graphics.Bitmap;

public class BookInstance {
	
	public BookInstance(int categoryId, int sourceId, String sourceName){
		this.categoryId = categoryId;
		this.sourceId = sourceId;
		this.sourceName = sourceName;
	}
	
	public int categoryId;
	public int sourceId;
	public String sourceName;
	
	public Bitmap coverBitmap;
	public String source_describe;
	public String last_modify;
	public String chapterJonUrl;
}
