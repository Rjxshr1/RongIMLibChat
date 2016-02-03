package com.alex.alexchat.config;
public class App
{
	public static final String loadTypeFirst = "loadTypeFirst";
	public static final String loadTypeMore = "loadTypeMore";
	public static final String loadTypeRefresh = "loadTypeRefresh";
	public static final String keyModuleName = "keyModuleName";
	public static final String keyChatType = "keyChatType";
	public static final String keyRyUserId = "keyRyUserId";
	public static final String keyRyUserToken = "keyUserToken";
	public static final String keyRyUserName = "keyUserName";
	public static final String keyRyPortraitUri = "keyRyPortraitUri";
	public static final String keyTargetId = "keyTargetID";
	public static final String keyTargetPortraitUri = "keyTargetPortraitUri";
	public static final String keyTargetName= "keyTargetName";
	public static final String chatTypeSingle = "chatTypeSingle";
	public static final String chatTypeGroup = "chatTypeGroup";
	public static final String keyMustConnectRongIMServer = "keyMustConnectRongIMServer";
	public static final String keyIsLoopConversation = "keyIsLoopConversation";
	public static String getChatType(int value){
		if(value == 1){
			return chatTypeSingle;
		}
		return chatTypeSingle;
	}
}
