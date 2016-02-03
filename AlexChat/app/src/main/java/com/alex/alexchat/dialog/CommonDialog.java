package com.alex.alexchat.dialog;

import github.common.dialog.BaseDialog;
import github.common.dialog.StatusBarUtils;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.alex.alexchat.R;

public class CommonDialog extends BaseDialog
{
	private OnDialogClickListener onDialogClickListener;
	public CommonDialog(Context context) {
		super(context,R.style.dialog_common);
		this.context = context;
		initBaseDialogTheme();
		initDialog();
	}
	public CommonDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		initBaseDialogTheme();
		initDialog();
	}
	private final class MyOnClickListener implements android.view.View.OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.bt_left == v.getId()){
				if(onDialogClickListener!=null){
					onDialogClickListener.onDialogClick(Gravity.LEFT);
				}else{
					dismiss();
				}
			}
			else if(R.id.bt_right == v.getId()){
				if(onDialogClickListener!=null){
					onDialogClickListener.onDialogClick(Gravity.RIGHT);
				}else{
					dismiss();
				}
			}
		}
	}
	public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener){
		this.onDialogClickListener = onDialogClickListener;
	}
	public interface OnDialogClickListener{
		/**Gravity.LEFT  |  Gravity.RIGHT*/
		public void onDialogClick(int gravity);
	}
	@Override
	public void initDialog()
	{
		dm = getContext().getResources().getDisplayMetrics();
		maxHeight = dm.heightPixels - StatusBarUtils.getHeight(context);
		rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_common, null);
		rootView.findViewById(R.id.bt_left).setOnClickListener(new MyOnClickListener());
		rootView.findViewById(R.id.bt_right).setOnClickListener(new MyOnClickListener());
		setContentView(rootView);
		setCancelable(true);
		setCanceledOnTouchOutside(canDismissTouchOut);
	}
}
