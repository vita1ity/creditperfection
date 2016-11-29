package utils;

import play.Logger;

public class StringConverter {

	public static String convertToDBForm(String str) {
		
		String lowerCased = str.toLowerCase();
		
		for (int i = 0; i < str.length(); i++) {
			if (Character.isUpperCase(str.charAt(i))) {
				String beginning = str.substring(0, i);
				beginning = beginning + "_" + lowerCased.charAt(i);
				String end = str.substring(i + 1);
				str = beginning + end;
			};
		}
		
		Logger.info("Converted String: " + str);
		
		return str;
	}
	
}
