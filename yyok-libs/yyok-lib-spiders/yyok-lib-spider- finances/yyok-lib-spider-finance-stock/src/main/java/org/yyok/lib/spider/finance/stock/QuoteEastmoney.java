package org.yyok.lib.spider.finance.stock;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.yyok.lib.spider.finance.util.SpderUtil;

/**
 * Hello world!Quote eastmoney
 *
 */
public class QuoteEastmoney {
	private static final Log log = LogFactory.getLog(QuoteEastmoney.class);

	public static void main(String[] args) {
		String url = "http://quote.eastmoney.com/";
		String encoding = "GBK";
		String html = SpderUtil.getHTMLResourceByUrl(url, encoding);
		List<Map<String, String>> linklist = SpderUtil.links(html, encoding, "Quote estmoney");
		String linkstr = linklist.toString();
		for (Map<String, String> linkMap : linklist) {
			System.out.println(linkMap);
			for (Map.Entry<String, String> entry : linkMap.entrySet()) {
				if (entry.getKey().equals("url") || entry.getKey() != null || !entry.getKey().equals("")) {
					String url2 = entry.getValue();
					String html2 = SpderUtil.getHTMLResourceByUrl(url2, encoding);
					List<Map<String, String>> linklist2 = SpderUtil.links(html2, encoding, "Quote estmoney1");
					for (Map<String, String> linkMap2 : linklist2) {
						System.out.println( " "+linkMap2);
						if (entry.getKey().equals("url") || entry.getKey() != null || !entry.getKey().equals("")) {
							String url3 = entry.setValue("url");
							String html3 = SpderUtil.getHTMLResourceByUrl(url3, encoding);
							List<Map<String, String>> linklist3 = SpderUtil.links(html3, encoding, "Quote estmoney2");
							for (Map<String, String> linkMap3 : linklist3) {
								System.out.println(linkMap3);
								if (entry.getKey().equals("url") || entry.getKey() != null || !entry.getKey().equals("")) {
									String url4 = entry.setValue("url");
									String html4 = SpderUtil.getHTMLResourceByUrl(url3, encoding);
									List<Map<String, String>> linklist4 = SpderUtil.links(html4, encoding, "Quote estmoney4");
								}
							}
						}
					}
				}
			}

		}
	}

}