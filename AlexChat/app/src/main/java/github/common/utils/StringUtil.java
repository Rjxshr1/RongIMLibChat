package github.common.utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;
/**String超级工具类
 * <br/>1.1... 转首字母大写 {@link #becomeCapitalized(String str)}
 * <br/>1.2... 半角转成全角 {@link #halfWidthToFullWidth(String str)}
 * <br/>1.3... 全角转成半角 {@link #fullWidthToHalfWidth(String str)}
 * <br/>2.1... 判断是空字串 {@link #isEmpty(String str)}
 * <br/>2.1... 判断是纯数字 {@link #isAllNumric()}
 * <br/>2.1... 判断是纯字母 {@link #isAllLetters()}
 * <br/>2.1... 判断是纯汉字 {@link #isAllChinese()}
 * <br/>2.2... 判断含有汉字 {@link #isHaveChinese()}
 * <br/>2.2... 限制字符长度 {@link #isLengthOK()}
 * <br/>2.3... 验证邮箱合法 {@link #isEmail()}
 * <br/>2.3... 验证qq号合法 {@link #isQQ()}
 * <br/>2.4... 提取子串集合 {@link #getRegexMatcherResults()
 * <br/>0... 常用正则表达式 {@link #commonRegex()}
 */
public class StringUtil 
{
	private static final String TAG = "StringUtil";
	private static final char CHAR_CHINESE_SPACE = '\u3000';//中文（全角）空格
	/**判断String是null 或 ""*/
	public static boolean isEmpty(String str) {
		return (str == null || str.length() == 0);
	}
	/**变成首字母大写
	 * <br/>capitalizeFirstLetter(null)     =   null;
	 * <br/>capitalizeFirstLetter("")       =   "";
	 * <br/>capitalizeFirstLetter("2ab")    =   "2ab"
	 * <br/>capitalizeFirstLetter("a")      =   "A"
	 * <br/>capitalizeFirstLetter("ab")     =   "Ab"
	 * <br/>capitalizeFirstLetter("Abc")    =   "Abc"
	 */
	public static String becomeCapitalized(String str)
	{
		if (isEmpty(str)){
			return str;
		}
		char c = str.charAt(0);
		return (!Character.isLetter(c) || Character.isUpperCase(c)) ? str : new StringBuilder(str.length()).append(Character.toUpperCase(c)).append(str.substring(1)).toString();
	}
	/**全角符号--->半角符号
	 * <br/>fullWidthToHalfWidth(null) = null;
	 * <br/>fullWidthToHalfWidth("") = "";
	 * <br/>fullWidthToHalfWidth(new String(new char[] {12288})) = " ";
	 * <br/>fullWidthToHalfWidth("！＂＃＄％＆) = "!\"#$%&";
	 */
	public static String fullWidthToHalfWidth(String str) {
		if (isEmpty(str)) {
			return str;
		}
		char[] source = str.toCharArray();
		for (int i = 0; i < source.length; i++) {
			if (source[i] == 12288) {
				source[i] = ' ';
			} else if (source[i] >= 65281 && source[i] <= 65374) {
				source[i] = (char)(source[i] - 65248);
			} else {
				source[i] = source[i];
			}
		}
		return new String(source);
	}

	/**半角符号---->全角符号
	 * <br/>halfWidthToFullWidth(null) = null;
	 * <br/>halfWidthToFullWidth("") = "";
	 * <br/>halfWidthToFullWidth(" ") = new String(new char[] {12288});
	 * <br/>halfWidthToFullWidth("!\"#$%&) = "！＂＃＄％＆";
	 */
	public static String halfWidthToFullWidth(String str) {
		if (isEmpty(str)) {
			return str;
		}
		char[] source = str.toCharArray();
		for (int i = 0; i < source.length; i++) {
			if (source[i] == ' ') {
				source[i] = (char)12288;
			} else if (source[i] >= 33 && source[i] <= 126) {
				source[i] = (char)(source[i] + 65248);
			} else {
				source[i] = source[i];
			}
		}
		return new String(source);
	}
	/**判断是纯数字 [0-9]
	 * <br/>不匹配, 返回false
	 */
	public static boolean isAllNumric(String str) 
	{
		if (str != null && !str.trim().equalsIgnoreCase("")) {
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	/**判断是纯英文字母, [a-z,A-Z]
	 * <br/>不匹配, 返回false 
	 */
	public static boolean isAllLetters(String str) 
	{
		if (str != null && !str.trim().equalsIgnoreCase("")) {
			Pattern pattern = Pattern.compile("[A-Za-z]+");
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	/**判断是纯汉字
	 * <br/>不匹配, 返回false 
	 */
	public static boolean isAllChinese(String str) 
	{
		if (str != null && !str.trim().equalsIgnoreCase("")) 
		{
			Pattern pattern = Pattern.compile("[\u4E00-\u9FA5\uF900-\uFA2D]+");
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	/**仅仅 包含 汉字 和 数字
	 * <br/>不匹配, 返回false 
	 */
	public static boolean isOnlyChinese2Num(String str) 
	{
		if (str != null && !str.trim().equalsIgnoreCase("")) 
		{
			Pattern pattern = Pattern.compile("[\u4E00-\u9FA5\uF900-\uFA2D0-9]+");
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	/**判断含有汉字
	 * <br/>不匹配, 返回false
	 */
	public static boolean isHaveChinese(String str) 
	{
		if (str != null && !str.trim().equalsIgnoreCase("")) 
		{
			for (int i = 0; i < str.length(); i++) {
				char ss = str.charAt(i);
				boolean s = String.valueOf(ss).matches("[\u4E00-\u9FA5\uF900-\uFA2D]");
				if (s) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}
	/**判断字符个数, 是否满足长度范围[min, max]; 所有的文字,都算作一个字符
	 *  <br/>不匹配, 返回false
	 */
	public static boolean isLengthOK(String str, int min,int max) 
	{
		if (str != null && !str.trim().equalsIgnoreCase("")) {
			String Regular = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{"+min+"," + max + "}$";
			Pattern pattern = Pattern.compile(Regular, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	/**验证邮箱合法性
	 * <br/>不匹配, 返回false
	 */
	public static boolean isEmail(String str) 
	{
		if (str != null && !str.trim().equalsIgnoreCase("")) {
			String Regular = "^([a-z0-9A-Z]+[-|\\_.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern pattern = Pattern.compile(Regular, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	/**验证QQ号码合法性, [min, max]
	 */
	public static boolean isQQ(String str, int min, int max) 
	{
		if (str != null && !str.trim().equalsIgnoreCase("")) {
			Pattern pattern = Pattern.compile("[1-9][0-9]{"+(min-1)+","+(max-1)+"}");
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static boolean isPhoneNum(String phoneNum)
	{
		boolean flag = false;
		try
		{
			Pattern p = Pattern.compile("(1)[0-9]{10}");
			Matcher m = p.matcher(phoneNum);
			flag = m.matches();
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}
	/**在str中找到所有满足regex的匹配子串, 组成集合
	 * <br/>常用正则匹配见 {@link #commonRegex()} 方法
	 */
	public static ArrayList<String> getRegexMatcherResults(String str, String regex) {
		try {
			if (str != null && !str.trim().equalsIgnoreCase("")) {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(str);
				ArrayList<String> RegexMatcherResults = new ArrayList<String>();
				while (matcher.find()) {
					Log.e(TAG, "getRegexMatcherResults=" + matcher.group(1));
					RegexMatcherResults.add(matcher.group(1));
				}
				return RegexMatcherResults;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG, "getRegexMatcherResults is Error! errorCode = " + ex.getMessage());
		}
		return null;
	}
	/**通过正则表达式提取字串
	 * @param str 传入的字符串
	 * @param regEx 正则表达式
	 * @return 要获取的字符串
	 */
	public static String getRegexString(String str) {
		String strReturn = "";
		int a = str.indexOf("{");
		int b = str.lastIndexOf("}");
		strReturn = str.substring(a, b + 1);
		return strReturn;
	}
	public static boolean isRightPwd(String pwd)
	{
		boolean flag = false;
		try
		{
			Pattern p = Pattern.compile("^[0-9a-zA-Z_]{6,18}");
			Matcher m = p.matcher(pwd);
			flag = m.matches();
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}
	/**
	 * 描述：是字母和数字.
	 * @param str 指定的字符串
	 * @return 是否只是字母和数字:是为true，否则false
	 */
	public static Boolean isNumberLetter(String str) {
		Boolean isNoLetter = false;
		String expr = "^[A-Za-z0-9]+$";
		if (str.matches(expr)) {
			isNoLetter = true;
		}
		return isNoLetter;
	}
	/**
	 * 从字符串s中截取某一段字符串
	 * @param s
	 * @param startToken 开始标记
	 * @param endToken 结束标记
	 * @return
	 */
	public static String mid(String s, String startToken, String endToken) {
		return mid(s, startToken, endToken, 0);
	}
	public static String mid(String s, String startToken, String endToken, int fromStart) {
		if (startToken==null || endToken==null)
			return null;
		int start = s.indexOf(startToken, fromStart);
		if (start==(-1))
			return null;
		int end = s.indexOf(endToken, start + startToken.length());
		if (end==(-1))
			return null;
		String sub = s.substring(start + startToken.length(), end);
		return sub.trim();
	}
	/**
	 * 简化字符串，通过删除空格键、tab键、换行键等实现
	 * @param s
	 * @return
	 */
	public static String compact(String s) {
		char[] cs = new char[s.length()];
		int len = 0;
		for(int n=0; n<cs.length; n++) {
			char c = s.charAt(n);
			if(c==' ' || c=='\t' || c=='\r' || c=='\n' || c==CHAR_CHINESE_SPACE)
				continue;
			cs[len] = c;
			len++;
		}
		return new String(cs, 0, len);
	}
	/**
	 * 求两个字符串数组的并集，利用set的元素唯一性  
	 * @param arr1
	 * @param arr2
	 * @return
	 */
	public static String[] union(String[] arr1, String[] arr2) {  
		Set<String> set = new HashSet<String>();  
		for (String str : arr1) {  
			set.add(str);  
		}  
		for (String str : arr2) {  
			set.add(str);  
		}
		String[] result = {};  
		return set.toArray(result);  
	}
	/**
	 * 求两个字符串数组的交集 
	 * @param arr1
	 * @param arr2
	 * @return
	 */
	public static String[] intersect(String[] arr1, String[] arr2) {  
		Map<String, Boolean> map = new HashMap<String, Boolean>();  
		LinkedList<String> list = new LinkedList<String>();  
		for (String str : arr1) {  
			if (!map.containsKey(str)) {  
				map.put(str, Boolean.FALSE);  
			}  
		}
		for (String str : arr2) {  
			if (map.containsKey(str)) {  
				map.put(str, Boolean.TRUE);  
			}  
		}  
		for (Entry<String, Boolean> e : map.entrySet()) {  
			if (e.getValue().equals(Boolean.TRUE)) {  
				list.add(e.getKey());  
			}  
		}  
		String[] result = {};  
		return list.toArray(result);  
	}
	/**
	 * 求两个字符串数组的差集  
	 * @param arr1
	 * @param arr2
	 * @return
	 */
	public static String[] minus(String[] arr1, String[] arr2) {  
		LinkedList<String> list = new LinkedList<String>();  
		LinkedList<String> history = new LinkedList<String>();  
		String[] longerArr = arr1;  
		String[] shorterArr = arr2;  
		//找出较长的数组来减较短的数组  
		if (arr1.length > arr2.length) {  
			longerArr = arr2;  
			shorterArr = arr1;  
		}  
		for (String str : longerArr) {  
			if (!list.contains(str)) {  
				list.add(str);  
			}  
		}  
		for (String str : shorterArr) {  
			if (list.contains(str)) {  
				history.add(str);  
				list.remove(str);  
			} else {  
				if (!history.contains(str)) {  
					list.add(str);  
				}  
			}  
		}  

		String[] result = {};  
		return list.toArray(result);  
	}
	/**
	 * 字符串数组反转
	 * @param strs
	 * @return
	 */
	public static String[] reverse(String[] strs) {
		for (int i = 0; i < strs.length; i++) {
			String top = strs[0];
			for (int j = 1; j < strs.length - i; j++) {
				strs[j - 1] = strs[j];
			}
			strs[strs.length - i - 1] = top;
		}
		return strs;
	}
	/**常用正则表达式
	 * <pre>
	 * 常用正则表达式代码提供 验证数字: ^[0-9]*$ <br>
	 * 验证n位的数字: ^\d{n}$ <br>
	 * 验证至少n位数字: ^\d{n,}$ <br>
	 * 验证m-n位的数字: ^\d{m,n}$ <br>
	 * 验证零和非零开头的数字: ^(0|[1-9][0-9]*)$ <br>
	 * 验证有两位小数的正实数: ^[0-9]+(.[0-9]{2})?$ <br>
	 * 验证有1-3位小数的正实数: ^[0-9]+(.[0-9]{1,3})?$ <br>
	 * 验证非零的正整数: ^\+?[1-9][0-9]*$ <br>
	 * 验证非零的负整数: ^\-[1-9][0-9]*$ <br>
	 * 验证非负整数（正整数 + 0） ^\d+$ <br>
	 * 验证非正整数（负整数 + 0） ^((-\d+)|(0+))$ <br>
	 * 验证长度为3的字符: ^.{3}$ <br>
	 * 验证由26个英文字母组成的字符串: ^[A-Za-z]+$ <br>
	 * 验证由26个大写英文字母组成的字符串: ^[A-Z]+$ <br>
	 * 验证由26个小写英文字母组成的字符串: ^[a-z]+$ <br>
	 * 验证由数字和26个英文字母组成的字符串: ^[A-Za-z0-9]+$ <br>
	 * 验证由数字、26个英文字母或者下划线组成的字符串: ^\w+$ <br>
	 * 验证用户密码:^[a-zA-Z]\w{5,17}$ 正确格式为: 以字母开头，长度在6-18之间，只能包含字母、数字和下划线。 <br>
	 * 验证是否含有 ^%&',;=?$\" 等字符: [^%&',;=?$\x22]+ <br>
	 * 验证汉字: ^[\u4E00-\u9FA5\uF900-\uFA2D]+$ <br>
	 * 验证Email地址: ^\w+[-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$ <br>
	 * 验证InternetURL: ^http://([\w-]+\.)+[\w-]+(/[\w-./?%&=]*)?$
	 * ；^[a-zA-z]+://(w+(-w+)*)(.(w+(-w+)*))*(?S*)?$ <br>
	 * 验证手机号码: ^(\(\d{3,4}\)|\d{3,4}-)?\d{7,8}$: --正确格式为: XXXX-XXXXXXX，XXXX-
	 * XXXXXXXX，xx-XXXXXXXX，XXXXXXX，XXXXXXXX。 <br>
	 * 验证身份证号（15位或18位数字）: ^\d{15}|\d{}18$ <br>
	 * 验证一年的12个月: ^(0?[1-9]|1[0-2])$ 正确格式为: “01”-“09”和“1”“12” <br>
	 * 验证一个月的31天: ^((0?[1-9])|((1|2)[0-9])|30|31)$ 正确格式为: 01、09和1、31。 <br>
	 * 整数: ^-?\d+$ <br>
	 * 非负浮点数（正浮点数 + 0）: ^\d+(\.\d+)?$ <br>
	 * 正浮点数
	 * ^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9
	 * ][0-9]*))$ <br>
	 * 非正浮点数（负浮点数 + 0） ^((-\d+(\.\d+)?)|(0+(\.0+)?))$ <br>
	 * 负浮点数
	 * ^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1
	 * -9][0-9]*)))$ <br>
	 * 浮点数 ^(-?\d+)(\.\d+)?$ <br>
	 * </pre>
	 */
	public void commonRegex(){

	}
}
