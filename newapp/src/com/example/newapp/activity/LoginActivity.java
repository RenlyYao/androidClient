package com.example.newapp.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.Constant;
import com.example.newapp.MyApplication;
import com.example.newapp.R;
import com.example.newapp.callback.StringDialogCallback;
import com.example.newapp.entity.User;
import com.example.newapp.utils.AESUtil;
import com.example.newapp.utils.MatchUtil;
import com.example.newapp.view.CustomDialog;
import com.example.newapp.view.CustomDialog.ButtonRespond;
import com.lzy.okgo.OkGo;

public class LoginActivity extends BaseActivity{
	private TextView tvPhoneRegisted;// 号码已经注册提示
	private EditText etAccount;// 帐号
	private EditText etPassword;
	private CheckBox cbSavedpw;
	private Button btRegister;// 注册按钮
	private Button btLogin;// 登录按钮
	private User user = new User();
	String userFormat;
	private String username = null;
	String name = null;
	private CustomDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		SharedPreferences sp = getOrSharedPrefences(this); 	//获取sharepreference
		//自动登录是要在这里去弄的
		boolean isChecked = sp.getBoolean(Constant.ISSAVEPW, false); //
		cbSavedpw.setChecked(isChecked);
		if (isChecked) {
			username = sp.getString(Constant.PHONENUM, null);
//			userFormat = String.format(getString(R.string.userformat), username.subSequence(0, 1), username.substring(username.length() - 1));
			etAccount.setText(username);
			String pw = sp.getString(Constant.PASSWORD, null);
			if (pw != null) {
				try {
					etPassword.setText(AESUtil.hexDecrypt(pw, Constant.ENCODEPASSWORD));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		etAccount = (EditText) this.findViewById(R.id.et_account);
		etPassword = (EditText) this.findViewById(R.id.et_password);
		cbSavedpw = (CheckBox) this.findViewById(R.id.cb_savedpw);
		TextView tvTitle = (TextView) this.findViewById(R.id.tv_labletext1);
		setBoldText(tvTitle);
		this.btLogin = (Button) findViewById(R.id.bt_login);
		btLogin.setOnClickListener(this);
		// 设置中文字体加粗
		setBoldText(btLogin);
		btRegister = (Button) findViewById(R.id.bt_register);
		// 设置中文字体加粗
		setBoldText(btRegister);
		btRegister.setOnClickListener(this);
		tvPhoneRegisted = (TextView) this.findViewById(R.id.tv_phoneRegister);
	}

	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.bt_register:
			startActivityForResult(new Intent(this, RegisterActivity.class), 100);
			break;
		case R.id.bt_login:
			doLongin();
			break;

		default:
			break;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {//make sure it comes from register.class
			if (resultCode == 3001) {
				String registerAccount = data.getStringExtra("Account");
				if (registerAccount != null && registerAccount.length() > 1) {
					//注册成功的提示
					String text = String.format(getString(R.string.phone_registerNotifide), registerAccount.charAt(0), registerAccount.charAt(registerAccount.length() - 1));
					tvPhoneRegisted.setText(text);
					tvPhoneRegisted.setVisibility(View.VISIBLE);
				}
			} else {
				tvPhoneRegisted.setVisibility(View.GONE); //不成功的话就隐藏

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	private void doLongin() {
		// TODO Auto-generated method stub
		name = etAccount.getText().toString().trim();
//		if (name.equals(userFormat)) {
//			name = username;
//		}
		if (!MatchUtil.isPhoneNum(name)) {
			if ("".equals(name)) {
				Toast.makeText(this, R.string.null_account, 0).show();
			} else if (name.length() < 3) {
				Toast.makeText(this, R.string.short_account, 0).show();
			} else {
				Toast.makeText(this, R.string.error_account, 0).show();
			}
			return;
		}
		// 验证密码
		final String loginPasWord = etPassword.getText().toString().trim();
		if (!MatchUtil.isLicitPassword(loginPasWord)) {
			if ("".equals(loginPasWord)) {
				Toast.makeText(this, R.string.null_password, 0).show();
			} else if (loginPasWord.length() < 6) {
				Toast.makeText(this, R.string.short_password, 0).show();
			} else {
				Toast.makeText(this, R.string.error_password, 0).show();
			}
			return;
		}
		SharedPreferences sp = getOrSharedPrefences(this); 	
		String token =sp.getString("token", null);
		OkGo.post(Constant.LOGIN)
		.params("phoneNum",name)
		.params("password",loginPasWord)
		.tag(this)
		.headers("token", token)
		.execute(new StringDialogCallback(this) {
			@Override
			public void onSuccess(String data, Call call, Response response) {
				// TODO Auto-generated method stub
				if(response.code()==200){
					try {
						JSONObject result=new JSONObject(data);
						int code=Integer.parseInt(result.getString("code"));
						String msg=result.getString("msg");
						if(code==0){
							Toast.makeText(getApplicationContext(), msg, 0).show();
						}
						//success
						else{
							user.setId(result.getString("userId"));
							user.setPhoneNum(result.getString("phoneNum"));
							user.setNickname(result.getString("nickname"));
							String token=result.getString("token");
							MyApplication.loginUser = user;
							SharedPreferences sp = LoginActivity.getOrSharedPrefences(LoginActivity.this);
							Editor editor = sp.edit();
							if(token!=null){
								editor.putString("token", token);
							}
							if (cbSavedpw.isChecked()) {//如果是记住了我就保存到sharepreference
								editor.putBoolean(Constant.ISSAVEPW, true);
								editor.putString(Constant.PHONENUM, name);
								editor.putString(Constant.PASSWORD, AESUtil.hexEncrypt(loginPasWord, Constant.ENCODEPASSWORD));
								editor.commit();
								editor.clear();
							}
							//跳转到主页面
							
							Toast.makeText(getApplicationContext(), LoginActivity.this.getString(R.string.login_success), 0).show();
							Intent intent = new Intent(LoginActivity.this, MainActivity.class);// 传递user对象
							startActivity(intent);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Toast.makeText(getApplicationContext(), "数据解析失败", 0).show();
						e.printStackTrace();
					}
					
				}
				else{
					Toast.makeText(getApplicationContext(), LoginActivity.this.getString(R.string.login_fail), 0).show();
				}
			}
			
		});
		
	}


	@Override
	public void onBackPressed() {
		exitAPP();
	}

	/**
	 * 退出程序
	 */
	public void exitAPP() {
		dialog = new CustomDialog(LoginActivity.this, new ButtonRespond() {

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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		OkGo.getInstance().cancelTag(this);
	}





}