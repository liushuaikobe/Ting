package org.footoo.ting.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

public class HttpUtil {

	/**
	 * 读取输入流
	 * 
	 * @param inStream
	 *            输入流
	 * @return 该流中的字节数组
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	/**
	 * 直接从给定的URL下载图片
	 * 
	 * @param url
	 *            图片的URL
	 * @param src_name
	 *            下载后图片的名字
	 * @return 下载好的图片,Drawable的格式
	 */
	public static Drawable loadImageFromUrl(String url, String src_name) {
		InputStream is = null;
		try {
			URL imgUrl = new URL(url);
			is = (InputStream) imgUrl.getContent();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Drawable d = Drawable.createFromStream(is, src_name);
		return d;
	}

	/**
	 * 下载图片到本地
	 * 
	 * @param folder
	 *            图片保存的文件夹
	 * @param url
	 *            图片的URL
	 * @return 下载好的图片
	 */
	public static Drawable loadImageFromUrlWithStore(String folder, String url,
			int reqWidth, int reqHeight) {
		Log.e("loadImageFromUrlWithStore", "reqWidth:" + reqWidth
				+ "    reqHeight" + reqHeight);
		try {
			String fileName = url.substring(url.lastIndexOf("/") + 1); // e.g.
																		// 2012611052497940.jpg
			URL imageUrl = new URL(url);
			byte[] data = readInputStream((InputStream) imageUrl.openStream());
			Bitmap bitmap = FileAccess.decodeSampledBitmapFromByteArray(data,
					reqWidth, reqHeight);
			// Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
			// data.length);
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				FileAccess.MakeDir(folder);
				String outFilename = folder + fileName;
				bitmap.compress(CompressFormat.PNG, 10, new FileOutputStream(
						outFilename));
				bitmap.recycle();

				Bitmap bitmapCompress = FileAccess
						.decodeSampledBitmapFromResource(outFilename, reqWidth,
								reqHeight);
				// Bitmap bitmapCompress =
				// BitmapFactory.decodeFile(outFilename);
				Drawable drawable = new BitmapDrawable(bitmapCompress);
				return drawable;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}