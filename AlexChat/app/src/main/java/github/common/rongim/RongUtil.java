package github.common.rongim;

import android.net.Uri;

import com.socks.library.KLog;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by AlexCheung on 2016/1/27.
 */
public class RongUtil {
    public static String errorCode2String(RongIMClient.ErrorCode errorCode){
        if("BIZ_ERROR_CLIENT_NOT_INIT".equalsIgnoreCase(errorCode.name())){
            return "未调用 init 初始化函数";
        }else if("BIZ_ERROR_CONNECTING".equalsIgnoreCase(errorCode.name())){
            return "连接中，再调用 connect 被拒绝";
        }else if("BIZ_ERROR_DATABASE_ERROR".equalsIgnoreCase(errorCode.name())){
            return "数据库初始化失败";
        }else if("BIZ_ERROR_INVALID_PARAMETER".equalsIgnoreCase(errorCode.name())){
            return "传入参数无效";
        }else if("BIZ_ERROR_NO_CHANNEL".equalsIgnoreCase(errorCode.name())){
            return "通道无效";
        }else if("BIZ_ERROR_RECONNECT_SUCCESS".equalsIgnoreCase(errorCode.name())){
            return "重新连接成功";
        }else if("IPC_DISCONNECT".equalsIgnoreCase(errorCode.name())){
            return "通信 进程意外终止";
        }else if("NOT_IN_CHATROOM".equalsIgnoreCase(errorCode.name())){
            return "不在聊天室";
        }else if("NOT_IN_DISCUSSION".equalsIgnoreCase(errorCode.name())){
            return "不在讨论组";
        }else if("NOT_IN_GROUP".equalsIgnoreCase(errorCode.name())){
            return "不在群组";
        }else if("RC_CONN_ACK_TIMEOUT".equalsIgnoreCase(errorCode.name())){
            return "做 connect 连接时，收到的 ACK 超时";
        }else if("RC_CONN_APP_BLOCKED_OR_DELETED".equalsIgnoreCase(errorCode.name())){
            return "APP 被屏蔽、删除或不存在";
        }else if("RC_CONN_ID_REJECT".equalsIgnoreCase(errorCode.name())){
            return "参数错误，App Id 错误";
        }else if("RC_CONN_NOT_AUTHRORIZED".equalsIgnoreCase(errorCode.name())){
            return " App Id 与 Token 不匹配";
        }else if("RC_CONN_PACKAGE_NAME_INVALID".equalsIgnoreCase(errorCode.name())){
            return "NAME 与后台注册信息不一致";
        }else if("RC_CONN_PROTO_VERSION_ERROR".equalsIgnoreCase(errorCode.name())){
            return "参数错误";
        }else if("RC_CONN_REDIRECTED".equalsIgnoreCase(errorCode.name())){
            return "重定向，地址错误";
        }else if("RC_CONN_SERVER_UNAVAILABLE".equalsIgnoreCase(errorCode.name())){
            return "服务器不可用";
        }else if("RC_CONN_USER_BLOCKED".equalsIgnoreCase(errorCode.name())){
            return "用户被屏蔽";
        }else if("RC_CONN_USER_OR_PASSWD_ERROR".equalsIgnoreCase(errorCode.name())){
            return "Token 错误";
        }else if("RC_DISCONN_EXCEPTION".equalsIgnoreCase(errorCode.name())){
            return "Disconnect，由服务器返回，比如用户互踢";
        }else if("RC_DISCONN_KICK".equalsIgnoreCase(errorCode.name())){
            return "Disconnect，由服务器返回，比如用户互踢";
        }else if("RC_DOMAIN_NOT_RESOLVE".equalsIgnoreCase(errorCode.name())){
            return "导航数据解析后，其中不存在有效 IP 地址";
        }else if("RC_HTTP_RECV_FAIL".equalsIgnoreCase(errorCode.name())){
            return "HTTP 接收失败";
        }else if("RC_HTTP_REQ_TIMEOUT".equalsIgnoreCase(errorCode.name())){
            return "HTTP 请求失败";
        }else if("RC_HTTP_SEND_FAIL".equalsIgnoreCase(errorCode.name())){
            return "导航操作时，Http 请求失败";
        }else if("RC_MSG_DATA_INCOMPLETE".equalsIgnoreCase(errorCode.name())){
            return "协议层内部错误";
        }else if("RC_MSG_RESP_TIMEOUT".equalsIgnoreCase(errorCode.name())){
            return "通信超时";
        }else if("RC_MSG_SEND_FAIL".equalsIgnoreCase(errorCode.name())){
            return "消息发送失败";
        }else if("RC_NAVI_RESOURCE_ERROR".equalsIgnoreCase(errorCode.name())){
            return "导航操作的 HTTP 请求，返回不是200";
        }else if("RC_NET_CHANNEL_INVALID".equalsIgnoreCase(errorCode.name())){
            return "通信过程中，当前 Socket 不存在";
        }else if("RC_NET_UNAVAILABLE".equalsIgnoreCase(errorCode.name())){
            return "Socket 连接不可用";
        }else if("RC_NODE_NOT_FOUND".equalsIgnoreCase(errorCode.name())){
            return "导航数据解析后，其中不存在有效数据";
        }else if("RC_PING_SEND_FAIL".equalsIgnoreCase(errorCode.name())){
            return "PING 操作失败";
        }else if("RC_PONG_RECV_FAIL".equalsIgnoreCase(errorCode.name())){
            return "PING 超时";
        }else if("RC_QUERY_ACK_NO_DATA".equalsIgnoreCase(errorCode.name())){
            return "协议层内部错误";
        }else if("RC_SOCKET_DISCONNECTED".equalsIgnoreCase(errorCode.name())){
            return "Socket 被断开";
        }else if("RC_SOCKET_NOT_CREATED".equalsIgnoreCase(errorCode.name())){
            return "创建 Socket 失败";
        }else if("REJECTED_BY_BLACKLIST".equalsIgnoreCase(errorCode.name())){
            return "在黑名单中";
        }
        return "未知错误";
    }
    /**
     * 处理消息
     *
     * @category 处理消息
     */
    public static String getMessageSummary(MessageContent messageContent) {
        /*收到好友验证消息*/
        if (messageContent instanceof TextMessage) {
            /*收到文本消息*/
            TextMessage textMessage = (TextMessage) messageContent;
            return textMessage.getContent();
        } else if (messageContent instanceof ImageMessage) {
            /*收到图片消息*/
            ImageMessage imageMessage = (ImageMessage) messageContent;
            KLog.w("getRemoteUri = " + imageMessage.getRemoteUri());
            return "图片消息";
        } else if (messageContent instanceof LocationMessage) {
            /*位置信息*/
            return "位置消息";
        } else if (messageContent instanceof VoiceMessage) {
            return "语音消息";
        }
        return "新消息";
    }
    public static String getUserName(Conversation conversation){
        UserInfo userInfo = conversation.getLatestMessage().getUserInfo();
        if (userInfo != null) {
            return userInfo.getName();
        }
        return "陌生人";
    }
    public static Uri getPortraitUri(Conversation conversation){
        UserInfo userInfo = conversation.getLatestMessage().getUserInfo();
        if (userInfo != null) {
            return userInfo.getPortraitUri();
        }
        return Uri.parse("www.baidu.com");
    }
}
