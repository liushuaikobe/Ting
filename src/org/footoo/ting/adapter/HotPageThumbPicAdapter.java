package org.footoo.ting.adapter;

import java.util.ArrayList;

import org.footoo.ting.R;
import org.footoo.ting.entity.Book;
import org.footoo.ting.util.AsyncImageLoader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class HotPageThumbPicAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Book> books;
	private AsyncImageLoader asyncImageLoader;
	private ViewGroup gridParent;

	public HotPageThumbPicAdapter(Context context, ViewGroup parent,
			ArrayList<Book> book) {
		this(context, parent);
		this.books = book;
	}

	public HotPageThumbPicAdapter(Context context, ViewGroup parent) {
		this(context);
		this.gridParent = parent;
	}

	public HotPageThumbPicAdapter(Context context) {
		this.mContext = context;
		asyncImageLoader = new AsyncImageLoader(90, 110);
	}

	public ArrayList<Book> getBook() {
		return books;
	}

	public void setBook(ArrayList<Book> book) {
		this.books = book;
	}

	public ViewGroup getGridParent() {
		return gridParent;
	}

	public void setGridParent(ViewGroup gridParent) {
		this.gridParent = gridParent;
	}

	public int getCount() {
		return books.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
		} else {
			imageView = (ImageView) convertView;
		}
		imageView.setTag(books.get(position).getImgUrl());
		Drawable cacheImg = asyncImageLoader.loadDrawable(books.get(position)
				.getImgUrl(), new AsyncImageLoader.ImageCallback() {

			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				ImageView ivByTag = (ImageView) gridParent
						.findViewWithTag(imageUrl);
				if (ivByTag != null && imageDrawable != null) {
					ivByTag.setImageDrawable(imageDrawable);
				} else {
					try {
						ivByTag.setImageResource(R.drawable.loading);
					} catch (Exception e) {

					}
				}
			}
		});
		imageView.setImageResource(R.drawable.loading);
		if (cacheImg != null) {
			imageView.setImageDrawable(cacheImg);
		}
		return imageView;
	}
}
