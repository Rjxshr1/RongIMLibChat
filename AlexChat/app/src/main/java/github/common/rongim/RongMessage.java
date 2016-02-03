package github.common.rongim;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.alex.alexchat.bean.LocationBean;

import io.rong.imlib.model.Message;
/**
 * Created by AlexCheung on 2016/2/2.
 */
public class RongMessage extends Message {
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
    public RongMessage(String millisTime) {
        this.millisTime = millisTime;
    }
    public RongMessage(){

    }
    @Override
    public int describeContents(){
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(messageId);
        dest.writeString(senderUserId);
        dest.writeString(senderPortraitUri);
        dest.writeString(senderUserName);
        dest.writeString(targetId);
        dest.writeString(targetName);
        dest.writeString(targetPortraitUri);
        dest.writeString(text);
        dest.writeString(imageUri.toString());
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address);
        dest.writeString(voiceUri.toString());
        dest.writeLong(length);
        dest.writeString(isFinished + "");
        dest.writeString(isShowed+"");
        dest.writeString(millisTime);
    }
    public static final Parcelable.Creator<RongMessage> CREATOR  = new Creator<RongMessage>() {
        //实现从source中创建出类的实例的功能
        @Override
        public RongMessage createFromParcel(Parcel source) {
            RongMessage bean  = new RongMessage();
            bean.messageId = source.readInt();
            bean.senderUserId= source.readString();
            bean.senderPortraitUri = source.readString();
            bean.senderUserName = source.readString();
            bean.targetId = source.readString();
            bean.targetName = source.readString();
            bean.targetPortraitUri = source.readString();
            bean.text = source.readString();
            bean.imageUri = Uri.parse(source.readString());
            bean.latitude = source.readDouble();
            bean.longitude = source.readDouble();
            bean.address = source.readString();
            bean.voiceUri = Uri.parse(source.readString());
            bean.length = source.readLong();
            bean.isFinished = Boolean.getBoolean(source.readString());
            bean.isShowed = Boolean.getBoolean(source.readString());
            bean.millisTime = source.readString();
            return bean;
        }
        //创建一个类型为T，长度为size的数组
        @Override
        public RongMessage[] newArray(int size) {
            return new RongMessage[size];
        }
    };
}
