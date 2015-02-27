package com.handsomezhou.t9searchdemo.util;

import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

public class ViewUtil {
	public static void showTextNormal(TextView tv,String text){
		if((null==tv)||(null==text)){
			return;
		}
		
		tv.setText(text);
	}
	
	/**
	 * @param tv
	 * @param baseText
	 * @param highlightText
	 * if the string of highlightText is a subset of the string of baseText,highlight the string of highlightText.
	 */
	public static void showTextHighlight(TextView tv,String baseText,String highlightText){
		if((null==tv)||(null==baseText)||(null==highlightText)){
			return;
		}
		
		int index=baseText.indexOf(highlightText);
		if(index<0){
			tv.setText(baseText);
			return;
		}
		
		int len=highlightText.length();
		/**
		 *  "<u><font color=#FF8C00 >"+str+"</font></u>"; 	//with underline
		 *  "<font color=#FF8C00>"+str+"</font>";			//without underline
		 */
		Spanned spanned=Html.fromHtml(baseText.substring(0, index)+"<font color=#FF8C00 >" 
                + baseText.substring(index, index + len) + "</font>" 
                + baseText.substring(index + len, baseText.length()));
		
		tv.setText(spanned);
	}
}
