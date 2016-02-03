package github.common.utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;

import android.net.Uri;
import android.webkit.MimeTypeMap;
public final class FileUtil {
	private static DecimalFormat mDecimalFormat = new DecimalFormat("0.0");
	/**获取文件的 Mime类型*/
	public static String getMimeType(File file) 
	{
		String extension = getExtension(file.getName());
		if (extension.length() > 0){
			return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
		}
		return "application/octet-stream";
	}
	/**获取文件的父目录路径
	 * <br/>getPathWithoutFilename("C:\Folder\File.txt") == "C:\Folder\"
	 * <br/>getPathWithoutFilename("C:\Folder") == "C:\Folder"
	 */
	public static File getPathWithoutFilename(File file) 
	{
		if (file != null) 
		{
			if (file.isDirectory()) {
				return file;
			} else {
				String filename = file.getName();
				String filepath = file.getAbsolutePath();
				String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
				if (pathwithoutname.endsWith("/")) {
					pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
				}
				return new File(pathwithoutname);
			}
		}
		return null;
	}
	/**获取文件的 uri*/
	public static Uri getUri(File file) {
		if (file != null) {
			return Uri.fromFile(file);
		}
		return null;
	}
	/**获取文件扩展名, like ".png" or ".jpg" 
	 * @return 没有扩展名, 返回 "" */
	public static String getExtension(String fileName) 
	{
		if (fileName == null) {
			return null;
		}
		int dot = fileName.lastIndexOf(".");
		if (dot >= 0) {
			return fileName.substring(dot);
		} else {
			return "";
		}
	}
	/**获取目标文件的大小
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String getFileAutoSize(File file){
		long size = getFileSize(file);
		if (size < 1024) {
			return size + "B";
		}else if (size < Math.pow(1024, 2)) {
			return mDecimalFormat.format(size / 1024) + "K";
		}else if (size < Math.pow(1024, 3)) {
			return mDecimalFormat.format(size / Math.pow(1024, 2)) + "M";
		}else {
			return mDecimalFormat.format(size / Math.pow(1024, 3)) + "G";
		}
	}
	/**获取目标文件大小且指定单位
	 * @param file
	 * @param unit Size.K
	 * @return 
	 */
	public static String getFileSize(File file, Size unit){
		long size = getFileSize(file);
		if (unit == Size.K || unit == Size.KB) {
			return size / 1024 + unit.name;
		}else if (unit == Size.M || unit == Size.MB) {
			return size / Math.pow(1024, 2) + unit.name;
		}else if (unit == Size.G || unit == Size.GB) {
			return size / Math.pow(1024, 3) + unit.name;
		}else {
			return size + "B";
		}
	}
	/**
	 * 获取文件大小
	 * @param file
	 * @return 出错，返回-1
	 */
	public static long getFileSize(File file){
		if (!file.exists() || file == null) {
			return -1;
		}
		long size = 0;
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				size += getFileSize(f);
			}else {
				size += f.length();
			}
		}
		return size;
	}
	public enum Size{
		B("B"), K("K"), M("M"), G("G"), KB("KB"), MB("MB"), GB("GB");
		public String name;
		private Size(String name){
			this.name = name;
		}
	}
}
