package org.footoo.ting.entity;

public class Category {
	private int category_id;
	private String categoryName;
	private String categoryUrl;

	public Category(int category_id, String categoryName, String categoryUrl) {
		super();
		this.category_id = category_id;
		this.categoryName = categoryName;
		this.categoryUrl = categoryUrl;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryUrl() {
		return categoryUrl;
	}

	public void setCategoryUrl(String categoryUrl) {
		this.categoryUrl = categoryUrl;
	}
}
