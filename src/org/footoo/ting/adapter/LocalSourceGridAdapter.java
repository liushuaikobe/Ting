package org.footoo.ting.adapter;

import org.footoo.ting.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class LocalSourceGridAdapter extends BaseAdapter {

	private Context mContext;

	private int[] localSourceCover = { R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1,
			R.drawable.sample_cover_1, R.drawable.sample_cover_1 };

	public LocalSourceGridAdapter(Context context) {
		mContext = context;
	}

	public int getCount() {
		return localSourceCover.length;
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LocalSourceGridHolder holder;
		if (convertView == null) {
			holder = new LocalSourceGridHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.local_source_griditem, null);
			holder.iv_cover = (ImageView) convertView
					.findViewById(R.id.local_source_cover);
			 holder.pb_downloading = (ProgressBar) convertView
			 .findViewById(R.id.downloading_prgbr);
			convertView.setTag(holder);
		} else {
			holder = (LocalSourceGridHolder) convertView.getTag();
		}
		holder.iv_cover.setImageResource(localSourceCover[position]);
		if (position < 3) {
			holder.pb_downloading.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	private class LocalSourceGridHolder {
		ImageView iv_cover;
		ProgressBar pb_downloading;
	}

}
