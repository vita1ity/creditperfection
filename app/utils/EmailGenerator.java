package utils;

import java.util.Random;

public class EmailGenerator {

	public static String generate() {
		
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		
		for (int i = 0; i < 10; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		sb.append("@gmail.com");
		
		return sb.toString();
		
	}
	
}
