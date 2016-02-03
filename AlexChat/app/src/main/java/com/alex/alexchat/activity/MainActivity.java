package com.alex.alexchat.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.chatroom.ChatRoomActivity;
import com.alex.alexchat.adapter.baseadapter.main.UserListAdapter;
import com.alex.alexchat.bean.UserBean;
import com.alex.alexchat.config.App;
import com.alex.alexchat.config.Cache;
import com.alex.alexchat.config.Url;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import github.common.rongim.RongUser;
import github.common.utils.JsonUtil;
import github.common.utils.ToastUtil;
import github.common.utils.XUtil;
import github.jianghejie.xrecyclerview.ProgressStyle;
import github.jianghejie.xrecyclerview.XRecyclerView;

/**
 * 启动项：
 *      聊天室：{@link com.alex.alexchat.activity.chatroom.ChatRoomActivity}
 * 适配器：{@link UserListAdapter}
 */
public class MainActivity extends BaseActivity {
    private UserListAdapter adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        String userId = RongUser.getValue(context, RongUser.userId);
        if(TextUtils.isEmpty(userId)){
            httpUtils.send(HttpMethod.POST, Url.userListApi, new MyRequestCallBack());
        }else{
            Intent intent = new Intent(context, ChatRoomActivity.class);
            intent.putExtra(App.keyRyUserId, userId);
            intent.putExtra(App.keyRyUserName, RongUser.getValue(context,RongUser.userName));
            intent.putExtra(App.keyRyUserToken, RongUser.getValue(context,RongUser.userToken));
            intent.putExtra(App.keyRyPortraitUri, RongUser.getValue(context,RongUser.portraitUri));
            context.startActivity(intent);
            finish();
        }
    }
    public void initView() {
        XRecyclerView xRecyclerView = (XRecyclerView) findViewById(R.id.xrv);
        xRecyclerView.setHasFixedSize(true);
        xRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        xRecyclerView.setItemAnimator(new DefaultItemAnimator());
        xRecyclerView.setLoadingListener(new MyLoadingListener());
        xRecyclerView.setPullRefreshEnabled(true);
        xRecyclerView.setLoadingMoreEnabled(true);
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.Pacman);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallPulse);
        adapter = new UserListAdapter(context);
        xRecyclerView.setAdapter(adapter);
    }

    private final class MyLoadingListener implements XRecyclerView.LoadingListener {
        @Override
        public void onRefresh() {
            httpUtils.send(HttpMethod.POST, Url.userListApi, new MyRequestCallBack());
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

    /**
     * xUtils.HttpUtils的回调
     */
    private final class MyRequestCallBack extends RequestCallBack<String> {
        @Override
        public void onStart() {
            super.onStart();
            dialogLoading.show();
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            LogUtils.e("onFailure = " + XUtil.getErrorInfo(error));
            ToastUtil.longAtCenterInThread(context, XUtil.getErrorInfo(error));
            XRecyclerView xRecyclerView = (XRecyclerView) findViewById(R.id.xrv);
            xRecyclerView.refreshComplete();
            xRecyclerView.loadMoreComplete();
            dialogLoading.dismiss();
        }

        @Override
        public void onSuccess(ResponseInfo<String> info) {
            dialogLoading.dismiss();
            String result = info.result;
            Cache.saveUserListJson(result);
            JSONArray jsonArray_userList = JsonUtil.getJsonArray(result, "userList");
            List<UserBean> listUserBean = new ArrayList<UserBean>();
            for (int i = 0; (!JsonUtil.isArrayEmpty(jsonArray_userList)) && (i < jsonArray_userList.length()); i++) {
                JSONObject jsonObject = JsonUtil.getJsonObject(jsonArray_userList, i);
                UserBean bean = new UserBean();
                bean.name = JsonUtil.getString(jsonObject, "name");
                bean.portraitUri = JsonUtil.getString(jsonObject, "portraitUri");
                bean.token = JsonUtil.getString(jsonObject, "token");
                bean.id = JsonUtil.getString(jsonObject, "userId");
                listUserBean.add(bean);
            }
            adapter.addItem(listUserBean);
            XRecyclerView xRecyclerView = (XRecyclerView) findViewById(R.id.xrv);
            xRecyclerView.refreshComplete();
            xRecyclerView.loadMoreComplete();
        }
    }
}
