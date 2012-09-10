package org.footoo.ting.util;

import java.io.File;
import java.text.DecimalFormat;

import android.os.Environment;
import android.util.Log;

public class FileUtil {
	/**
	 * 创建文件夹
	 * 
	 * @param dirName
	 */
	public static void MakeDir(String dirName) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File destDir = new File(dirName);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
		}
	}

	/**
	 * 删除某一文件夹下所有的文件(注意：如果该文件夹下还有文件夹，将不会被删除)
	 * 
	 * @param folder
	 *            文件夹的路径
	 */
	public static void deleteAllFiles(String folder) {
		File file = new File(folder);
		if (!file.exists()) {
			Log.e("deleteAllFiles", "foler doesn't exists");
			return;
		}
		if (!file.isDirectory()) {
			Log.e("deleteAllFiles", "folder isn't a Directory");
			return;
		}
		File fileList[] = file.listFiles();
		for (File f : fileList) {
			if (f.isFile()) {
				f.delete();
			}
		}
		Log.i("deleteAllFiles", "clean complete");
	}

	/**
	 * 获取某一文件夹下所有文件的大小总和(注意：如果该文件夹下还有子文件夹，子文件夹的大小将不会被计算)
	 * 
	 * @param folder
	 *            文件夹的路径（如果传进来的是一个文件，则直接返回它的大小）
	 * @return 问价大小的总和
	 */
	public static long getFolderSize(String folder) {
		File file = new File(folder);
		if (!file.exists()) {
			return -1L;
		}
		if (!file.isDirectory()) {
			return file.length();
		}
		long sum = 0L;
		File fileList[] = file.listFiles();
		for (File f : fileList) {
			if (f.isFile()) {
				sum += f.length();
			}
		}
		Log.i("getFolderSize", "get completed  -- " + sum);
		return sum;
	}

	/**
	 * 转换文件大小，变成友好的易读的字符串
	 * 
	 * @param fileS
	 *            文件的大小
	 * @return
	 */
	public static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}
}
