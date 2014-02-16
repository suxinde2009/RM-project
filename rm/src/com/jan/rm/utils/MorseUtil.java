package com.jan.rm.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 *   _    ________    __    __    ________    ________    __    __    ________    ___   __ _
 *  /\\--/\______ \--/\ \--/\ \--/\  _____\--/\  ____ \--/\ \--/\ \--/\  _____\--/\  \-/\ \\\
 *  \ \\ \/_____/\ \ \ \ \_\_\ \ \ \ \____/_ \ \ \__/\ \ \ \ \_\ \ \ \ \ \____/_ \ \   \_\ \\\
 *   \ \\       \ \ \ \ \  ____ \ \ \  _____\ \ \  ____ \ \_ \ \_\ \  \ \  _____\ \ \  __   \\\
 *    \ \\       \ \ \ \ \ \__/\ \ \ \ \____/_ \ \ \__/\ \  \_ \ \ \   \ \ \____/_ \ \ \ \_  \\\
 *     \ \\       \ \_\ \ \_\ \ \_\ \ \_______\ \ \_\ \ \_\   \_ \_\    \ \_______\ \ \_\_ \__\\\
 *      \ \\       \/_/  \/_/  \/_/  \/_______/  \/_/  \/_/     \/_/     \/_______/  \/_/ \/__/ \\
 *       \ \\----------------------------------------------------------------------------------- \\
 *        \//                                                                                   \//
 *
 * 
 *
 */

public class MorseUtil {
	
	private static HashMap<Character, String> morseCodeMap;
	
	//based on ITU standard
	private static final String A = "01";
	private static final String B = "1000";
	private static final String C = "1010";
	private static final String D = "100";
	private static final String E = "0";
	private static final String F = "0010";
	private static final String G = "110";
	private static final String H = "0000";
	private static final String I = "00";
	private static final String J = "0111";
	private static final String K = "101";
	private static final String L = "0100";
	private static final String M = "11";
	private static final String N = "10";
	private static final String O = "111";
	private static final String P = "0110";
	private static final String Q = "1101";
	private static final String R = "010";
	private static final String S = "000";
	private static final String T = "1";
	private static final String U = "001";
	private static final String V = "0001";
	private static final String W = "011";
	private static final String X = "1001";
	private static final String Y = "1011";
	private static final String Z = "1100";
	
	private static final String ZERO = "11111";
	private static final String ONE = "01111";
	private static final String TWO = "00111";
	private static final String THREE = "00011";
	private static final String FOUR = "00001";
	private static final String FIVE = "00000";
	private static final String SIX = "10000";
	private static final String SEVEN = "11000";
	private static final String EIGHT = "11100";
	private static final String NINE = "11110";
	
	private static final char MORSE_SEPERATOR = '-';
	
	public static final int DASH = 1;
	public static final int DOT = 0;
	
	static{
		
		morseCodeMap = new HashMap<Character, String>();
		
		morseCodeMap.put('A', A);
		morseCodeMap.put('B', B);
		morseCodeMap.put('C', C);
		morseCodeMap.put('D', D);
		morseCodeMap.put('E', E);
		morseCodeMap.put('F', F);
		morseCodeMap.put('G', G);
		morseCodeMap.put('H', H);
		morseCodeMap.put('I', I);
		morseCodeMap.put('J', J);
		morseCodeMap.put('K', K);
		morseCodeMap.put('L', L);
		morseCodeMap.put('M', M);
		morseCodeMap.put('N', N);
		morseCodeMap.put('O', O);
		morseCodeMap.put('P', P);
		morseCodeMap.put('Q', Q);
		morseCodeMap.put('R', R);
		morseCodeMap.put('S', S);
		morseCodeMap.put('T', T);
		morseCodeMap.put('U', U);
		morseCodeMap.put('V', V);
		morseCodeMap.put('W', W);
		morseCodeMap.put('X', X);
		morseCodeMap.put('Y', Y);
		morseCodeMap.put('Z', Z);
		
		morseCodeMap.put('0', ZERO);
		morseCodeMap.put('1', ONE);
		morseCodeMap.put('2', TWO);
		morseCodeMap.put('3', THREE);
		morseCodeMap.put('4', FOUR);
		morseCodeMap.put('5', FIVE);
		morseCodeMap.put('6', SIX);
		morseCodeMap.put('7', SEVEN);
		morseCodeMap.put('8', EIGHT);
		morseCodeMap.put('9', NINE);
	}
	
	public static List<Character> lettersToMorseString(String letters){
		List<Character> result = new ArrayList<Character>();
		int length = letters.length();
		for(int i = 0; i < length; i++){
			String letter = morseCodeMap.get(letters.charAt(i));
			for(int j = 0; j < letter.length(); j++){
				result.add(letter.charAt(j));
			}
			result.add(MORSE_SEPERATOR);
		}
		
		return result;
	}

	/**
	 * 
	 * @param letters letters to convert to morse code
	 * @param dashTime continuously times for longer marks in milliseconds
	 * @param dotTime continuously times for shorter marks in milliseconds
	 * @param separateTime separate time for marks within letter
	 * @param lettersSeparateTime separate times between letters
	 * @param sectionTime times before next loop
	 * @return
	 */
	public static List<long[]> lettersToMorseLong(String letters, long dashTime, long dotTime, long separateTime, long lettersSeparateTime, long sectionTime){
		String allowPattern = "[^a-zA-Z0-9]+";
		Pattern pattern = Pattern.compile(allowPattern);
		Matcher matcher = pattern.matcher(letters);
		if(matcher.find()) new Exception("only allow letters or digits.");
		
		letters = letters.toUpperCase(Locale.getDefault());
		List<long[]> result = new ArrayList<long[]>();
		List<Character> morseString = lettersToMorseString(letters);
		
		int length = morseString.size();
	    for(int i = 0; i < length; i++){
	    	if(morseString.get(i) == '-'){
	    		result.add(new long[]{0, lettersSeparateTime});
	    	}else{
	    		if(i != 0){
	    			result.add(new long[]{0, separateTime});
	    		}
	    		
	    		if(morseString.get(i) == '0'){
		    		result.add(new long[]{1, dotTime});
		    	}else if(morseString.get(i) == '1'){
		    		result.add(new long[]{1, dashTime});
		    		if(i + 1 < length && morseString.get(i + 1) == '1'){
		    			result.add(new long[]{0, dashTime / 2});
		    		}
		    	}
	    	}
	    }
	    
	    result.add(new long[]{0, sectionTime});
		
		return result;
	}
}
