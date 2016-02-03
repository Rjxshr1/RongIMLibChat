package com.alex.alexchat.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.MainActivity;
import com.alex.alexchat.activity.chatroom.ChatRoomActivity;
import com.alex.alexchat.activity.chatroom.SingleChatActivity;
import com.alex.alexchat.adapter.baseadapter.ConversationAdapter;
import com.alex.alexchat.config.App;
import com.alex.alexchat.config.Cache;
import com.socks.library.KLog;

import java.util.List;

import github.common.rongim.RongHelper;
import github.common.rongim.RongService;
import github.common.rongim.RongUser;
import github.common.utils.ToastUtil;
import github.jianghejie.xrecyclerview.ProgressStyle;
import github.jianghejie.xrecyclerview.XRecyclerView;
import github.pnikosis.ProgressWheel;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;

@SuppressLint("InflateParams")
/**会话列表
 * 启动者：
 * 	聊天：{@link ChatRoomActivity}
 * 启动项：
 * 	开始聊天：{@link SingleChatActivity}
 * 适配器：
 *    {@link ConversationAdapter}
 * */

public class ConversationFragment extends BaseFragment {
    private RongIMClient rongIMClient;
    private ConversationAdapter adapter;
    /**
     * 融云 会话  迭代器服务  的 意图
     */
    protected Intent rongServiceIntent;
    /**
     * 融云 会话 迭代器的 广播接收者
     */
    private BroadcastReceiver rongReceiver;
    private String userId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        rongIMClient = RongIMClient.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_conversation, null);
            initView();
            refreshConversation();
            setupConversationReceiver();
        }
        return rootView;
    }

    /**
     * 刷新 融云账号信息
     */
    private void refreshConversation() {
        if (App.loadTypeRefresh.equalsIgnoreCase(loadType)) {
            showDialog(loadingDialog, context);
        }
        userId = RongUser.getValue(context, RongUser.userId);
        String userToken = RongUser.getValue(context, RongUser.userToken);
        String userName = RongUser.getValue(context, RongUser.userName);
        String portraitUri = RongUser.getValue(context, RongUser.portraitUri);
        new RongHelper(activity, userId, userToken).doLogin(userName, portraitUri, new MyRongHelperListener());
    }

    @Override
    public void initView() {
        XRecyclerView xRecyclerView = (XRecyclerView) rootView.findViewById(R.id.xrv);
        xRecyclerView.setHasFixedSize(true);
        xRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        xRecyclerView.setItemAnimator(new DefaultItemAnimator());
        xRecyclerView.setLoadingListener(new MyLoadingListener());
        xRecyclerView.setPullRefreshEnabled(true);
        xRecyclerView.setLoadingMoreEnabled(true);
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallPulse);
        adapter = new ConversationAdapter(context);
        xRecyclerView.setAdapter(adapter);
    }

    private final class MyLoadingListener implements XRecyclerView.LoadingListener {

        @Override
        public void onRefresh() {
            loadType = App.loadTypeRefresh;
            refreshConversation();
        }

        @Override
        public void onLoadMore() {
            new LoadTask().execute();
        }
    }

    private final class LoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(loadingDialog, getActivity());
        }

        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(2000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissDialog(loadingDialog);
            if (App.loadTypeRefresh.equalsIgnoreCase(loadType)) {

            }
            XRecyclerView xRecyclerView = (XRecyclerView) rootView.findViewById(R.id.xrv);
            xRecyclerView.refreshComplete();
            xRecyclerView.loadMoreComplete();
        }
    }

    /**
     * @category 融云账号连接的 回调
     */
    private final class MyRongHelperListener implements RongHelper.RongHelperListener {
        @Override
        public void onStart() {
            KLog.e("之前不在线 开始登陆");
        }

        @Override
        public void onSuccess(RongHelper.HelperStatus helperStatus, String userId) {
            if(RongHelper.HelperStatus.connectionSuccess == helperStatus){
                KLog.e("登录成功 userId = " + userId);
            }else if(RongHelper.HelperStatus.syncSuccess == helperStatus){
                KLog.e("同步用户信息成功 userId = " + userId);
            }
            stopProgressWheel();
            handleConnection(userId);
        }

        @Override
        public void onFail(RongHelper.HelperStatus helperStatus, String errorMsg) {
            KLog.e("登录失败 errorMsg = " + errorMsg);
            ToastUtil.shortAtCenter(context, errorMsg);
            if(RongHelper.HelperStatus.tokenIsNull == helperStatus){
                startActivity(new Intent(context, MainActivity.class));
                getActivity().finish();
            }
            stopProgressWheel();
        }

        @Override
        public void onLoading() {

        }


    }

    /**
     * 处理登录成功后的事件
     */
    private void handleConnection(String userId) {
        if (TextUtils.isEmpty(userId) || ("-1".equalsIgnoreCase(userId)) || (rongIMClient == null)) {
            return;
        }
        ConversationType conversationTypes[] = {ConversationType.CHATROOM, ConversationType.DISCUSSION, ConversationType.GROUP, ConversationType.PRIVATE, ConversationType.SYSTEM};
        rongIMClient.getUnreadCount(new UnReadResultCallback(), conversationTypes);
        RongUser.setValue(context, RongUser.userId, userId);
        List<Conversation> listConversation = rongIMClient.getConversationList();
        if (adapter != null) {
            adapter.refreshItem(listConversation);
        }
    }
    private void stopProgressWheel() {
        if (rootView == null) {
            KLog.e("rootView null = " + (rootView == null));
            return;
        }
        ProgressWheel progressWheel = (ProgressWheel) rootView.findViewById(R.id.pw);
        if (progressWheel == null) {
            KLog.e("progressWheel  null = " + (progressWheel == null));
            return;
        }
        XRecyclerView xRecyclerView = (XRecyclerView) rootView.findViewById(R.id.xrv);
        xRecyclerView.refreshComplete();
        xRecyclerView.loadMoreComplete();
        dismissDialog(loadingDialog);
        progressWheel.stopSpinning();
        progressWheel.setVisibility(View.GONE);
    }

    private final class UnReadResultCallback extends ResultCallback<Integer> {
        @Override
        public void onError(ErrorCode errorCode) {
            KLog.e("errorCode = " + errorCode.getMessage());
        }

        @Override
        public void onSuccess(Integer count) {
            KLog.e("未读消息数量 = " + count);
            Cache.saveConversationTmp("getActivity 空 = "+(getActivity()==null)+" count 空 = "+(count==null));
            ((ChatRoomActivity) getActivity()).setLabelText(0, count + "");
        }
    }

    /**
     * 创建 融云会话的 广播接收者
     */
    private void setupConversationReceiver() {
        rongReceiver = new ConversationReceiver();
        IntentFilter filter = new IntentFilter(ConversationReceiver.class.getName());
        getActivity().registerReceiver(rongReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rongServiceIntent == null) {
            rongServiceIntent = new Intent(context, RongService.class);
            rongServiceIntent.putExtra(App.keyIsLoopConversation, true);
        }
        //KLog.e("start 一个 服务");
        getActivity().startService(rongServiceIntent);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().stopService(rongServiceIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(rongServiceIntent);
        getActivity().unregisterReceiver(rongReceiver);
        KLog.e("断开连接  退出登录");
        RongIMClient.getInstance().disconnect();
        RongIMClient.getInstance().logout();
    }

    public final class ConversationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleConnection(userId);
        }
    }

}
