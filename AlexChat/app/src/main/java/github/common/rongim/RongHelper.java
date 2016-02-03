package github.common.rongim;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.lidroid.xutils.util.LogUtils;
import com.socks.library.KLog;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
import io.rong.imlib.model.UserData;

public class RongHelper {
    public RongIMClient rongIMClient;
    private RongHelperListener rongHelperListener;
    private String userToken;
    private String userId;
    private Activity activity;
    private UserData userData;
    private ConnectionHandler connectionHandler;
    private OperationHandler operationHandler;
    public static final int rongSuccess = 100;
    public static final int rongFail = 101;
    public static final int rongStart = 102;
    public static final int rongLoading = 103;
    public static final int rongFailTokenIsNull = 104;
    public static final long messageDelayed = 2000;
    public RongHelper(Activity activity, String userId, String userToken) {
        this.activity = activity;
        this.userId = userId;
        this.userToken = userToken;
        this.connectionHandler = new ConnectionHandler();
        this.operationHandler = new OperationHandler();
        this.rongIMClient = RongIMClient.getInstance();
    }

    public void doLogin(String userName, String portraitUri, RongHelperListener rongHelperListener) {
        this.rongHelperListener = rongHelperListener;
        ConnectionStatus status = rongIMClient.getCurrentConnectionStatus();
        Message message = Message.obtain();
        if(ConnectionStatus.CONNECTED == status){
            message.what = rongSuccess;
            connectionHandler.sendMessageDelayed(message,messageDelayed);
        }else if(ConnectionStatus.CONNECTING == status){
            message.what = rongLoading;
            connectionHandler.sendMessage(message);
        }else{
            KLog.e("之前没有在线  现在开始登录");
            if(TextUtils.isEmpty(userToken)){
                message.what = rongFailTokenIsNull;
                message.obj = "token 为空";
                connectionHandler.sendMessageDelayed(message,messageDelayed);
            }else{
                RongIMClient.connect(userToken, new MyConnectCallback());
                message.what = rongStart;
                connectionHandler.sendMessage(message);
            }
        }
        UserData userData = new UserData(activity);
        UserData.AccountInfo accountInfo = new UserData.AccountInfo();
        accountInfo.setUserName(userName);
        UserData.PersonalInfo personalInfo = new UserData.PersonalInfo();
        personalInfo.setPortraitUri(portraitUri);
        userData.setAccountInfo(accountInfo);
        userData.setPersonalInfo(personalInfo);
        this.userData = userData;
    }
    /**融云登录的接口回调*/
    private final class MyConnectCallback extends RongIMClient.ConnectCallback {
        @Override
        public void onTokenIncorrect() {
            KLog.e("token 错误");
            if (rongHelperListener != null) {
                rongHelperListener.onFail(HelperStatus.tokenIsNull, "token 错误");
            }
            Message message = Message.obtain();
            message.what = rongFail;
            message.obj = "token 错误";
            connectionHandler.sendMessage(message);
        }
        @Override
        public void onSuccess(String userId) {
            KLog.e("登录成功 userId = " + userId);
            if (rongHelperListener != null) {
                Message message = Message.obtain();
                message.what = rongSuccess;
                connectionHandler.sendMessageDelayed(message,messageDelayed);
                rongIMClient.syncUserData(userData, new MyOperationCallback());
            }
        }

        @Override
        public void onError(RongIMClient.ErrorCode errorCode) {
            KLog.e("登录出错 errorCode = " + errorCode.getMessage());
            Message message = Message.obtain();
            message.what = rongFail;
            message.obj = errorCode.getMessage();
            connectionHandler.sendMessage(message);
        }

        @Override
        public void onCallback(String userId) {
            //KLog.e("登录回调 userId = " + userId);
            rongIMClient.syncUserData(userData, new MyOperationCallback());
            Message message = Message.obtain();
            message.what = rongSuccess;
            connectionHandler.sendMessage(message);
        }

        @Override
        public void onFail(RongIMClient.ErrorCode errorCode) {
            Message message = Message.obtain();
            message.what = rongFail;
            ConnectionStatus status = rongIMClient.getCurrentConnectionStatus();
            if(ConnectionStatus.NETWORK_UNAVAILABLE == status){
                message.obj = "网络异常";
                KLog.e("网络异常");
            }else if(ConnectionStatus.TOKEN_INCORRECT == status){
                message.obj = "token 出错";
                KLog.e("token 出错");
            }else if(ConnectionStatus.SERVER_INVALID == status){
                message.obj = "融云停止对您的服务";
                KLog.e("token 出错");
            }else{
                message.obj = RongUtil.errorCode2String(errorCode);
                KLog.e("登录失败 errorCode = " + RongUtil.errorCode2String(errorCode));
            }
            connectionHandler.sendMessage(message);
        }

        @Override
        public void onFail(int errorCode) {
            LogUtils.e("登陆失败 errorCode = " + errorCode);
            Message message = Message.obtain();
            message.what = rongSuccess;
            message.obj = "错误码 = "+errorCode;
            connectionHandler.sendMessage(message);
        }

    }
    /**融云同步 UserData的接口回调*/
    private final class MyOperationCallback extends RongIMClient.OperationCallback
    {
        @Override
        public void onSuccess() {
            KLog.e("同步用户信息成功");
            Message message = Message.obtain();
            message.what = rongSuccess;
            message.obj = "同步用户信息成功";
            operationHandler.sendMessage(message);
        }
        @Override
        public void onError(RongIMClient.ErrorCode errorCode) {
            KLog.e(RongUtil.errorCode2String(errorCode));
            Message message = Message.obtain();
            message.what = rongFail;
            message.obj = RongUtil.errorCode2String(errorCode);
            operationHandler.sendMessage(message);
        }
    }
    @SuppressLint("HandlerLeak")
    private final class ConnectionHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            String error = (String) msg.obj;
            if (code == rongSuccess) {
                if (rongHelperListener != null) {
                    rongHelperListener.onSuccess(HelperStatus.connectionSuccess, userId);
                }
            } else if (code == rongStart) {
                if (rongHelperListener != null) {
                    rongHelperListener.onStart();
                }
            } else if (code == rongLoading) {
                KLog.e("连接中");
                if (rongHelperListener != null){
                    rongHelperListener.onLoading();
                }
            }else if(code == rongFailTokenIsNull){
                if (rongHelperListener != null){
                    rongHelperListener.onFail(HelperStatus.tokenIsNull,error);
                }
            }
        }
    }
    private final class OperationHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == rongSuccess){
                rongHelperListener.onSuccess(HelperStatus.syncSuccess, userId);
            }else{
                rongHelperListener.onFail(HelperStatus.syncFail, msg.obj.toString());
            }
        }
    }
    public interface RongHelperListener
    {
        /**开始 连接*/
        public void onStart();
        /**连接成功*/
        public void onSuccess(HelperStatus helperStatus, String userId);
        /**连接失败*/
        public void onFail(HelperStatus errorType, String errorMsg);
        /**正在连接*/
        public void onLoading();
    }
    public enum HelperStatus
    {
        /**token 为空*/
        tokenIsNull,
        /**登陆成功*/
        connectionSuccess,
        /**同步用户数据 成功*/
        syncSuccess,
        /**同步用户数据 失败*/
        syncFail
    }
}
