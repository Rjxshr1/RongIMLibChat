package github.common.rongim;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.alex.alexchat.config.App;
import com.alex.alexchat.fragment.ConversationFragment;
import com.socks.library.KLog;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by AlexCheung on 2016/1/29.
 */
public class RongService extends Service {
    protected boolean isLoop = true;
    protected UpdateTaskThread updateTaskThread;
    private int lastCount;
    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        KLog.e("onBind");
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lastCount = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        boolean isLoopConversation = intent.getBooleanExtra(App.keyIsLoopConversation, true);
        KLog.e("isLoopConversation = "+isLoopConversation);
        try {
            if(updateTaskThread == null){
                KLog.e("创建，线程对象");
                updateTaskThread = new UpdateTaskThread();
            }
            if(!updateTaskThread.isAlive() || updateTaskThread.isInterrupted()){
                KLog.e("子线程死了，即将被救活");
                updateTaskThread.start();
            }
            if (Build.VERSION.SDK_INT < 18) {
                startForeground(1120, new Notification());
            }else{
                startForeground(1120,null);
            }
            return START_REDELIVER_INTENT;
        } catch (Exception e) {
        }
        return super.onStartCommand(intent, flags, startId);
    }
    public class MyBinder extends Binder
    {
        public RongService getService(){
            return RongService.this;
        }
    }
    protected final class UpdateTaskThread extends Thread
    {
        protected static final String TAG = "UpdateTaskThread";
        @Override
        public void run()
        {
            RongIMClient rongIMClient = RongIMClient.getInstance();
            isLoop = true;
            while(isLoop)
            {
                Conversation.ConversationType conversationTypes[] = {Conversation.ConversationType.CHATROOM, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.GROUP, Conversation.ConversationType.PRIVATE, Conversation.ConversationType.SYSTEM};
                rongIMClient.getUnreadCount(new UnReadResultCallback(), conversationTypes);
                SystemClock.sleep(1000);
            }
        }
    }
    private final class UnReadResultCallback extends RongIMClient.ResultCallback<Integer> {
        @Override
        public void onError(RongIMClient.ErrorCode errorCode) {
            KLog.e("errorCode = " + errorCode.getMessage());
        }
        @Override
        public void onSuccess(Integer count) {
            if(lastCount==count){
                return ;
            }
            lastCount = count;
            KLog.e("未读消息数量 = " + count);
            Intent intent = new Intent(ConversationFragment.ConversationReceiver.class.getName());
            intent.putExtra("count",count.intValue());
            sendBroadcast(intent);
        }
    }
    @Override
    public void onDestroy()
    {
        isLoop = false;
    }
}
