package com.alex.alexchat.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.chatroom.ChatRoomActivity;
import com.alex.alexchat.activity.chatroom.SingleChatActivity;
import com.alex.alexchat.adapter.baseadapter.ContactAdapter;
import com.alex.alexchat.adapter.baseadapter.HotCityAdapter;
import com.alex.alexchat.bean.UserBean;
import com.alex.alexchat.config.App;
import com.alex.alexchat.config.Cache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import github.common.rongim.RongUser;
import github.common.utils.JsonUtil;
import github.common.utils.ToastUtil;
import github.leerduo.PinyinComparator;
import github.leerduo.SideBar;
import github.leerduo.SortAdapter;
import github.leerduo.SortModel;
import github.pinyin4j.Pinyin4jUtil;

/**
 * <pre>
 * 通讯录界面
 *      启动者：{@link ChatRoomActivity}
 *  启动：
 *      聊天室：{@link SingleChatActivity}
 *      适配器：{@link ContactAdapter}
 * </pre>
 */
public class ContactFragment extends BaseFragment {
    protected View rootView;
    private List<SortModel> listSortModel;
    private SortAdapter adapter;
    private PinyinComparator pinyinComparator;
    private ListView listView;
    private HotCityAdapter hotCityAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        pinyinComparator = new PinyinComparator();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_contact, null);
            initView();
            loadJsonData();
        }
        return rootView;
    }

    @Override
    public void initView() {
        listView = (ListView) rootView.findViewById(R.id.lv);
        View headView = LayoutInflater.from(context).inflate(R.layout.headview_city, null, false);
        GridView gridView = (GridView) headView.findViewById(R.id.hgv);
        gridView.setOnItemClickListener(new MyOnItemClickListener());
        hotCityAdapter = new HotCityAdapter(context);
        gridView.setAdapter(hotCityAdapter);
        String result = Cache.getUserListJson();
        JSONArray jsonArray_userList = JsonUtil.getJsonArray(result, "groupList");
        List<UserBean> listUserBean = new ArrayList<UserBean>();
        for (int i = 0; (!JsonUtil.isArrayEmpty(jsonArray_userList)) && (i<jsonArray_userList.length()); i++)
        {
            JSONObject jsonObject = JsonUtil.getJsonObject(jsonArray_userList, i);
            UserBean bean = new UserBean();
            bean.id = JsonUtil.getString(jsonObject, "groupId");
            bean.name = JsonUtil.getString(jsonObject, "groupName");
            listUserBean.add(bean);
        }
        hotCityAdapter.refreshItem(listUserBean);
        listView.addHeaderView(headView);
        SideBar sideBar = (SideBar) rootView.findViewById(R.id.sb);
        adapter = new SortAdapter(context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MyOnItemClickListener());
        sideBar.setTextView((TextView) rootView.findViewById(R.id.tv_selected));
        sideBar.setOnTouchingLetterChangedListener(new MyOnTouchingLetterChangedListener());
    }

    private final class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context, SingleChatActivity.class);
            if(R.id.lv == parent.getId()){
                SortModel bean = adapter.getList().get(position - 1);
                intent.putExtra(App.keyChatType, App.chatTypeSingle);
                intent.putExtra(App.keyTargetId, bean.id);
                intent.putExtra(App.keyTargetName, bean.name);
                intent.putExtra(App.keyTargetPortraitUri, bean.portraitUri);
                intent.putExtra(App.keyMustConnectRongIMServer, false);
            }else if(R.id.hgv == parent.getId()){
                UserBean bean = hotCityAdapter.getList().get(position);
                intent.putExtra(App.keyChatType, App.chatTypeGroup);
                intent.putExtra(App.keyTargetId, bean.id);
                intent.putExtra(App.keyTargetName, bean.name);
            }
            intent.putExtra(App.keyRyUserId, RongUser.getValue(context, RongUser.userId));
            intent.putExtra(App.keyRyUserName, RongUser.getValue(context, RongUser.userName));
            intent.putExtra(App.keyRyUserToken, RongUser.getValue(context, RongUser.userToken));
            intent.putExtra(App.keyRyPortraitUri, RongUser.getValue(context, RongUser.portraitUri));
            context.startActivity(intent);
        }
    }

    private final class MyOnTouchingLetterChangedListener implements SideBar.OnTouchingLetterChangedListener {
        @Override
        public void onTouchingLetterChanged(String s) {
            //该字母首次出现的位置
            int position = adapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                listView.setSelection(position);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void loadJsonData() {
        listSortModel = new ArrayList<SortModel>();
        String result = Cache.getUserListJson();
        JSONArray jsonArray_userList = JsonUtil.getJsonArray(result, "userList");
        List<UserBean> listUserBean = new ArrayList<UserBean>();
        for (int i = 0; (!JsonUtil.isArrayEmpty(jsonArray_userList)) && (i<jsonArray_userList.length()); i++)
        {
            JSONObject jsonObject = JsonUtil.getJsonObject(jsonArray_userList, i);
            SortModel sortModel = new SortModel();
            sortModel.name = JsonUtil.getString(jsonObject, "name");
            sortModel.portraitUri = JsonUtil.getString(jsonObject, "portraitUri");
            sortModel.token = JsonUtil.getString(jsonObject, "token");
            sortModel.id = JsonUtil.getString(jsonObject, "userId");
            String letter = Pinyin4jUtil.converterToSpell(sortModel.name).substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (letter.matches("[A-Z]")) {
                sortModel.letter = letter.toUpperCase();
            } else {
                sortModel.letter = "#";
            }
            listSortModel.add(sortModel);
        }
        Collections.sort(listSortModel, pinyinComparator);
        adapter.addItem(listSortModel);
    }

}
