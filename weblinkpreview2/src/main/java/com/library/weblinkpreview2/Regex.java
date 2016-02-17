package com.library.weblinkpreview2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
	public static final String IMAGE_PATTERN = "(.+?)\\.(jpg|png|gif|bmp)$";
	public static final String TITLE_PATTERN = "<title(.*?)>(.*?)</title>";
	public static final String SCRIPT_PATTERN = "<script(.*?)>(.*?)</script>";
	public static final String METATAG_PATTERN = "<meta(.*?)>";
	public static final String METATAG_CONTENT_PATTERN = "content=\"(.*?)\"";

	public static String pregMatch(String content, String pattern, int index) {

		String match = "";
		Matcher matcher = Pattern.compile(pattern).matcher(content);

		while (matcher.find()) {
			match = matcher.group(index);
			break;
		}

		return TextCrawler.extendedTrim(match);
	}

	public static List<String> pregMatchAll(String content, String pattern,
			int index) {

		List<String> matches = new ArrayList<String>();
		Matcher matcher = Pattern.compile(pattern).matcher(content);

		while (matcher.find()) {
			matches.add(TextCrawler.extendedTrim(matcher.group(index)));
		}

		return matches;
	}
}
