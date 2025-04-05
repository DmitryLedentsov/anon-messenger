package com.dimka228.messenger.utils;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class HtmlTagsRemover {

	public static String clean(String unsafe, List<String> tags) {
		Whitelist whitelist = Whitelist.none();

		whitelist.addTags(tags.toArray(String[]::new));

		String safe = Jsoup.clean(unsafe, whitelist);
		return StringEscapeUtils.unescapeXml(safe);
	}

	public static String clean(String unsafe) {
		Whitelist whitelist = Whitelist.relaxed();
		String safe = Jsoup.clean(unsafe, whitelist);
		return StringEscapeUtils.unescapeXml(safe);
	}

}
