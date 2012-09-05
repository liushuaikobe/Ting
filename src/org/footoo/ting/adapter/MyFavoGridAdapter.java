package org.footoo.ting.adapter;

import org.footoo.ting.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MyFavoGridAdapter extends BaseAdapter {

	private Context mContext;
	private int[] myFavoSourceCover = { R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1 };

	public MyFavoGridAdapter(Context context) {
		this.mContext = context;
	}

	public int getCount() {
		return myFavoSourceCover.length;
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
			imageView = new ImageView(mContext);
		} else {
			imageView = (ImageView) convertView;
		}
		imageView.setImageResource(myFavoSourceCover[position]);
		return imageView;
	}
}
