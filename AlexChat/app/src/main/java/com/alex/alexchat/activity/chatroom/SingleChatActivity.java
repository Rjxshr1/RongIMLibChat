package com.alex.alexchat.activity.chatroom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.BaseActivity;
import com.alex.alexchat.activity.LocationActivity;
import com.alex.alexchat.adapter.baseadapter.SingleChatAdapter;
import com.alex.alexchat.bean.MessageBean;
import com.alex.alexchat.bean.MessageType;
import com.alex.alexchat.config.App;
import com.alex.alexchat.config.Cache;
import com.alex.alexchat.config.Url;
import com.alex.alexchat.fragment.ContactFragment;
import com.alex.alexchat.fragment.ConversationFragment;
import com.alex.alexchat.fragment.SettingFragment;
import com.jakewharton.disklrucache.DiskLruCache;
import com.lidroid.xutils.util.LogUtils;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import github.common.helper.MediaHelper;
import github.common.helper.MediaHelper.MediaStatus;
import github.common.helper.MediaHelper.OnRecorderListener;
import github.common.listener.CommonClickListener;
import github.common.listener.clickcore.CommonClickType;
import github.common.rongim.RongUtil;
import github.common.utils.DiskUtil;
import github.common.utils.TimeUtils;
import github.common.utils.ToastUtil;
import github.common.utils.ViewUtil;
import github.common.view.SwipeTextView;
import github.common.view.SwipeTextView.OnSwipeListener;
import github.common.view.SwipeTextView.SwipeType;
import github.jianghejie.xrecyclerview.ProgressStyle;
import github.jianghejie.xrecyclerview.XRecyclerView;
import github.nereo.MultiImageSelectorActivity;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectCallback;
import io.rong.imlib.RongIMClient.ConnectionStatusListener;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.OnReceiveMessageListener;
import io.rong.imlib.RongIMClient.OnReceivePushMessageListener;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.RongIMClient.SendImageMessageCallback;
import io.rong.imlib.RongIMClient.SendMessageCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.notification.PushNotificationMessage;

/**
 * <pre>
 * 与XXX聊天界面
 * 启动者：
 *      聊天室：{@link ChatRoomActivity}
 *      会话列表：{@link ConversationFragment}
 *      联系人群组：{@link ContactFragment}
 *      设置页面：{@link SettingFragment}
 * 启动：
 *      定位：{@link LocationActivity}
 * 适配器：{@link SingleChatAdapter}
 * </pre>
 */
public class SingleChatActivity extends BaseActivity {
    protected SingleChatAdapter adapter;
    /**
     * 获取图
     */
    public static final int requestCodePhoto = 100;
    /**
     * 获取位
     */
    public static final int requestCodeLocation = 101;
    private String chatType;
    private String targetId;
    private String targetName;
    private String targetPortraitUri;
    private String userId;
    private String userName;
    private String userToken;
    private String userPortraitUri;
    private boolean mustConnectRongIMServer;
    private PopupWindow voicePopWindow;
    private RongIMClient rongIMClient;
    private View voicePopView;
    private AnimationDrawable rightVoiceAnim;
    private AnimationDrawable leftVoiceAnim;
    private ConversationType conversationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlechat);
        Intent intent = getIntent();
        chatType = intent.getStringExtra(App.keyChatType);
        targetId = intent.getStringExtra(App.keyTargetId);
        targetName = intent.getStringExtra(App.keyTargetName);
        targetPortraitUri = intent.getStringExtra(App.keyTargetPortraitUri);
        userId = intent.getStringExtra(App.keyRyUserId);
        userName = intent.getStringExtra(App.keyRyUserName);
        userToken = intent.getStringExtra(App.keyRyUserToken);
        userPortraitUri = intent.getStringExtra(App.keyRyPortraitUri);
        mustConnectRongIMServer = intent.getBooleanExtra(App.keyMustConnectRongIMServer, false);
        adapter = new SingleChatAdapter(context);
        if (App.chatTypeSingle.equalsIgnoreCase(chatType)) {
            conversationType = ConversationType.PRIVATE;
            adapter.setCanDisplaySenderUserName(false);
        } else if (App.chatTypeGroup.equalsIgnoreCase(chatType)) {
            conversationType = ConversationType.GROUP;
            adapter.setCanDisplaySenderUserName(true);
        }
        initVoicePopWindow();
        initView();
        initRongIMSDK();
    }

    public void initView() {
        ViewUtil.setText2TextView(findViewById(R.id.tv), targetName);
        ((EditText) findViewById(R.id.et_message)).addTextChangedListener(new MyTextWatcher());
        ((SwipeTextView) findViewById(R.id.stv_press_for_voice)).setOnSwipeListener(new MyOnSwipeListener());
        findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
        findViewById(R.id.iv_chat_more).setOnClickListener(new MyOnClickListener());
        findViewById(R.id.iv_chat_voice).setOnClickListener(new MyOnClickListener());
        findViewById(R.id.iv_chat_keypad).setOnClickListener(new MyOnClickListener());
        findViewById(R.id.tv_add_image).setOnClickListener(new MyOnClickListener());
        findViewById(R.id.tv_add_location).setOnClickListener(new MyOnClickListener());
        findViewById(R.id.tv_send).setOnClickListener(new MyOnClickListener());
        findViewById(R.id.stv_press_for_voice).setOnClickListener(new MyOnClickListener());
        XRecyclerView xRecyclerView = (XRecyclerView) findViewById(R.id.xrv);
        xRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        xRecyclerView.setItemAnimator(new DefaultItemAnimator());
        xRecyclerView.setLoadingListener(new MyLoadingListener());
        xRecyclerView.setPullRefreshEnabled(true);
        xRecyclerView.setLoadingMoreEnabled(false);
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallPulse);
        xRecyclerView.setAdapter(adapter);
    }

    private final class MyLoadingListener implements XRecyclerView.LoadingListener {
        @Override
        public void onRefresh() {
            new LoadTask().execute();
        }

        @Override
        public void onLoadMore() {
            new LoadTask().execute();
        }
    }

    private final class LoadTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(2000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            XRecyclerView xRecyclerView = (XRecyclerView) findViewById(R.id.xrv);
            xRecyclerView.refreshComplete();
            xRecyclerView.loadMoreComplete();
        }
    }

    public final void deleteMessages(int messageID) {
        rongIMClient.deleteMessages(new int[]{messageID}, new DeleteMessageResultCallback());
    }

    private final class DeleteMessageResultCallback extends ResultCallback<Boolean> {
        @Override
        public void onError(ErrorCode errorCode) {
            LogUtils.e("errorCode = " + errorCode.getMessage());
        }

        @Override
        public void onSuccess(Boolean result) {
            LogUtils.e("result = " + result);
            adapter.dismissDialog(true);
        }
    }

    private final class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            XRecyclerView xRecyclerView = (XRecyclerView) findViewById(R.id.xrv);
            //xRecyclerView.scrollToPosition(adapter.getList().size());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (TextUtils.isEmpty(text)) {
                findViewById(R.id.tv_send).setVisibility(View.GONE);
                findViewById(R.id.iv_chat_more).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.tv_send).setVisibility(View.VISIBLE);
                findViewById(R.id.iv_chat_more).setVisibility(View.GONE);
            }
        }
    }

    private final class MyOnReceivePushMessageListener implements OnReceivePushMessageListener {
        @Override
        public boolean onReceivePushMessage(PushNotificationMessage message) {
            LogUtils.e("收到 onReceivePushMessage");
            return false;
        }

    }

    /**
     * @category 监听收到消息
     */
    private final class MyOnReceiveMessageListener implements OnReceiveMessageListener {
        /**
         * @param message 收到的消息实体。
         * @param left    剩余未拉取消息数目。
         * @return 是否已经处理消息
         */
        @Override
        public boolean onReceived(Message message, int left) {
            LogUtils.e("消息监听器  收到的消息实体");
            handleReceivedMessage(message);
            return false;
        }
    }

    /**
     * @category 点击说话or向上滑动取消
     */
    private final class MyOnSwipeListener implements OnSwipeListener {
        private MediaHelper mediaRecorderHelper;
        private String voicePath;

        @Override
        public void onStart(SwipeTextView swipeTextView) {
            swipeTextView.setText("松开后发送");
            int[] location = new int[2];
            View view = findViewById(R.id.line_divide);
            view.getLocationOnScreen(location);
            FrameLayout container = new FrameLayout(view.getContext());
            container.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int xoffset = view.getWidth() / 2 - container.getMeasuredWidth() / 2;
            voicePopWindow.showAsDropDown(view, xoffset, 0);
            voicePopView.findViewById(R.id.iv_chat_voice_status_left).setVisibility(View.VISIBLE);
            voicePopView.findViewById(R.id.iv_chat_voice_status_right).setVisibility(View.VISIBLE);
            rightVoiceAnim.start();
            leftVoiceAnim.start();
            //
            String timeMillis = TimeUtils.getCurrentTimeInLongString();
            DiskLruCache diskLruCache = DiskUtil.open(context, Cache.voiceDiskCacheDir);
            voicePath = DiskUtil.getPath(diskLruCache, timeMillis);
            mediaRecorderHelper = new MediaHelper(voicePath, new MyOnRecorderListener());
            mediaRecorderHelper.startRecorder();
        }

        @Override
        public void onStop(SwipeTextView swipeTextView, SwipeType swipeType) {
            swipeTextView.setText("按住说话  ");
            mediaRecorderHelper.stopRecorder();
            voicePopWindow.dismiss();
            rightVoiceAnim.stop();
            leftVoiceAnim.stop();
            if (SwipeType.isAboved == swipeType) {
                //KLog.e("取消发送");
            } else {
                MessageBean bean = new MessageBean(TimeUtils.getCurrentTimeInLongString());
                bean.duration = mediaRecorderHelper.getDuration();
                bean.voiceUri = Uri.fromFile(mediaRecorderHelper.getVoiceFile());
                bean.isFinished = false;
                bean.length = mediaRecorderHelper.getVoiceFile().length();
                if (bean.duration <= 0) {
                    ToastUtil.shortAtCenter(context, "时间太短");
                    return;
                }
                bean.messageType = MessageType.voiceRight;
                sendMessageContent(bean);
            }
        }

        @Override
        public void onSwipe(SwipeTextView swipeTextView, SwipeType swipeType) {
            View cancel = voicePopView.findViewById(R.id.iv_chat_voice_status_cancel);
            View speak = voicePopView.findViewById(R.id.iv_chat_voice_status_speak);
            TextView tip = (TextView) voicePopView.findViewById(R.id.tv_chat_voice_status_tip);
            if (SwipeType.isAboved == swipeType) {
                voicePopView.findViewById(R.id.iv_chat_voice_status_left).setVisibility(View.GONE);
                voicePopView.findViewById(R.id.iv_chat_voice_status_right).setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                speak.setVisibility(View.GONE);
                tip.setText("松开手指，取消发送");
            } else if (SwipeType.isNotAboved == swipeType) {
                voicePopView.findViewById(R.id.iv_chat_voice_status_left).setVisibility(View.VISIBLE);
                voicePopView.findViewById(R.id.iv_chat_voice_status_right).setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                speak.setVisibility(View.VISIBLE);
                tip.setText("手指向上滑动，取消发送");
            }
        }
    }

    private final class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (R.id.tv_send == v.getId()) {
                String text = ((EditText) findViewById(R.id.et_message)).getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    MessageBean bean = new MessageBean(TimeUtils.getCurrentTimeInLongString());
                    bean.text = text;
                    bean.messageType = MessageType.textRight;
                    sendMessageContent(bean);
                }
            } else if (R.id.iv_chat_more == v.getId()) {
                View viewMore = findViewById(R.id.layout_chat_more);
                if (View.VISIBLE == viewMore.getVisibility()) {
                    viewMore.setVisibility(View.GONE);
                } else {
                    viewMore.setVisibility(View.VISIBLE);
                    ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(findViewById(R.id.et_message).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            } else if (R.id.tv_add_image == v.getId()) {
                Intent intent = new Intent(context, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                startActivityForResult(intent, requestCodePhoto);
            } else if (R.id.tv_add_location == v.getId()) {
                Intent intent = new Intent(context, LocationActivity.class);
                startActivityForResult(intent, requestCodeLocation);
            } else if (R.id.iv_chat_voice == v.getId()) {
                ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(findViewById(R.id.et_message).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                findViewById(R.id.et_message).setVisibility(View.GONE);
                findViewById(R.id.iv_chat_voice).setVisibility(View.GONE);
                findViewById(R.id.layout_chat_more).setVisibility(View.GONE);
                findViewById(R.id.stv_press_for_voice).setVisibility(View.VISIBLE);
                findViewById(R.id.iv_chat_keypad).setVisibility(View.VISIBLE);
            } else if (R.id.stv_press_for_voice == v.getId()) {
                ToastUtil.shortAtCenter(context, "点我");
            } else if (R.id.iv_chat_keypad == v.getId()) {
                ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(findViewById(R.id.et_message).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                findViewById(R.id.et_message).setVisibility(View.VISIBLE);
                findViewById(R.id.iv_chat_voice).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_chat_more).setVisibility(View.GONE);
                findViewById(R.id.stv_press_for_voice).setVisibility(View.GONE);
                findViewById(R.id.iv_chat_keypad).setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || data.getExtras() == null) {
            return;
        }
        if (requestCode == requestCodePhoto) {
            /*获取 图片*/
            ArrayList<String> listImagePath = data.getExtras().getStringArrayList(MultiImageSelectorActivity.EXTRA_RESULT);
            if (listImagePath == null || listImagePath.size() <= 0) {
                return;
            }
            MessageBean bean = new MessageBean(TimeUtils.getCurrentTimeInLongString());
            bean.imageUri = Uri.parse("file:///" + listImagePath.get(0));
            Bitmap oldBitmap = BitmapFactory.decodeFile(bean.imageUri.getPath());
            bean.extra = ((oldBitmap.getHeight() * 0.9) > oldBitmap.getWidth()) + "";
            bean.messageType = MessageType.imageRight;
            sendMessageContent(bean);
        } else if (requestCode == requestCodeLocation) {
            /*获取 定位后的 图片*/
            DiskLruCache diskLruCache = DiskUtil.open(context, Cache.imageDiskCacheDir);
            String imageKey = data.getStringExtra("imageKey");
            byte[] bitmapBtyes = DiskUtil.getByte(diskLruCache, data.getStringExtra("imageKey"));
            if (bitmapBtyes == null) {
                return;
            }
            MessageBean bean = new MessageBean(TimeUtils.getCurrentTimeInLongString());
			/*位置信息*/
            bean.latitude = data.getDoubleExtra("latitude", 0);
            bean.longitude = data.getDoubleExtra("longitude", 0);
            bean.address = data.getStringExtra("address");
            bean.imageUri = Uri.parse("file:///" + DiskUtil.getPath(diskLruCache, imageKey));
            bean.isFinished = false;
            bean.messageType = MessageType.locationRight;
            sendMessageContent(bean);
        }
    }

    /**
     * @category 发送实体消息
     * @see SingleChatActivity.MyOnClickListener
     */
    public void sendMessageContent(MessageBean bean) {
        //LogUtils.e("conversationType = "+conversationType+" targetID = "+targetId);
        String pushContent = "pushContent";
        String pushData = "pushData";
        bean.isFinished = false;
        //LogUtils.e("userId = "+userId+" userName = "+userName+" targetPortraitUri = "+targetPortraitUri);
        UserInfo userInfo = new UserInfo(userId, userName, Uri.parse(userPortraitUri));
        bean.senderPortraitUri = userPortraitUri;
        bean.senderUserId = userId;
        bean.senderUserName = userName;
        if (MessageType.textRight == bean.messageType) {
            TextMessage textMessage = TextMessage.obtain(bean.text);
            textMessage.setUserInfo(userInfo);
            ((EditText) findViewById(R.id.et_message)).setText("");
            rongIMClient.sendMessage(conversationType, targetId, textMessage, pushContent, pushData, new MySendMessageCallback(bean), new SendMessageResultCallback());
        } else if (MessageType.imageRight == bean.messageType) {
            ImageMessage imageMessage = new ImageMessage();
            imageMessage.setLocalUri(bean.imageUri);
            imageMessage.setExtra(bean.extra);
            imageMessage.setUserInfo(userInfo);
            rongIMClient.sendImageMessage(conversationType, targetId, imageMessage, pushContent, pushData, new MySendImageMessageCallback(bean));
        } else if (MessageType.locationRight == bean.messageType) {
            String poi = "";
            LocationMessage locationMessage = LocationMessage.obtain(bean.latitude, bean.longitude, poi, bean.imageUri);
            locationMessage.setExtra(bean.address);
            locationMessage.setUserInfo(userInfo);
            rongIMClient.sendMessage(conversationType, targetId, locationMessage, pushContent, pushData, new MySendMessageCallback(bean), new SendMessageResultCallback());
        } else if (MessageType.voiceRight == bean.messageType) {
            VoiceMessage voiceMessage = VoiceMessage.obtain(bean.voiceUri, bean.duration);
            voiceMessage.setExtra(bean.length + "");
            voiceMessage.setUserInfo(userInfo);
            rongIMClient.sendMessage(conversationType, targetId, voiceMessage, pushContent, pushData, new MySendMessageCallback(bean), new SendMessageResultCallback());
        }
        adapter.addItem(bean);
        XRecyclerView xRecyclerView = (XRecyclerView) findViewById(R.id.xrv);
        xRecyclerView.scrollToPosition(adapter.getList().size());
    }

    private final class ReadMessageResultCallback extends ResultCallback<Boolean> {

        @Override
        public void onSuccess(Boolean aBoolean) {

        }

        @Override
        public void onError(ErrorCode errorCode) {

        }
    }

    private final class SendMessageResultCallback extends ResultCallback<Message> {
        @Override
        public void onError(ErrorCode errorCode) {
            LogUtils.e("errorCode = " + errorCode.getMessage());
        }

        @Override
        public void onCallback(Message message) {
            LogUtils.e("messageId = " + message.getMessageId());
        }

        @Override
        public void onFail(ErrorCode errorCode) {
            LogUtils.e("errorCode = " + errorCode.getMessage());
        }

        @Override
        public void onFail(int errorCode) {
            LogUtils.e("errorCode = " + errorCode);
        }

        @Override
        public void onSuccess(Message message) {
            LogUtils.e("onsuccess = " + message.getMessageId());
        }
    }

    /**
     * @category 发送文本消息的 回调
     */
    private final class MySendMessageCallback extends SendMessageCallback {
        private MessageBean messageBean;

        public MySendMessageCallback(MessageBean bean) {
            this.messageBean = bean;
        }

        @Override
        public void onCallback(Integer messageId) {
            super.onCallback(messageId);
            KLog.e("onCallback   ....    messageId = " + messageId);
        }

        @Override
        public void onError(Integer messageId, ErrorCode code) {
            KLog.e("onError         ....    code = " + code.getMessage());
        }

        @Override
        public void onSuccess(Integer messageId) {
            KLog.e("onSuccess      ....    messageId = " + messageId);
            messageBean.isFinished = true;
            messageBean.messageId = messageId;
            adapter.updateItemByMillisTime(messageBean);
        }
    }

    /**
     * 发送图片消息 的 回调
     */
    private final class MySendImageMessageCallback extends SendImageMessageCallback {
        private MessageBean messageBean;

        public MySendImageMessageCallback(MessageBean chatBean) {
            this.messageBean = chatBean;
        }

        @Override
        public void onAttached(Message message) {
        }

        @Override
        public void onError(Message message, ErrorCode code) {
        }

        @Override
        public void onProgress(Message message, int progress) {
            //LogUtils.e("图片上传 progress = "+progress);
        }

        @Override
        public void onSuccess(Message message) {
            messageBean.messageId = message.getMessageId();
            messageBean.isFinished = true;
            adapter.updateItemByMillisTime(messageBean);
        }
    }

    /**
     * @category 更新适配器   该方法  可运行在  子线程
     */
    private final class AdapterAddItemRunnable implements Runnable {
        private MessageBean bean;

        public AdapterAddItemRunnable(MessageBean bean) {
            this.bean = bean;
        }

        @Override
        public void run() {
            adapter.addItem(bean);
            XRecyclerView xRecyclerView = (XRecyclerView) findViewById(R.id.xrv);
            xRecyclerView.scrollToPosition(adapter.getList().size());
        }
    }

    /**
     * 初始化 融云即时通信SDK
     *
     * @see #onCreate(Bundle)
     */
    private void initRongIMSDK() {
        dialogLoading.show();
        rongIMClient = RongIMClient.getInstance();
        rongIMClient.clearMessagesUnreadStatus(conversationType, targetId, new MyResultCallback());
        if (mustConnectRongIMServer) {
            KLog.e("强制登录融云服务器");
            rongIMClient = RongIMClient.connect(userToken, new MyConnectCallback());
        }
        rongIMClient.getHistoryMessages(conversationType, targetId, -1, Url.rows, new HistoryResultCallback());
        RongIMClient.setConnectionStatusListener(new MyConnectionStatusListener());
        RongIMClient.setOnReceiveMessageListener(new MyOnReceiveMessageListener());
        RongIMClient.setOnReceivePushMessageListener(new MyOnReceivePushMessageListener());
    }

    private final class MyResultCallback extends ResultCallback<Boolean> {

        @Override
        public void onSuccess(Boolean result) {
            //消息  已读
            //KLog.e("result = "+result);
        }

        @Override
        public void onError(ErrorCode errorCode) {
            KLog.e("errorCode = " + RongUtil.errorCode2String(errorCode));
        }
    }

    /**
     * @category 融云账号登录成功后 回调
     */
    private final class MyConnectCallback extends ConnectCallback {
        @Override
        public void onCallback(String userId) {
            KLog.e("result = " + userId);
        }

        @Override
        public void onFail(ErrorCode errorCode) {
            KLog.e("errorCode = " + errorCode);
        }

        @Override
        public void onFail(int errorCode) {
            KLog.e("errorCode = " + errorCode);
        }

        @Override
        public void onTokenIncorrect() {
            KLog.e("onTokenIncorrect ");
        }

        @Override
        public void onError(ErrorCode errorCode) {
            KLog.e("onError : " + errorCode.getMessage());
        }

        @Override
        public void onSuccess(String userId) {
            KLog.e("userId = " + userId);
        }
    }

    /**
     * @category 历史消息列表
     */
    private final class HistoryResultCallback extends ResultCallback<List<Message>> {
        @Override
        public void onError(ErrorCode errorCode) {
            KLog.e("errorCode = " + errorCode.getMessage());
            dialogLoading.dismiss();
        }

        @Override
        public void onSuccess(List<Message> listMessage) {
            dialogLoading.dismiss();
            if ((listMessage != null) && (listMessage.size() > 0)) {
                KLog.e("历史消息数量 =  " + listMessage.size());
                runOnUiThread(new HistoryMessageRunnable(listMessage));
            }
        }
    }

    private final class HistoryMessageRunnable implements Runnable {
        private List<Message> listMessage;

        public HistoryMessageRunnable(List<Message> listMessage) {
            this.listMessage = listMessage;
        }

        @Override
        public void run() {
            for (int i = 0; i < listMessage.size(); i++) {
                SystemClock.sleep(20);
                handleReceivedMessage(listMessage.get(i));
            }
        }
    }

    /**
     * @category 处理收到的消息
     */
    private void handleReceivedMessage(Message message) {
        message.setReceivedTime(TimeUtils.getCurrentTimeInLong());
        MessageBean messageBean = new MessageBean(TimeUtils.getCurrentTimeInLongString());
        messageBean.messageId = message.getMessageId();
        messageBean.senderUserId = message.getSenderUserId();
        messageBean.targetId = message.getTargetId();
        messageBean.isFinished = true;
        messageBean.senderPortraitUri = targetPortraitUri;
        //Message.ReceivedStatus receivedStatus  = new Message.ReceivedStatus();
        //rongIMClient.setMessageReceivedStatus(messageBean.messageId,receivedStatus,new ReadMessageResultCallback());
        KLog.e("消息发送者 " + messageBean.senderUserId + " 目标 id = " + targetId + " 用户id = " + userId);
        if ((!messageBean.senderUserId.equalsIgnoreCase(targetId)) && (!userId.equalsIgnoreCase(messageBean.targetId))) {
            return;
        }
        @SuppressWarnings("unused") int describeContents = message.describeContents();
        MessageContent messageContent = message.getContent();
		/*收到好友验证消息*/
        if (messageContent instanceof ContactNotificationMessage) {
            ContactNotificationMessage contactNotificationMessage = (ContactNotificationMessage) messageContent;
            LogUtils.e("messageContent.getSourceUserId = " + contactNotificationMessage.getSourceUserId());
            LogUtils.e("messageContent.getTargetUserId = " + contactNotificationMessage.getTargetUserId());
            LogUtils.e("messageContent.getMessage = " + contactNotificationMessage.getMessage());
            LogUtils.e("messageContent.getOperation = " + contactNotificationMessage.getOperation());
        } else if (messageContent instanceof TextMessage) {
			/*收到文本消息*/
            TextMessage textMessage = (TextMessage) messageContent;
            messageBean.messageType = MessageType.textLeft;
            messageBean.text = textMessage.getContent();
            messageBean = handleSenderUserInfo(messageBean, textMessage.getUserInfo());
            runOnUiThread(new AdapterAddItemRunnable(messageBean));
        } else if (messageContent instanceof ImageMessage) {
			/*收到图片消息*/
            ImageMessage imageMessage = (ImageMessage) messageContent;
            messageBean.imageUri = imageMessage.getRemoteUri();
            messageBean.messageType = MessageType.imageLeft;
            messageBean.extra = imageMessage.getExtra();
            messageBean = handleSenderUserInfo(messageBean, imageMessage.getUserInfo());
            runOnUiThread(new AdapterAddItemRunnable(messageBean));
        } else if (messageContent instanceof LocationMessage) {
            LocationMessage locationMessage = (LocationMessage) messageContent;
			/*位置信息*/
            messageBean.latitude = locationMessage.getLat();
            messageBean.longitude = locationMessage.getLng();
            messageBean.address = locationMessage.getExtra();
            messageBean.imageUri = locationMessage.getImgUri();
            messageBean.messageType = MessageType.locationLeft;
            messageBean = handleSenderUserInfo(messageBean, locationMessage.getUserInfo());
            runOnUiThread(new AdapterAddItemRunnable(messageBean));
        } else if (messageContent instanceof VoiceMessage) {
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
			/*语音信息*/
            messageBean.messageType = MessageType.voiceLeft;
            messageBean.duration = voiceMessage.getDuration();
            messageBean.voiceUri = voiceMessage.getUri();
            messageBean.length = Long.parseLong(voiceMessage.getExtra());
            messageBean = handleSenderUserInfo(messageBean, voiceMessage.getUserInfo());
            runOnUiThread(new AdapterAddItemRunnable(messageBean));
        }
    }

    /**
     * @category 解析出消息的发送方信息
     **/
    private MessageBean handleSenderUserInfo(MessageBean bean, UserInfo userInfo) {
		/*当前接收方  选择发送方  和发送方聊天  必须每次都要解析出发送方的 用户信息；
		 * 因为在跟 发送方聊天的过程中  发送方可能随时出去 修改头像*/
        if (userInfo != null) {
            bean.senderPortraitUri = userInfo.getPortraitUri().toString();
            bean.senderUserId = userInfo.getUserId();
            bean.senderUserName = userInfo.getName();
            //LogUtils.e("senderUserId = "+bean.senderUserId+" senderUserName = "+bean.senderUserName+" senderPortraitUri = "+bean.senderPortraitUri);
        } else {
            LogUtils.e("userInfo 空 = " + (userInfo == null));
        }
        return bean;
    }

    private final class MyConnectionStatusListener implements ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus status) {
            if (status == ConnectionStatus.CONNECTED) {
                KLog.e("连接成功");
            } else if (status == ConnectionStatus.CONNECTING) {
                //连接中。
            } else if (status == ConnectionStatus.DISCONNECTED) {
                KLog.e("断开连接");
            } else if (status == ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                KLog.e("用户账户在其他设备登录，本机被踢掉线");
            } else if (status == ConnectionStatus.NETWORK_UNAVAILABLE) {
                KLog.e("网络不可用");
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void initVoicePopWindow() {
        voicePopView = LayoutInflater.from(context).inflate(R.layout.pop_window_voice, null);
        rightVoiceAnim = (AnimationDrawable) ((ImageView) voicePopView.findViewById(R.id.iv_chat_voice_status_right)).getDrawable();
        leftVoiceAnim = (AnimationDrawable) ((ImageView) voicePopView.findViewById(R.id.iv_chat_voice_status_left)).getDrawable();
        voicePopWindow = new PopupWindow(voicePopView, android.view.ViewGroup.LayoutParams.MATCH_PARENT, ViewUtil.getViewHeight(voicePopView));
        voicePopWindow.setContentView(voicePopView);
        voicePopWindow.setFocusable(true);
        voicePopWindow.setBackgroundDrawable(new BitmapDrawable());
        voicePopWindow.setOutsideTouchable(true);
    }

    private final class MyOnRecorderListener implements OnRecorderListener {
        @Override
        public void onRecorder(MediaStatus mediaStatus, int duration, String message) {
            LogUtils.e("mediaStatus = " + mediaStatus + " duration = " + duration + " message = " + message);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        rightVoiceAnim.stop();
        leftVoiceAnim.stop();
        voicePopWindow.dismiss();
        ((TextView) findViewById(R.id.stv_press_for_voice)).setText("按住说话");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mustConnectRongIMServer) {
            KLog.e("断开连接  退出登录");
            RongIMClient.getInstance().disconnect();
            RongIMClient.getInstance().logout();
        }
    }
}
