package com.example.newapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.Constant;
import com.example.newapp.MyApplication;
import com.example.newapp.R;
import com.example.newapp.netUtils.NetBroadcastReceiver;
import com.example.newapp.netUtils.NetUtils;
import com.example.newapp.view.CustomDialog;
import com.example.newapp.view.CustomDialog.ButtonRespond;

 
public class BaseActivity extends Activity implements OnClickListener,NetBroadcastReceiver.NetEvevt{
	public ImageView ivReback;
	public TextView tvTitle;
	public static NetBroadcastReceiver.NetEvevt evevt;
	private int netMobile;
	private int netMobile2 = -1000;
	private String netType=null;
	private CustomDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MyApplication.addActivity2Stack(this);
		evevt = this;
	    inspectNet();// 第一次查看网络
	    
	}

    public void inspectNet() {
        this.netMobile = NetUtils.getNetWorkState(BaseActivity.this); //这是第一次的，总之都要根据netMobile去判断的
        if(netMobile==Constant.NO_NETWORK)
        	showNetworkChange(netMobile);
    }
    
    
	@Override
	public void onNetChange(int netMobile) {
		// TODO Auto-generated method stub
		if(netMobile2!=netMobile) {
			netMobile2=netMobile;
			showNetworkChange(netMobile);
		}
			
	}
	

	public void showNetworkChange(int netMobile){
		switch(netMobile){
		case NetUtils.NETWORK_WIFI:
			netType="已连接，当前为wifi网络";
			break;
		case  NetUtils.NETWORK_MOBILE:
			netType="已连接，当前为移动网络";
			break;
		case NetUtils.NETWORK_NONE:
			netType="当前无网络连接";
			break;
		default:
			netType="当前无网络连接";
		}
		/**
		 * 
		 
		AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle("提示");
        builder.setMessage(netType);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        
        */
		
        dialog = new CustomDialog(BaseActivity.this, new ButtonRespond() {

			@Override
			public void buttonRightRespond() {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}

			@Override
			public void buttonLeftRespond() {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.setDialogTitle(R.string.warning);
		dialog.setDialogMassage(netType);
		dialog.setLeftButtonText(R.string.cancel);
		dialog.setRightButtonText(R.string.confirm);
		// dialog.setLeftButonBackgroud(R.drawable.b);
		
		dialog.show();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.removeActivityFromStack(this);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.iv_reback) {
			doReback();
		}
	}

 
	public void setTitle(Activity activity, String titleStr) {
		tvTitle = (TextView) activity.findViewById(R.id.tv_title);
		setBoldText(tvTitle);
		tvTitle.setText(titleStr);
	}

 
	public void setTitle(Activity activity, int titleResouceId) {
		tvTitle = (TextView) activity.findViewById(R.id.tv_title);
		setBoldText(tvTitle);
		tvTitle.setText(titleResouceId);
	}
 
	public void initivReabck(Activity activity) {
		ivReback = (ImageView) activity.findViewById(R.id.iv_reback);
		ivReback.setOnClickListener((OnClickListener) activity);
	}
 
	public void doReback() {
		finish(); 
	}

	 
	public static void setBoldText(TextView tv) {
		TextPaint tp2 = tv.getPaint();
		tp2.setFakeBoldText(true);
	}

	public static void showResulttoast(int result, Context context) {
		MyApplication.showResultToast(result, context);
	}
	public static SharedPreferences getOrSharedPrefences(Context context){
		return context.getSharedPreferences(Constant.ORPREFERENCES, Context.MODE_PRIVATE);
		
	}
 
 
}
