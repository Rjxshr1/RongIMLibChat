package com.alex.alexchat.config;
import github.common.utils.SDCardUtil;

import java.io.File;

import android.os.Environment;
public class Cache 
{
	public static String split = File.separator;
	public static final int ramCacheSize = 1024*1024*32;
	public static final int sdCacheSize = 1024*1024*128;
	public static final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath()+split;
	/**0/sdcard/AlexChat/*/
	public static final String cacheDir = rootPath+"AlexChat"+split;
	public static final String dbDir = cacheDir+"DataBase";
	public static final String dbName = "fiendsList.db";
	public static final int dbVersion = 1;
	/**Linux文件夹分割符*/
	public static String imageDiskCacheDir = "imageCache";
	public static String voiceDiskCacheDir = "voiceCache";
	/**保存在 附近逛/tmp.java 下*/
	public static void saveUserListJson(String result){
		SDCardUtil.saveFile(result.getBytes(), cacheDir+"userList.json", false);
	}
	public static String getUserListJson(){
		return new String(SDCardUtil.getFileFromSDCard(cacheDir+"userList.json"));
	}
	public static void saveChatRoomLabelsInfo(String result){
		SDCardUtil.saveFile(result.getBytes(), cacheDir+"chatRoomLabelsInfo.text", false);
	}
	public static void saveChatRoomLabelBadgeInfo(String result){
		SDCardUtil.saveFile(result.getBytes(), cacheDir+"saveChatRoomLabelBadgeInfo.text", false);
	}
	public static void saveConversationTmp(String result){
		SDCardUtil.saveFile(result.getBytes(), cacheDir+"saveConversationTmp.text", false);
	}
}
