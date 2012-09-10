package org.footoo.ting.adapter;

import org.footoo.ting.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class HotPageThumbPicAdapter extends BaseAdapter {

	private Context mContext;
	private int[] sourceCoverPicIds = { R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1 };

	// private int[] sourceCoverPicIds = { R.drawable.sample_cover_1,
	// R.drawable.sample_cover_2, R.drawable.sample_cover_3,
	// R.drawable.sample_cover_4, R.drawable.sample_cover_5,
	// R.drawable.sample_cover_6, R.drawable.sample_cover_7,
	// R.drawable.sample_cover_8, R.drawable.sample_cover_9 };

	public HotPageThumbPicAdapter(Context context) {
		this.mContext = context;
	}

	public int getCount() {
		return sourceCoverPicIds.length;
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
		imageView.setImageResource(sourceCoverPicIds[position]);
		return imageView;
	}
}
