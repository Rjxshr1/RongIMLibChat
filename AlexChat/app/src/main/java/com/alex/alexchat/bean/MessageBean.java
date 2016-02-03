package com.alex.alexchat.bean;
import android.net.Uri;
/**消息实体类*/
public class MessageBean 
{
	/**消息 id*/
	public int messageId;
	/**发送消息的人 的id*/
	public String senderUserId;
	/**发送消息的人 的头像*/
	public String senderPortraitUri;
	/**发送消息的人 的名称*/
	public String senderUserName;
	/**消息目标的 id*/
	public String targetId;
	/**消息目标的 名称*/
	public String targetName;
	/**消息目标的 头像*/
	public String targetPortraitUri;
	/**消息 文本内容*/
	public String text;
	/**消息 图片链接*/
	public Uri imageUri;
	/**单位 秒*/
	public int duration;
	/**维度*/
	public double latitude;
	/**经度*/
	public double longitude;
	/**地址信息*/
	public String address;
	public Uri voiceUri;
	public long length;
	/**true 图片 高 > 宽 */
	public String extra;
	/**定位信息 实体类*/
	public LocationBean locationBean;
	/**消息类型*/
	public int messageType;
	/**消息更新完成*/
	public boolean isFinished;
	/**图片 消息 已经展示过了*/
	public boolean isShowed;
	/**消息发布时间，精确到 毫秒*/
	public String millisTime;
	public String base64;
	public MessageBean(String millisTime) {
		this.millisTime = millisTime;
	}
}
