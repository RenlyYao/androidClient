package com.example.newapp.utils;


/**
 * 字符串匹配工具类
 * 
 * @author Administrator
 * 
 */
public class MatchUtil {
	// 手机号码正则表达式
	private static String PHONEREG = "^(13\\d|147|15[0-35-9]|18[025-9])\\d{8}$";

	// 以字母开头的英文或数字组合3到50位字符串
	private static String CHACCOUNT = "^\\w{3,20}$";

	// private static String CHACCOUNT = "^[A-Z0-9a-z_]{3,50}$";

	private static String PASSWORDREG = "^[\\x20-\\x7E]{6,50}$";

	/**
	 * 验证字符串是否是手机号码
	 * 
	 * @param matchStr
	 *            代匹配的字符串
	 * @return 如果是返回true否则返回false
	 */
	public static boolean isPhoneNum(String matchStr) {

		return matchStr == null ? false : matchStr.matches(PHONEREG);

	}

	

	//是否为合法的密码
	public static boolean isLicitPassword(String matcherStr) {

		return matcherStr == null ? false : matcherStr.matches(PASSWORDREG);

	}


	public static boolean isUsername(String username) {
		 return username == null ? false : username.matches(CHACCOUNT);
	}
}
