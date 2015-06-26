package alonedroid.com.nanitabe.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Copyright (c) 2015 Yahoo! JAPAN Corporation. All rights reserved.
 */
public class NtTextUtility {

    public static String encode(String query) {
        try {
            String encode = "UTF-8";
            String encodeStr = URLEncoder.encode(query, encode);
            encodeStr = encodeStr.replace("+", "%20");
            return encodeStr;
        } catch (UnsupportedEncodingException e) {
            return query;
        }
    }
}
