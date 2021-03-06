package com.example.newapp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

 
public class MyCrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CrashHandler";
	private static MyCrashHandler myCrashHandler;

	private MyCrashHandler() {
	};

	private Context context;

	public synchronized static MyCrashHandler getInstance() {
		if (myCrashHandler == null) {
			myCrashHandler = new MyCrashHandler();
			return myCrashHandler;
		} else {
			return myCrashHandler;
		}
	}

	public void init(Context context) {
		this.context = context;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			StringBuffer sb = new StringBuffer();
			
			Writer writer = new StringWriter();
			PrintWriter printwriter = new PrintWriter(writer);

			ex.printStackTrace(printwriter);
			printwriter.flush();
			printwriter.close();

			sb.append(writer.toString());
		
			Log.e(TAG, sb.toString());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, "异常退出", 1).show();
				Looper.loop();

			}

		}.start();

		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				android.os.Process.killProcess(android.os.Process.myPid());
			}

		}.start();

	}

}
