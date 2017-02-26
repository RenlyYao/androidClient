package com.example.newapp;

import java.util.ArrayList;
import java.util.logging.Level;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextPaint;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.entity.User;
import com.example.newapp.view.CustomDialog;
import com.example.newapp.view.CustomDialog.ButtonRespond;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
public class MyApplication extends Application {

	public static MyApplication instance;//  
	private static ArrayList<Activity> activitystack;//  
	public static User loginUser =null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		instance = this;
		activitystack = new ArrayList<Activity>();
		MyCrashHandler handler = MyCrashHandler.getInstance();
		handler.init(getApplicationContext());
		Thread.setDefaultUncaughtExceptionHandler(handler);
		OkGo.init(this);
		try {
	        OkGo.getInstance()
	        	   .debug("OkGo", Level.INFO, true)
	              .setCookieStore(new MemoryCookieStore());            //cookie使用内存缓存（app退出后，cookie消失）
//	                .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		super.onCreate();
	}

	public static MyApplication getInstance() {
		return instance;
	}
	
	@Override
	public void onTerminate() {
		if(activitystack!=null){
			for (Activity activity : activitystack) {
				activity.finish();
			}
		}
		
		activitystack.clear();
		super.onTerminate();
	}

 
	public static void addActivity2Stack(Activity activity) {
		if(activity!=null)
			instance.activitystack.add(activity);
	}

 
	public static void removeActivityFromStack(Activity activity) {
		if(activity!=null)
			instance.activitystack.remove(activity);
	}

 
	public static void setBoldText(TextView tv) {
		TextPaint tp2 = tv.getPaint();
		tp2.setFakeBoldText(true);
	}
	
	
	 
	public static SharedPreferences getOrSharedPrefences(Context context) {
		return context.getSharedPreferences(Constant.ORPREFERENCES, Context.MODE_PRIVATE);

	}
	
	public static void showResultToast(int result, Context context) {
		switch (result) {
		case Constant.NO_RESPONSE:
			Toast.makeText(context, R.string.no_response, 0).show();
			break;
		case Constant.S_EXCEPTION:
			Toast.makeText(context, R.string.server_exception, 0).show();
			break;
		case Constant.RESPONESE_EXCEPTION:
			Toast.makeText(context, R.string.responese_exception, 0).show();
			break;
		case Constant.TIMEOUT:
			Toast.makeText(context, R.string.timeout, 0).show();
			break;
		case Constant.NO_NETWORK:
			Toast.makeText(context, R.string.no_network, 0).show();
			break;
		case Constant.NULLPARAMEXCEPTION:
			Toast.makeText(context, R.string.nullparamexception, 0).show();
			break;
		case Constant.SERVER_EXCEPTION:
			Toast.makeText(context, R.string.server_exception, 0).show();
			break;
		case Constant.RELOGIN:
			CustomDialog dialog = new CustomDialog(context, new ButtonRespond() {

				@Override
				public void buttonRightRespond() {
					Activity activity = activitystack.get(0);
					activitystack.remove(0);// 把登录界面提出来
					MyApplication.instance.onTerminate();
					activitystack.add(activity);// 重新放到栈中
				}

				@Override
				public void buttonLeftRespond() {
					// TODO Auto-generated method stub
					MyApplication.instance.onTerminate();
				}
			});
			dialog.setDialogTitle(R.string.relogin);
			dialog.setDialogMassage(R.string.relogin_message);
			dialog.setLeftButtonText(R.string.exit_app);
			dialog.setRightButtonText(R.string.relogin);
			dialog.setCancelable(false);
			dialog.show();
			break;
		case 4005:
			Toast.makeText(context, "缺少参数", 0).show();
			break;
		case 4006:
			Toast.makeText(context, "参数值不能为空", 0).show();
			break;
		default:
			Toast.makeText(context, "请求响应失败，错误号" + result, 0).show();
			break;
		}
	}
}
