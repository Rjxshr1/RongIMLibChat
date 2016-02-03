package github.common.utils;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import com.lidroid.xutils.util.LogUtils;
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class DiskUtil
{
	/**得到一个 DiskLruCache实例  默认 128MB
	 * 使用之前，一定要先open 
	 * @param context 上下文
	 * @param cacheDir 缓存文件夹的路径，不需要携带SD卡的根目录，直接写 XX软件
	 * */
	public static DiskLruCache open(Context context, String cacheDir){
		return open(context, cacheDir, 1024*1024*128);
	}
	/**得到一个 DiskLruCache实例
	 * 使用之前，一定要先open 
	 * @param context 上下文
	 * @param cacheDir 直接写 项目名 | 模块名
	 * @param maxSize 这个缓存文件夹 要存放多大的文件 单位/字节(B)
	 * */
	public static DiskLruCache open(Context context, String cacheDir, long maxSize)
	{
		File directory = getDiskCacheDir(context, cacheDir);
		int appVersion = getAppVersion(context);
		try
		{
			DiskLruCache diskLruCache = DiskLruCache.open(directory, appVersion, 1, maxSize);
			return diskLruCache;
		} catch (IOException e){
			LogUtils.e("有异常："+e);
		}
		return null;
	}

	/**
	 * @param key 键(未加密)  方法内部进行MD5加密
	 * */
	public static Editor edit(DiskLruCache diskLruCache, String key)
	{
		if (diskLruCache == null || diskLruCache.isClosed() || TextUtils.isEmpty(key)){
			return null;
		}
		String md5Key = getMD5String(key);
		try
		{
			LogUtils.e("key = "+key);
			LogUtils.e("md5Key = "+md5Key);
			Editor editor = diskLruCache.edit(md5Key);
			return editor;
		} catch (IOException e){
			LogUtils.e("有异常：" + e);
		}finally{
			flush(diskLruCache);
		}
		return null;
	}
	/**
	 * @param key 键(未加密)
	 * */
	public static InputStream newInputStream(DiskLruCache diskLruCache, String key)
	{
		InputStream inputStream = null;
		try
		{
			Editor editor = edit(diskLruCache, key);
			if(editor==null){
				return null;
			}
			inputStream = editor.newInputStream(0);
			LogUtils.e("inputStream = null = "+(inputStream==null));
			if(inputStream == null){
				return null;
			}
			return inputStream;
		} catch (Exception e){
			LogUtils.e("有异常："+e);
		}finally{
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			flush(diskLruCache);
		}
		return null;
	}
	public static OutputStream newOutputStream(DiskLruCache diskLruCache, String key)
	{
		Editor editor = edit(diskLruCache, key);
		try
		{
			if(editor == null){
				return null;
			}
			return editor.newOutputStream(0);
		} catch (IOException e){
			e.printStackTrace();
		}finally{
			flush(diskLruCache);
		}
		return null;
	}
	public static String getString(DiskLruCache diskLruCache, String key)
	{
		Editor editor = edit(diskLruCache, key);
		if(editor == null){
			return null;
		}
		try{
			String result = editor.getString(0);
			if(!TextUtils.isEmpty(result)){
				editor.commit();
			}else{
				editor.abortUnlessCommitted();
			}
			return result;
		} catch (IOException e){
			LogUtils.e("有异常："+e);
		}finally{
			flush(diskLruCache);
		}
		return null;
	}
	/**
	 * @param key 键(未加密)
	 * */
	public static byte[] getByte(DiskLruCache diskLruCache, String key){
		ByteArrayOutputStream output = null;
		InputStream inputStream = null;
		Snapshot snapshot = null;
		try {
			String md5Key = getMD5String(key);
			snapshot = diskLruCache.get(md5Key);
			if(snapshot == null){
				return null;
			}
			inputStream = snapshot.getInputStream(0);
			if(inputStream==null){
				return null;
			}
			output = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int n = 0;
			while (-1 != (n = inputStream.read(buffer))) {
				output.write(buffer, 0, n);
			}
			byte[] byteArray = output.toByteArray();
			output.flush();
			return byteArray;
		} catch (IOException e) {
			LogUtils.e("有异常："+e);
		}finally{
			try {
				if(output!=null){
					output.close();
				}
				if(inputStream!=null){
					inputStream.close();
					snapshot.edit().commit();
					snapshot.close();
				}
				flush(diskLruCache);
			} catch (IOException e) {
				LogUtils.e("有异常："+e);
			}
		}
		return null;
	}
	public static void close(DiskLruCache diskLruCache){
		if(diskLruCache!=null && !diskLruCache.isClosed()){
			try
			{
				diskLruCache.close();
			} catch (IOException e){
				LogUtils.e("有异常："+e);
			}
		}
	}
	public static void flush(DiskLruCache diskLruCache)
	{
		if(diskLruCache!=null && !diskLruCache.isClosed())
		{
			try{
				diskLruCache.flush();
			} catch (IOException e){
				LogUtils.e("有异常："+e);
			}
		}
	}
	public static void write(DiskLruCache diskLruCache, String key, final byte buffer[])
	{
		OutputStream outputStream =null;
		BufferedOutputStream buffOS  = null;
		try
		{
			String md5Key = getMD5String(key);
			Editor editor = diskLruCache.edit(md5Key);
			outputStream = editor.newOutputStream(0);
			buffOS = new BufferedOutputStream(outputStream);
			buffOS.write(buffer, 0, buffer.length);
			buffOS.flush();
			editor.commit();
		} catch (IOException e){
			LogUtils.e("有异常："+e);
		}finally{
			try{
				if(outputStream!=null){
					outputStream.close();
				}
				if(buffOS!=null){
					buffOS.close();
				}
			} catch (IOException e){
				LogUtils.e("有异常："+e);
			}
			flush(diskLruCache);
		}
	}
	private static File getDiskCacheDir(Context context, String cacheDir)
	{
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()){
			if (context.getExternalCacheDir()!=null){
				cachePath = context.getExternalCacheDir().getAbsolutePath();
			}else{
				cachePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			}
		}
		else{
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + cacheDir);
	}
	private static int getAppVersion(Context context)
	{
		try
		{
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e){
			LogUtils.e("有异常："+e);
		}
		return 1;
	}
	/**根据 key，获取全路径 包含后缀名*/
	public static String getPath(DiskLruCache diskLruCache, String key){
		String path = diskLruCache.getDirectory()+File.separator+getMD5String(key)+".0";
		return path;
	}
	/** MD5加密，换取 文件名的 key */
	public static String getMD5String(String key)
	{
		String md5Key;
		try
		{
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			md5Key = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e){
			md5Key = String.valueOf(key.hashCode());
		}
		return md5Key;
	}
	private static String bytesToHexString(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)
		{
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1){
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}
