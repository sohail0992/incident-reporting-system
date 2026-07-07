package com.msohailse.app.incident.model;

public class StringNormalizer {

	public boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}

	// we start from empty string builder and
	// if we found white space in original value we add one and mark lastWasSpace
	// true
	// if we found another whitespace we don't append and keep the lastWasSpace true
	// we keep ignoring the white space if lastWasSpace = true until we get actual
	// char
	public String trimAllSpaces(String value) {
		StringBuilder sb = new StringBuilder();
		boolean lastWasSpace = false;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (Character.isWhitespace(c)) {
				if (!lastWasSpace && sb.length() > 0)
					sb.append(' ');
				lastWasSpace = true;
			} else {
				sb.append(c);
				lastWasSpace = false;
			}
		}
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ' ')
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

}
