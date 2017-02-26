package com.example.newapp.activity;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.example.newapp.Constant;
import com.example.newapp.MyApplication;
import com.example.newapp.R;
import com.example.newapp.entity.User;
import com.example.newapp.utils.AESUtil;
import com.example.newapp.utils.MatchUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

public class RegisterActivity extends BaseActivity implements OnClickListener {
	private LinearLayout llStepNavigate;// 页面第一第二步的导航条
	private TextView tvStep1;
	private TextView tvStep2;
	private ViewFlipper vfRegister;// 页面切换的容器
	private int viewIndex = 0;// 初始化当前页排位
	// step1界面元素
	private EditText etPhoneNum;// 电话号码输入框
	private String phone;
	private Button btGetCode;// 获取验证码按钮;
	private EditText etCheckCode;// 验证码输入框
	private Button btNextStep;// 下一步按钮

	// step2的界面元素
	private EditText etUserName;// 电话或用户名
	private EditText etPassWord;// 登录密码
	private EditText etTransatonPassWord;// 交易密码
	private Button btRegissterLogin;// 注册并登录
	private ProgressDialog dialog;
	private Runnable timerRunnable;// 定时器
	private static final int TIMECHANGE = 200;
	// 定时结束
	private static final int TIMERCOMPLETE = 300;
	
	private static final int CODE_ING = 1;   //已发送，倒计时
	 private static final int CODE_REPEAT = 2;  //重新发送
	 private static final int SMSDDK_HANDLER = 3;  //短信回调
	 private int validTime = 60;//倒计时60s
	 private EventHandler eventHandler;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_register);
		setTitle(this, R.string.register);
		initView();
		initSDK();
	}

	public void initView() {
		llStepNavigate = (LinearLayout) this.findViewById(R.id.ll_step_navigate);
		tvStep1 = (TextView) this.findViewById(R.id.tv_step1);
		tvStep2 = (TextView) this.findViewById(R.id.tv_step2);
		ivReback=(ImageView) findViewById(R.id.iv_reback);
		ivReback.setOnClickListener(this);
		vfRegister = (ViewFlipper) this.findViewById(R.id.vfl_register);

		// step1
		etPhoneNum = (EditText) this.findViewById(R.id.et_phonenum);  //手机账号
		btGetCode = (Button) this.findViewById(R.id.bt_get_checkCode);  //获取验证码
		btGetCode.setOnClickListener(this);

		etCheckCode = (EditText) this.findViewById(R.id.et_checkcode);
		
		etCheckCode.addTextChangedListener(new captChaTextWatcher());//验证码
		
		btNextStep = (Button) this.findViewById(R.id.bt_next_step);
		btNextStep.setOnClickListener(this);
		btNextStep.setClickable(false);  //静止掉，但是这里先打开以下吧
		setButtonBackgroud(btNextStep);//对那个下一步做背景的改变和切换

		// step2
		etUserName = (EditText) this.findViewById(R.id.et_usernmae); //用户名和密码的填写
		etPassWord = (EditText) this.findViewById(R.id.et_login_paw);
		
		btRegissterLogin = (Button) this.findViewById(R.id.bt_register_and_login);
		btRegissterLogin.setOnClickListener(this);
	}
	
	
	private void initSDK()
	 {
		SMSSDK.initSDK(this, "1a4ac0027d208", "d1a947d8ec595105d0f25022b276dd35");
		 eventHandler = new EventHandler() {
		  @Override
		  public void afterEvent(int event, int result, Object data) {
		  Message msg = new Message();
		  msg.arg1 = event;
		  msg.arg2 = result;
		  msg.obj = data;
		  msg.what = SMSDDK_HANDLER;
		  handler.sendMessage(msg);
		  }
		 };
		 SMSSDK.registerEventHandler(eventHandler);  
	 }
	
	Handler handler =new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){

			case SMSDDK_HANDLER:
				int event=msg.arg1;
				int result=msg.arg2;
				Object data=msg.obj;
				//回调完成
				if(result==SMSSDK.RESULT_COMPLETE){
					if(event==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
						//验证码验证成功的
						Log.d("verisucee","success");
						llStepNavigate.setBackgroundResource(R.drawable.step2);
						tvStep1.setTextColor(getResources().getColor(R.color.white2));
						tvStep2.setTextColor(getResources().getColor(R.color.white2));
						vfRegister.showNext();
						viewIndex++;
						
					}
					//已发送验证码 
					else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE)
					{
					    Toast.makeText(getApplicationContext(), getString(R.string.send_message),
					     Toast.LENGTH_SHORT).show();
					} else
					{
						Toast.makeText(getApplicationContext(), "出错了", Toast.LENGTH_SHORT).show();
					  ((Throwable) data).printStackTrace();
					}
				}
				if(result==SMSSDK.RESULT_ERROR)
					   {
					   try {
					    Throwable throwable = (Throwable) data;
					    throwable.printStackTrace();
					    JSONObject object = new JSONObject(throwable.getMessage());
					    String des = object.optString("detail");//错误描述
					    int status = object.optInt("status");//错误代码
					    if (status > 0 && !TextUtils.isEmpty(des)) {
					    	Toast.makeText(getApplicationContext(), des, Toast.LENGTH_SHORT).show();
					    return;
					    }
					   } catch (Exception e) {
					    //do something
						   Toast.makeText(getApplicationContext(), "出错了", Toast.LENGTH_SHORT).show();
					   }
				}
				//计时器相关
			case TIMECHANGE:
				Bundle timedata = msg.getData();
				int time = timedata.getInt("time");
				btGetCode.setText(String.valueOf(time));
				break;
			case TIMERCOMPLETE:
				btGetCode.setClickable(true);
				setButtonBackgroud(btGetCode);
				btGetCode.setText(R.string.get_verification_code);
				btNextStep.setClickable(false);
				setButtonBackgroud(btNextStep);
				handler.removeCallbacks(timerRunnable);
				validTime = 60;
				timerRunnable = null;
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();  
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_reback:
			reback();
			break;
		case R.id.bt_get_checkCode:
			phone = etPhoneNum.getText().toString().trim();
			getCaptcha(phone);
			break;
		case R.id.bt_next_step:
			String captcha = etCheckCode.getText().toString().trim();
			if (captcha == null || "".equals(captcha)) {
				Toast.makeText(this, R.string.null_captcha, 0).show();
				return;
			}
			Log.d("veri", captcha);
			SMSSDK.submitVerificationCode("86", phone, captcha);//请求验证
			break;
		case R.id.bt_register_and_login:
			doRegisterAndLogin();
			break;
		default:
			break;
		}
	}

	
	private void getCaptcha(String phone2) {
		if (MatchUtil.isPhoneNum(phone)){
			OkGo.post(Constant.CHECK_PHONE)
			.tag(this)
			.params("phone",phone)
			.execute(new StringCallback() {
				
				@Override
				public void onSuccess(String data, Call arg1, Response response) {
					// TODO Auto-generated method stub
					if(response.isSuccessful()){
						try {
							JSONObject result=new JSONObject(data);
							int code=Integer.parseInt(result.getString("code"));
							String msg=result.getString("msg");
							if(code==0){
								Toast.makeText(getApplicationContext(), getString(R.string.phone_has_register), 0).show();
								mySetResult(3001, phone);//会直接回去的，所以后面不用管理
							}
							else{
								SMSSDK.getVerificationCode("86", phone);
								btGetCode.setClickable(false);
								setButtonBackgroud(btGetCode);
								// 定时器
								if (timerRunnable != null) {
									handler.removeCallbacks(timerRunnable);
									timerRunnable = null;
								}
								//控制计时的东西
								timerRunnable = new Runnable() {
									@Override
									public void run() {
										if (validTime > 0) {
											validTime--;
											Message msg = handler.obtainMessage();
											msg.what = TIMECHANGE;
											Bundle bundle = new Bundle();
											bundle.putInt("time", validTime);
											msg.setData(bundle);
											msg.sendToTarget();
											handler.postDelayed(timerRunnable, 1000);
										} else {
											handler.sendEmptyMessage(TIMERCOMPLETE);
										}
									}
								};
								handler.post(timerRunnable);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							Toast.makeText(getApplicationContext(), "数据解析失败", 0).show();
							e.printStackTrace();
						}
					}
					
				}
			});
			
			
		}else{
			if ("".equals(phone)) {
				Toast.makeText(this, R.string.null_phone, 0).show();
			} else {
				Toast.makeText(this, R.string.error_phone, 0).show();
			}
			btNextStep.setClickable(false);
			setButtonBackgroud(btNextStep);
			return;
		}
	}


	protected void mySetResult(int resultCode, String loginName) {
		Intent data = new Intent();
		data.putExtra("Account", loginName);  //返回给跳转到这个活动的activity
		setResult(resultCode, data);
	}

	/**
	 * 注册并登录
	 */
	private void doRegisterAndLogin() {
		// 验证用户名
		String username = etUserName.getText().toString().trim();
		if (!MatchUtil.isUsername(username)) {
			if ("".equals(username)) {
				Toast.makeText(this, R.string.null_account, 0).show();
			} else {
				Toast.makeText(this, R.string.error_account, 0).show();
			}
			return;
		}
		// 验证密码
		String loginPasWord = etPassWord.getText().toString().trim();
		if ("".equals(loginPasWord)) {
			Toast.makeText(this, R.string.null_password, 0).show();
			return;
		} else if (loginPasWord.length() < 6) {
			Toast.makeText(this, R.string.short_password, 0).show();
			return;
		}
		 if (!MatchUtil.isLicitPassword(loginPasWord)) {
			Toast.makeText(this, R.string.error_password, 0).show();
			return;
		}
		requestRegister(phone, username, loginPasWord);
	}

	
	private void requestRegister(String phone, String username, String loginPasWord) {
		OkGo.post(Constant.REGISTER)
		.tag(this)
		.params("phone",phone)
		.params("nickname",username)
		.params("password",loginPasWord)
		.execute(new StringCallback() {
			@Override
			public void onSuccess(String data, Call call, Response response) {
				User user=new User();
				if(response.isSuccessful()){
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
							SharedPreferences sp = RegisterActivity.getOrSharedPrefences(RegisterActivity.this);
							Editor editor = sp.edit();
							Toast.makeText(getApplicationContext(), RegisterActivity.this.getString(R.string.registerSuccess), 0).show(); //注册成功并登录
							Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
							startActivity(intent);//跳转到主页
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Toast.makeText(getApplicationContext(), "数据解析失败", 0).show();
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 返回前一页 如果ViewFlipper 的当前页不是第一页退回前一页，否则退出整个页面，
	 */
	private void reback() {
		if (viewIndex > 0) {
			llStepNavigate.setBackgroundResource(R.drawable.step1);
			tvStep1.setTextColor(getResources().getColor(R.color.white2));
			tvStep2.setTextColor(getResources().getColor(R.color.textcolor));
			vfRegister.showPrevious();
			viewIndex--;
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		reback();
	}
	
	private void setButtonBackgroud(Button button) {
		if (button.isClickable()) {
			button.setBackgroundResource(R.drawable.register_step_background_selector);
		} else {
			button.setBackgroundResource(R.drawable.unclickble_right_round);
		}
	}
	
	class captChaTextWatcher implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			int length = s.length();
			if (length > 0) {
				if (s.charAt(length - 1) == ' ') {
					s.delete(length - 1, length);
					length--;
					return;
				}
			}
			if (length > 4) {
				s.delete(length - 1, length);
				length--;
				return;
			}
			if (length == 4 ) {
				phone = etPhoneNum.getText().toString().trim();
				if (MatchUtil.isPhoneNum(phone)) {
					btNextStep.setClickable(true);
					setButtonBackgroud(btNextStep);
				} else {
					if ("".equals(phone)) {
						Toast.makeText(getApplicationContext(), R.string.null_phone, 0).show();
					} else {
						Toast.makeText(getApplicationContext(), R.string.error_phone, 0).show();
					}
				}

			} else {
				btNextStep.setClickable(false);
				setButtonBackgroud(btNextStep);
			}
		}

	}

}



