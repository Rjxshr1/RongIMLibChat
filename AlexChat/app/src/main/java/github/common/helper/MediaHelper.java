package github.common.helper;
import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.lidroid.xutils.util.LogUtils;
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class MediaHelper
{
	private static String TAG = "MediaRecorderHelper";
	private MediaRecorder mediaRecorder;  
	private MediaPlayer mediaPlayer;
	private OnRecorderListener onRecorderStatusListener;
	private OnPlayerListener onPlayerListener;
	private MediaThread mediaThread;
	/**单位 0.1秒*/
	private int duration;
	private boolean canLoop;
	private String filePath;
	/**@param filePath 保存音频的全路径*/
	public MediaHelper(String filePath,OnRecorderListener onRecorderStatusListener) {
		this.onRecorderStatusListener = onRecorderStatusListener;
		this.filePath = filePath;
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setOnErrorListener(new MediaRecorderOnErrorListener());
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);  
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mediaRecorder.setOutputFile(file.getAbsolutePath());
		LogUtils.e("file.getAbsolutePath = "+file.getAbsolutePath());
		mediaThread = new MediaThread();
		duration = 0;
		canLoop = true;
	}
	/**@param fileDescriptor 文件描述符
	 * @param length 文件长度
	 * @param uri 
	 * */
	public MediaHelper(Context context,Uri uri, OnPlayerListener onPlayerListener){
		this.onPlayerListener = onPlayerListener;
		mediaPlayer = MediaPlayer.create(context, uri);
		try
		{
			mediaPlayer.setLooping(false);
			mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
			mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
			mediaPlayer.setOnErrorListener(new MediaPlayerErrorListener());
		} catch (Exception e){
			Log.e(TAG, "有异常："+e);
		}
	}
	public void startPlay(){
		mediaPlayer.start();
	}
	public void stopPlayer(){
		mediaPlayer.stop();
		mediaPlayer.release();
	}
	public void startRecorder()
	{
		try
		{
			mediaRecorder.prepare();
		} catch (Exception e){
			if(onRecorderStatusListener!=null){
				onRecorderStatusListener.onRecorder(MediaStatus.prepareError,duration, "准备中出错");
			}
			Log.e(TAG, "有异常: "+e);
		}
		mediaRecorder.start();
		mediaThread.start();
		if(onRecorderStatusListener!=null){
			onRecorderStatusListener.onRecorder(MediaStatus.recordering,duration, "录音中");
		}
	}
	public void stopRecorder(){
		if(mediaRecorder==null){
			return ;
		}
		try
		{
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		} catch (Exception e){
			if(onRecorderStatusListener!=null){
				onRecorderStatusListener.onRecorder(MediaStatus.stopError,duration, "停止出错");
			}
		}
		canLoop = false;
		mediaThread = null;
	}
	private final class MediaRecorderOnErrorListener implements OnErrorListener
	{
		@Override
		public void onError(MediaRecorder mr, int what, int extra)
		{
			if(mr!=null){
				try
				{
					mr.stop();
					mr.release();
					mr = null;
				} catch (Exception e){
					if(onRecorderStatusListener!=null){
						onRecorderStatusListener.onRecorder(MediaStatus.recorderError, duration, "录音中出错");
					}
				}
			}
			if(onRecorderStatusListener!=null){
				onRecorderStatusListener.onRecorder(MediaStatus.recorderError, duration, "录音中出错");
			}
		}
	}
	/**工作线程  每 0.1秒 迭代一次*/
	private final class MediaThread extends Thread
	{
		@Override
		public void run()
		{
			while(canLoop){
				duration++;
				SystemClock.sleep(100);
			}
		}
	}
	/**单位秒*/
	public int getDuration(){
		return duration/10;
	}
	public File getVoiceFile(){
		File voiceFile = new File(filePath);
		return voiceFile;
	}
	private final class MyOnPreparedListener implements OnPreparedListener
	{
		@Override
		public void onPrepared(MediaPlayer mp)
		{
			if(onPlayerListener!=null){
				onPlayerListener.onPlayer(MediaStatus.prepared, 0, "准备完成");
			}
		}
	}
	private final class MyOnCompletionListener implements OnCompletionListener
	{
		@Override
		public void onCompletion(MediaPlayer mp)
		{
			if(onPlayerListener!=null){
				onPlayerListener.onPlayer(MediaStatus.playComplete, 0, "播放完成");
			}
		}
	}
	private final class MediaPlayerErrorListener implements MediaPlayer.OnErrorListener
	{
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra)
		{
			if(onPlayerListener!=null){
				onPlayerListener.onPlayer(MediaStatus.palyError, extra, "播放中出错");
			}
			return false;
		}
		
	}
	public interface OnRecorderListener{
		public void onRecorder(MediaStatus mediaStatus,int duration, String message);
	}
	public interface OnPlayerListener{
		public void onPlayer(MediaStatus mediaStatus,int code, String message);
	}
	public enum MediaStatus{
		/**准备过程中， 出错了*/
		prepareError,
		/**录音中*/
		recordering,
		/**录音中 出错*/
		recorderError,
		/**停止出错*/
		stopError,
		/**播放中 出错*/
		palyError,
		/**播放完成*/
		playComplete,
		/**准备完成*/
		prepared
	}
}
