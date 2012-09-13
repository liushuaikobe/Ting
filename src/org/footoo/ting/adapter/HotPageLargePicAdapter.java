package org.footoo.ting.adapter;

import java.util.ArrayList;

import org.footoo.ting.R;
import org.footoo.ting.entity.Book;
import org.footoo.ting.util.AsyncImageLoader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class HotPageLargePicAdapter extends BaseAdapter {

	private Context mContext;
	private ViewGroup lrgPicParent;

	private ArrayList<Book> books;
	private AsyncImageLoader asyncImageLoader;

	public HotPageLargePicAdapter(Context context) {
		this.mContext = context;
		this.asyncImageLoader = new AsyncImageLoader(320, 130);
	}

	public HotPageLargePicAdapter(Context context, ArrayList<Book> books) {
		this(context);
		this.books = books;
	}

	public HotPageLargePicAdapter(Context context, ArrayList<Book> books,
			ViewGroup parent) {
		this(context, books);
		this.lrgPicParent = parent;
	}

	public ViewGroup getParent() {
		return lrgPicParent;
	}

	public void setParent(ViewGroup parent) {
		this.lrgPicParent = parent;
	}

	public ArrayList<Book> getBooks() {
		return books;
	}

	public void setBooks(ArrayList<Book> books) {
		this.books = books;
	}

	public int getCount() {
		return Integer.MAX_VALUE;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		IvHolder ivHolder;
		if (convertView == null) {
			ivHolder = new IvHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_hot_large_pic, null);
			ivHolder.iv_largePic = (ImageView) convertView
					.findViewById(R.id.hot_large_pic);
			convertView.setTag(ivHolder);
		} else {
			ivHolder = (IvHolder) convertView.getTag();
		}
		ivHolder.iv_largePic.setTag(books.get(position % books.size())
				.getImgUrl());
		Drawable cacheImg = asyncImageLoader.loadDrawable(
				books.get(position % books.size()).getImgUrl(),
				new AsyncImageLoader.ImageCallback() {
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
						ImageView ivByTag = (ImageView) lrgPicParent
								.findViewWithTag(imageUrl);
						if (ivByTag != null && imageDrawable != null) {
							ivByTag.setImageDrawable(imageDrawable);
						} else {
							try {
								ivByTag.setImageResource(R.drawable.large_pic_loding);
							} catch (Exception e) {

							}
						}
					}
				});
		ivHolder.iv_largePic.setImageResource(R.drawable.large_pic_loding);
		if (cacheImg != null) {
			ivHolder.iv_largePic.setImageDrawable(cacheImg);
		}
		return convertView;
	}

	private class IvHolder {
		ImageView iv_largePic;
	}
}
