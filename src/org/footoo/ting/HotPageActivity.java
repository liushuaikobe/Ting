package org.footoo.ting;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.footoo.ting.adapter.HotPageLargePicAdapter;
import org.footoo.ting.adapter.HotPageThumbPicAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.media.BookInstance;
import org.footoo.ting.media.PlayerEngine;
import org.footoo.ting.media.ServerInfo;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.SizeCallBackForMenu;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class HotPageActivity extends MainBaseActivity {

	public static String URL = "url of data";
	public static ArrayList<BookInstance> hotBook;
	private ViewFlow viewFlow;
	private GridView thumbGridView;
	private ImageView broadcastController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("cain", "current thread:" + Thread.currentThread().getId());
		new InitializeTask().execute(ServerInfo.ServerURl);
		
		initViewFlow();
	}

	private void initViews() {
		scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);

		contentPage = LayoutInflater.from(this).inflate(
				R.layout.layout_hot_page, null);

		slideBtn = (Button) contentPage.findViewById(R.id.slideBtn);
		slideBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				scrollView.clickSlideButton();
			}
		});

		View leftView = new View(this);
		leftView.setBackgroundColor(Color.TRANSPARENT);
		final View[] children = new View[] { leftView, contentPage };
		scrollView.initViews(children, new SizeCallBackForMenu(slideBtn),
				(View) findViewById(R.id.menu_layout));
		scrollView.setSlideButton(slideBtn);

		// 为菜单设置adapter
		underView.setAdapter(new MenuGridAdapter(HotPageActivity.this,
				MenuGridAdapter.HOT_PAGE_ID));

		thumbGridView = (GridView) findViewById(R.id.hot_thumbnail_gridView);

		// 为HotPage地下的小GridView设置adapter
		thumbGridView.setAdapter(new HotPageThumbPicAdapter(
				HotPageActivity.this, hotBook));
		
		broadcastController = (ImageView) ((View) contentPage
				.findViewById(R.id.tmp_btm_ctrl))
				.findViewById(R.id.bottom_control_btn);

		broadcastController.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(HotPageActivity.this,
						PlayerEngine.class);
				String uri = "http://172.16.244.31/Apologize.mp3";
				intent.putExtra(URL, uri);
				startService(intent);
			}
		});
	}

	private void initViewFlow() {
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setAdapter(new HotPageLargePicAdapter(HotPageActivity.this));
		viewFlow.setmSideBuffer(3);

		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);

		// 为large picture设置点击事件
		viewFlow.getChildAt(0).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(HotPageActivity.this, "fuck", Toast.LENGTH_SHORT)
						.show();
			}
		});

		viewFlow.setSelection(3 * 1000);
		viewFlow.startAutoFlowTimer();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (MainHorizontalScrollView.underViewIsOut == true) {
				scrollView.clickSlideButton();
			} else {
				this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private Bitmap getImageByURL(URL paramURL) throws IOException  
    {  
        URLConnection mURLConnection = paramURL.openConnection();  
        mURLConnection.connect();  
        InputStream mInputStream = mURLConnection.getInputStream();  
        BufferedInputStream mBufferedInputStream = new BufferedInputStream(mInputStream);  
        Bitmap mBitmap = BitmapFactory.decodeStream(mBufferedInputStream);  
        mBufferedInputStream.close();  
        mInputStream.close();  
        return mBitmap;  
    }
	
	
	private class InitializeTask extends AsyncTask<String, Integer, Integer> {

		ProgressDialog dlg;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(HotPageActivity.this);
			dlg.setTitle("最新");
			dlg.setMessage("获取推荐书目中.......");
			dlg.setCancelable(false);
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dlg.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			
			String urlString = params[0]+"/hot/hot.json";
			HttpURLConnection urlConnection = null;
			InputStreamReader in = null;
			URL url = null;
			BufferedReader buffer = null;
			StringBuilder mStringBuilder = new StringBuilder();
			try {
				url = new URL(urlString);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setReadTimeout(10000 /* milliseconds */);
		        urlConnection.setConnectTimeout(15000 /* milliseconds */);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();
				
				in = new InputStreamReader(urlConnection.getInputStream());
				buffer = new BufferedReader(in);
				
				
				for (String str = buffer.readLine(); str != null; str = buffer.readLine()) {
					mStringBuilder.append(str);
				}

			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			JSONObject mJSONObject = null;
			int category_id = 1000;
			try {
				mJSONObject = new JSONObject(mStringBuilder.toString());
				category_id = mJSONObject.getInt("category_id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (category_id == 0) {
				Log.v("cain", "success");
				JSONArray hotContent = null;
				try {
					hotContent = mJSONObject.getJSONArray("content");
					Log.v("cain", "content success "+hotContent.length());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
				for(int i=0; i<hotContent.length(); i++){
					
					JSONObject eachBook = (JSONObject) hotContent.opt(i);
					BookInstance book;
					try {
						book = new BookInstance(category_id, eachBook.getInt("source_id") , eachBook.getString("source_name"));
						String a = eachBook.getString("img_url");
						URL b = new URL(a);
						Log.v("cain", "process"+a);
						book.coverBitmap = HotPageActivity.this.getImageByURL(b);
						hotBook.add(book);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			initViews();
		}

		@Override
		protected void onPostExecute(Integer result) {
			dlg.dismiss();
		}

	}
}
