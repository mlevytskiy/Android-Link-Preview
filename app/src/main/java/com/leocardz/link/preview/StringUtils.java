package com.leocardz.link.preview;

/**
 * Created by max on 04.02.16.
 */
public class StringUtils {

    public static boolean hasEmail(String text) {
        return text.contains("@");
    }

    /**
     * This method help get correct link, when link started with "/" or "//"
     */
    public static String fixIfLinkPartial(String link, String mainUrl) {
        final String SLASH = "/";
        final String DOUBLE_SLASH = "//";

        if (link.startsWith(DOUBLE_SLASH)) {
            link = "http://" + link.substring(2);
        } else if (link.startsWith(SLASH)) {
            link = mainUrl + link.substring(1);
        }

        return link;
    }

}
