package com.t9search.util;

import java.util.List;

import com.t9search.model.PinyinUnit;
import com.t9search.model.T9PinyinUnit;

public class T9MatchPinyinUnits {
	//private static final String TAG="T9MatchPinyinUnits";
	/**
	 * @description match Pinyin Units
	 * @param pinyinUnits		
	 * @param baseData   		the original string which be parsed to PinyinUnit
	 * @param search			search key words
	 * @param chineseKeyWord	the sub string of base data
	 * @return true if match success,false otherwise. 
	 */
	public static boolean matchPinyinUnits(final List<PinyinUnit> pinyinUnits,final String baseData, String search,StringBuffer chineseKeyWord){
		if((null==pinyinUnits)||(null==search)||(null==chineseKeyWord)){
			return false;
		}
		
		StringBuffer matchSearch=new StringBuffer();
		matchSearch.delete(0, matchSearch.length());
		chineseKeyWord.delete(0, chineseKeyWord.length());
		PinyinUnit pyUnit=null;
		
		int pinyinUnitsLength=0;
		pinyinUnitsLength=pinyinUnits.size();
		StringBuffer searchBuffer=new StringBuffer();
		for(int i=0; i<pinyinUnitsLength; i++){
			pyUnit=pinyinUnits.get(i);
			for(int j=0; j<pyUnit.getT9PinyinUnitIndex().size(); j++){
				chineseKeyWord.delete(0, chineseKeyWord.length());
				searchBuffer.delete(0, searchBuffer.length());
				searchBuffer.append(search);
				boolean found=findPinyinUnits(pinyinUnits, i, j, baseData, searchBuffer, chineseKeyWord);
				if(true==found){
					return true;
				}
			}
		}
					
		return false;
	}
	
	/**
	 * @param pinyinUnits		pinyinUnits head node index
	 * @param pinyinUnitIndex   pinyinUint Index
	 * @param t9PinyinUnitIndex t9PinyinUnit Index 
	 * @param baseData			base data for search.
	 * @param searchBuffer		search keyword.
	 * @param chineseKeyWord	save the Chinese keyword.
	 * @return true if find,false otherwise.
	 */
	private static boolean findPinyinUnits(final List<PinyinUnit> pinyinUnits,int pinyinUnitIndex,int t9PinyinUnitIndex,final String baseData, StringBuffer searchBuffer,StringBuffer chineseKeyWord ){
		if((null==pinyinUnits)||(null==baseData)||(null==searchBuffer)||(null==chineseKeyWord)){
			return false;
		}
		
		String search=searchBuffer.toString();
		if(search.length()<=0){	//match success
			return true;
		}
		
		if(pinyinUnitIndex>=pinyinUnits.size()){
			return false;
		}
		PinyinUnit pyUnit=pinyinUnits.get(pinyinUnitIndex);
		
		if(t9PinyinUnitIndex>=pyUnit.getT9PinyinUnitIndex().size()){
			return false;
		}
		
		T9PinyinUnit t9PinyinUnit=pyUnit.getT9PinyinUnitIndex().get(t9PinyinUnitIndex);
		
		
		
		if(pyUnit.isPinyin()){
			
			if(search.startsWith(String.valueOf(t9PinyinUnit.getNumber().charAt(0)))){// match pinyin first character
				searchBuffer.delete(0,1);//delete the match character
				chineseKeyWord.append(baseData.charAt(pyUnit.getStartPosition()));
				boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex+1, 0, baseData, searchBuffer, chineseKeyWord);
				if(true==found){
					return true; 
				}else{
					searchBuffer.insert(0, t9PinyinUnit.getNumber().charAt(0));
					chineseKeyWord.deleteCharAt(chineseKeyWord.length()-1);
				}
				
			}
			
			if(t9PinyinUnit.getNumber().startsWith(search)){
				//The string of "search" is the string of t9PinyinUnit.getNumber() of a subset. means match success.
				chineseKeyWord.append(baseData.charAt(pyUnit.getStartPosition()));
				searchBuffer.delete(0, searchBuffer.length());	
				return true;
				
			}else if(search.startsWith(t9PinyinUnit.getNumber())){ //match quanpin  success
				//The string of t9PinyinUnit.getNumber() is the string of "search" of a subset.
				searchBuffer.delete(0, t9PinyinUnit.getNumber().length());
				chineseKeyWord.append(baseData.charAt(pyUnit.getStartPosition()));
				boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex+1, 0, baseData, searchBuffer, chineseKeyWord);
				if(true==found){
					return true;
				}else{
					searchBuffer.insert(0, t9PinyinUnit.getNumber());
					chineseKeyWord.deleteCharAt(chineseKeyWord.length()-1);
				}
			}else{ //mismatch
				boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex, t9PinyinUnitIndex+1, baseData, searchBuffer, chineseKeyWord);
				if(found==true){
					return true;
				}
			}
			
		}else{ //non-pure Pinyin
			
			if(t9PinyinUnit.getNumber().startsWith(search)){
				//The string of "search" is the string of t9PinyinUnit.getNumber() of a subset.
				int startIndex=0; 
				chineseKeyWord.append(baseData.substring(startIndex+pyUnit.getStartPosition(),startIndex+pyUnit.getStartPosition()+ search.length()));
				searchBuffer.delete(0, searchBuffer.length());
				return true;
			}else if(search.startsWith(t9PinyinUnit.getNumber())){ //match all non-pure pinyin 
				//The string of t9PinyinUnit.getNumber() is the string of "search" of a subset.
				int startIndex=0; 
				searchBuffer.delete(0, t9PinyinUnit.getNumber().length());
				chineseKeyWord.append(baseData.substring(startIndex+pyUnit.getStartPosition(),startIndex+pyUnit.getStartPosition()+ t9PinyinUnit.getNumber().length()));
				boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex+1, 0, baseData, searchBuffer, chineseKeyWord);
				if(true==found){
					return true;
				}else{
					searchBuffer.insert(0, t9PinyinUnit.getNumber());
					chineseKeyWord.delete(chineseKeyWord.length()-t9PinyinUnit.getNumber().length(), chineseKeyWord.length());
				}
			}else if((chineseKeyWord.length()<=0)){
				if(t9PinyinUnit.getNumber().contains(search)){
					int index=t9PinyinUnit.getNumber().indexOf(search);
					chineseKeyWord.append(baseData.substring(index+pyUnit.getStartPosition(),index+pyUnit.getStartPosition()+ search.length()));
					searchBuffer.delete(0, searchBuffer.length());
					return true;
				}else{
//					 match case:[Non-Chinese characters]+[Chinese characters]
//					 for example:baseData="Tony测试"; match this case:"onycs"<===>"66927" 
					//start [Non-Chinese characters]+[Chinese characters]
					int numLength=t9PinyinUnit.getNumber().length();
					for(int i=0; i<numLength; i++){
						String subStr=t9PinyinUnit.getNumber().substring(i);
						if(search.startsWith(subStr)){
							searchBuffer.delete(0, subStr.length());
							chineseKeyWord.append(baseData.substring(i+pyUnit.getStartPosition(), i+pyUnit.getStartPosition()+subStr.length()));
							boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex+1, 0, baseData, searchBuffer, chineseKeyWord);
							if(true==found){
								return true;
							}else{
								searchBuffer.insert(0, t9PinyinUnit.getNumber().substring(i));
								chineseKeyWord.delete(chineseKeyWord.length()-subStr.length(), chineseKeyWord.length());
							}
							
						}
					}
					//end [Non-Chinese characters]+[Chinese characters]
					
					//in fact,if pyUnit.isPinyin()==false, pyUnit.getT9PinyinUnitIndex().size()==1. The function of findPinyinUnits() will return false.
					boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex, t9PinyinUnitIndex+1, baseData, searchBuffer, chineseKeyWord);
					if(found==true){
						return true;
					}
				}
			}else { //mismatch
				//in fact,if pyUnit.isPinyin()==false, pyUnit.getT9PinyinUnitIndex().size()==1.  The function of findPinyinUnits() will return false.
				boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex, t9PinyinUnitIndex+1, baseData, searchBuffer, chineseKeyWord);
				if(found==true){
					return true;
				}
			}
		}
		return false;
	}
}
