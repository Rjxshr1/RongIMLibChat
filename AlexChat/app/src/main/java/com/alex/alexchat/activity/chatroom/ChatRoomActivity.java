package com.alex.alexchat.activity.chatroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.BaseFragmentActivity;
import com.alex.alexchat.activity.MainActivity;
import com.alex.alexchat.activity.StartSelfActivity;
import com.alex.alexchat.adapter.fragmentpageradapter.TitlePagerAdapter;
import com.alex.alexchat.config.App;
import com.alex.alexchat.config.Cache;
import com.alex.alexchat.fragment.ContactFragment;
import com.alex.alexchat.fragment.ConversationFragment;
import com.alex.alexchat.fragment.SettingFragment;

import java.util.ArrayList;
import java.util.List;

import github.common.rongim.RongUser;
import github.common.utils.ViewUtil;
import github.hellojp.TabsIndicator;

/**
 * <pre>
 * 聊天功能
 * 启动者：
 *     程序入口： {@link MainActivity}
 * 启动项：
 *      会话列表：{@link ConversationFragment}
 *      联系人群组：{@link ContactFragment}
 *      设置页面：{@link SettingFragment}
 *      聊天页面：{@link SingleChatActivity}
 *      重启配置：{@link StartSelfActivity}
 * </pre>
 */
public class ChatRoomActivity extends BaseFragmentActivity {
    private List<TextView> listTvLabel;
    private ViewPager viewPager;
    private TabsIndicator tabsIndicator;
    private TitlePagerAdapter titlePagerAdapter;
    private List<String> listTitle;
    private List<Fragment> listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_chatroom);
        initRyUserData();
        listTvLabel = new ArrayList<TextView>();
        initView();
        if (savedInstanceState == null) {
            initFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((titlePagerAdapter == null) || (listFragment == null) || (listTitle == null)) {
            Intent intent = new Intent(context, StartSelfActivity.class);
            Intent getIntent = getIntent();
            intent.putExtra(App.keyRyUserName, getIntent.getStringExtra(App.keyRyUserName));
            intent.putExtra(App.keyRyUserToken, getIntent.getStringExtra(App.keyRyUserToken));
            intent.putExtra(App.keyRyUserId, getIntent.getStringExtra(App.keyRyUserId));
            intent.putExtra(App.keyTargetPortraitUri, getIntent.getStringExtra(App.keyTargetPortraitUri));
            intent.putExtra(App.keyModuleName, ChatRoomActivity.class.getSimpleName());
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void initView() {
        listTvLabel.add((TextView) findViewById(R.id.tv));
        listTvLabel.add((TextView) findViewById(R.id.tv_2));
        listTvLabel.add((TextView) findViewById(R.id.tv_3));
        viewPager = (ViewPager) findViewById(R.id.vp);
        tabsIndicator = (TabsIndicator) findViewById(R.id.ti);
    }

    private void initFragment() {
        listFragment = new ArrayList<Fragment>();
        String strAry[] = new String[]{"消息", "联系人", "设置"};
        listTitle = new ArrayList<String>();
        listFragment.add(new ConversationFragment());
        listFragment.add(new ContactFragment());
        listFragment.add(new SettingFragment());
        for (int i = 0; i < strAry.length; i++) {
            listTitle.add(strAry[i]);
        }
        titlePagerAdapter = new TitlePagerAdapter(getSupportFragmentManager(), listFragment, listTitle);
        viewPager.setAdapter(titlePagerAdapter);
        tabsIndicator.setViewPager(0, viewPager);
        tabsIndicator.setAnimationWithTabChange(true);
        tabsIndicator.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    protected class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    /**
     * 给顶部 lable 设置内容
     *
     * @param position 从左到右 第几个
     * @param text     要显示的内容
     */
    public void setLabelText(int position, String text) {
        StringBuilder builder = new StringBuilder();
        builder.append("listTvLabel 空 = " + (listTvLabel == null)+" position = "+position);
        if(listTvLabel!=null){
            builder.append("\nlistTvLabel.size() = "+listTvLabel.size());
        }
        if((listTvLabel.size() > position)){
            builder.append("\nlistTvLabel.size() > position");
        }
        if((listTvLabel.get(position) != null)){
            builder.append("\nlistTvLabel.get(position) 空 = "+(listTvLabel.get(position) == null));
        }
        Cache.saveChatRoomLabelBadgeInfo(builder.toString());

        if ((listTvLabel == null) || (listTvLabel.size() <= position) || (listTvLabel.get(position) == null)) {
            return;
        }
        TextView textView = listTvLabel.get(position);
        if ((TextUtils.isEmpty(text)) || ("0".equals(text))) {
            textView.setVisibility(View.GONE);
        } else {
            ViewUtil.setText2TextView(textView, text);
            textView.setVisibility(View.VISIBLE);
        }

    }

    private void initRyUserData() {
        Intent intent = getIntent();
        String userId = intent.getStringExtra(App.keyRyUserId);
        String userName = intent.getStringExtra(App.keyRyUserName);
        String userToken = intent.getStringExtra(App.keyRyUserToken);
        String portraitUri = intent.getStringExtra(App.keyRyPortraitUri);
        RongUser.setValue(context, RongUser.userId, userId);
        RongUser.setValue(context, RongUser.userName, userName);
        RongUser.setValue(context, RongUser.userToken, userToken);
        RongUser.setValue(context, RongUser.portraitUri, portraitUri);
    }
}
