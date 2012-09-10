package org.footoo.ting.adapter;

import java.util.ArrayList;

import org.footoo.ting.media.BookInstance;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class HotPageThumbPicAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<BookInstance> book;

	public HotPageThumbPicAdapter(Context context, ArrayList<BookInstance> book) {
		this.mContext = context;
		this.book = book;
	}

	public int getCount() {
		return book.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			// convertView = LayoutInflater.from(mContext).inflate(
			// R.layout.layout_menu_grid_item, null);
			// imageView = (ImageView) convertView.findViewById(R.id.menu_item);

			// imageView = (ImageView) (LayoutInflater.from(mContext).inflate(
			// R.layout.layout_menu_grid_item, null));

			imageView = new ImageView(mContext);
			// imageView.setLayoutParams(new GridView.LayoutParams(180, 230));
			// imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			// imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}
		imageView.setImageBitmap(book.get(position).coverBitmap);
		return imageView;
	}
}
