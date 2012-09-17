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
import android.widget.ListView;
import android.widget.TextView;

public class SourcesListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Book> books;
	private AsyncImageLoader asyncImageLoader;
	private ListView itemParent;

	public SourcesListAdapter(Context context) {
		this.mContext = context;
		asyncImageLoader = new AsyncImageLoader(90, 110);
	}

	public SourcesListAdapter(Context context, ArrayList<Book> books) {
		this(context);
		this.books = books;
	}

	public SourcesListAdapter(Context context, ArrayList<Book> books,
			ListView parent) {
		this(context, books);
		this.itemParent = parent;
	}

	public ArrayList<Book> getBooks() {
		return books;
	}

	public void setBooks(ArrayList<Book> books) {
		this.books = books;
	}

	public ListView getParent() {
		return itemParent;
	}

	public void setParent(ListView parent) {
		this.itemParent = parent;
	}

	public int getCount() {
		return books.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		SourceItemHolder holder = null;
		if (convertView == null) {
			holder = new SourceItemHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_sources_listitem, null);
			holder.cover_iv = (ImageView) convertView
					.findViewById(R.id.source_cover);
			holder.name_tv = (TextView) convertView
					.findViewById(R.id.source_name);
			holder.desc_tv = (TextView) convertView
					.findViewById(R.id.source_desc);
			convertView.setTag(holder);
		} else {
			holder = (SourceItemHolder) convertView.getTag();
		}
		holder.name_tv.setText(books.get(position).getSourceName());
		holder.desc_tv.setText(books.get(position).getSource_describe());
		holder.cover_iv.setTag(books.get(position).getImgUrl());
		Drawable cacheImg = asyncImageLoader.loadDrawable(books.get(position)
				.getImgUrl(), new AsyncImageLoader.ImageCallback() {

			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				ImageView ivByTag = (ImageView) itemParent
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
		holder.cover_iv.setImageResource(R.drawable.loading);
		if (cacheImg != null) {
			holder.cover_iv.setImageDrawable(cacheImg);
		}
		return convertView;
	}

	private class SourceItemHolder {
		ImageView cover_iv;
		TextView name_tv;
		TextView desc_tv;
	}

}
