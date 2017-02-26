package com.example.newapp.activity;
  
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.newapp.MyApplication;
import com.example.newapp.R;
import com.example.newapp.view.CustomDialog;
import com.example.newapp.view.CustomDialog.ButtonRespond;
public class MainActivity extends BaseActivity {
	private CustomDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	
	@Override
	public void onBackPressed() {
		exitAPP();
	}

	/**
	 * 退出程序
	 */
	public void exitAPP() {
		dialog = new CustomDialog(MainActivity.this, new ButtonRespond() {

			@Override
			public void buttonRightRespond() {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}

			@Override
			public void buttonLeftRespond() {
				// TODO Auto-generated method stub
				dialog.dismiss();
				MyApplication.instance.onTerminate();
			}
		});
		dialog.setDialogTitle(R.string.exit_app);
		dialog.setDialogMassage(R.string.live_for_time);
		dialog.setLeftButtonText(R.string.no);
		dialog.setRightButtonText(R.string.yes);
		// dialog.setLeftButonBackgroud(R.drawable.b);
		dialog.show();
	}

	

	
}
