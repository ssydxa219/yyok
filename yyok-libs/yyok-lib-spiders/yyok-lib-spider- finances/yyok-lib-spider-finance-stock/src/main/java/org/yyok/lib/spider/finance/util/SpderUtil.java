package org.yyok.lib.spider.finance.util;

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

/**
 * Hello world!Quote eastmoney
 *
 */
public class SpderUtil {
	private static final Log log = LogFactory.getLog(SpderUtil.class);

	// 获取html
	public static String getHTMLResourceByUrl(String url, String encoding) {
		StringBuffer sb = new StringBuffer();
		URL urlObj = null;
		URLConnection openConnection = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			urlObj = new URL(url);
			openConnection = urlObj.openConnection();
			isr = new InputStreamReader(openConnection.getInputStream(), encoding);
			// 建立文件缓冲流
			br = new BufferedReader(isr);
			// 建立临时文件
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp + "\n");
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.error("error message", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("error message", e);
		} finally {
			try {
				if (isr != null) {
					isr.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("error message", e);
			}
		}
		return sb.toString();
	}

	public static List links(String html, String encoding, String title) {
		Document parse = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		// 解析html，按照什么编码进行解析html
		parse = Jsoup.parse(html, encoding);
		Elements elementByTag_a = parse.getElementsByTag("a");
		int num = 1;
		for (Element element : elementByTag_a) {
			Map<String, String> map = new HashMap<String, String>();
			// 获图片
			// String imgSrc = element.getElementsByTag("a").attr("src");
			// 获取title
			String url = element.getElementsByTag("a").attr("href");
			// 获取描述信息
			String desc = element.getElementsByTag("a").text();
			// map.put("imgSrc", imgSrc);
			map.put("id", String.valueOf(num++));
			map.put("url", url);
			map.put("desc", desc);
			map.put("from", title);
			list.add(map);
		}
		return list;
	}

	public Document getResultPage(String url, String keyword) throws UnsupportedEncodingException {
		Document doc = null;

		// multipart/form-data 编码类型转换，必须进行转换,不然会导致POST里的keyword乱码
		// Multipart/form-data code type conversion must be converted, otherwise it will
		// cause keyword confusion in POST.
		keyword = URLEncoder.encode(keyword, "gbk");

		try {

			// 获取主页
			// Get index page
			Response resp = Jsoup.connect(url).method(Method.GET).execute();
			doc = resp.parse();

			// 获取查询结果页的跳转链接
			// Get query results jump page link
			String actionPath = doc.select("form").attr("action");

			Connection con = Jsoup.connect(actionPath).data("keyword", keyword).userAgent("Mozilla")
					.cookies(resp.cookies()).header("Accept-Language", "zh-CN,zh;q=0.9").timeout(300000);
			// 得到查询结果页面
			// Get query results page
			doc = con.method(Method.POST).execute().parse();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public void getResult(String url, String keyword, String dir, String fileName) {

		Document doc = null;
		File htmlPath = null;
		File txtPath = null;
		String htmlFilePath = dir + fileName + ".htm";
		String txtFilePath = dir + fileName + "2.txt";
		txtPath = new File(txtFilePath);
		htmlPath = new File(htmlFilePath);
		Map map = null;
		String printSrc = "";

		try {
			// 本地如果有html文件则解析该文件,打印内容并储存一个txt文件
			// If there is a HTML file in the local area, parse the file, print the contents
			// and store a TXT file.
			if (!txtPath.exists() && htmlPath.exists()) {

				doc = Jsoup.parse(htmlPath, "utf-8");

				if (!doc.children().isEmpty())
					System.out.println("File not empty");

				map = Screen51Jobs(doc);
				printSrc = printScreen(map);
				saveFile(printSrc, txtFilePath);
				System.out.println(printSrc);

				// 如果本地有html和txt文件则读取txt文件内容，否则抛出IOException
				// If you have HTML and txt files locally, you can read the contents of the txt
				// file, otherwise throw IOException.
			} else if (txtPath.exists() && htmlPath.exists()) {
				System.out.println("File not empty");
				printSrc = printScreen(txtPath);
				System.out.println(printSrc);
			} else
				throw new IOException("NOT HTML FILE");

		} catch (IOException e) { // 在catch块里执行爬虫并且把文件保存在本地,Execute crawler in catch block and save the file
									// locally.

			System.out.println("file not found");

			try {

				// 从网址上获取查询结果页面
				// Get query results page from web address
				doc = this.getResultPage(url, keyword);

				htmlPath.createNewFile();
				// 存储html文件
				// Save html file
				saveFile(doc.toString(), htmlFilePath);

				map = Screen51Jobs(doc);
				String printStr = printScreen(map);

				if (!htmlPath.exists())
					htmlPath.createNewFile();
				// 存储txt文件
				// Save txt file
				saveFile(printStr, txtFilePath);

				System.out.println(printSrc);

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	private String printScreen(File path) throws IOException {

		StringBuilder printSrc = new StringBuilder();
		InputStream in = new FileInputStream(path);
		BufferedInputStream bis = new BufferedInputStream(in);

		int len = 0;
		byte[] bytes = new byte[1024 * 8];
		while ((len = bis.read(bytes, 0, bytes.length)) != -1) {
			printSrc.append(new String(bytes, 0, bytes.length));
		}
		bis.close();

		return printSrc.toString();
	}

	private String printScreen(Map<?, ?> screen) throws IOException {

		StringBuilder sb = new StringBuilder();
		String p = "\r\n";
		sb.append(p + " KeyWord:" + screen.get("keyword") + p + p + " Total query data:" + screen.get("totalquerydata")
				+ p + p + " Recruitment info:");

		List list = (ArrayList) screen.get("recruitmentlist");

		for (Object o : list) {
			Map map = (HashMap<String, Object>) o;

			for (Object obj : map.entrySet()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) obj;
				sb.append(p + entry.getKey() + " == " + entry.getValue());
			}
			sb.append(p);
		}

		return sb.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<?, ?> Screen51Jobs(Document doc) {

		Map screen = new HashMap<String, Object>();

		Elements resultList = doc.select("div[class=dw_table]div[id=resultList]");
		Elements findKeyword = resultList.select("div[class=sbox]");
		Elements totalQueryData = resultList.select("div[class=rt]div:matchesOwn(^共)");
		Elements recruitmentInfo = resultList.select("div[class=el]");

		screen.put("keyword", findKeyword.text());
		screen.put("totalquerydata", totalQueryData.text());

		List recruitmentList = new ArrayList<Map<String, String>>();
		Map m = null;
		for (Element e : recruitmentInfo) {
			m = new HashMap<String, Object>();
			m.put("position", e.select("p[class~=^t1]").text());
			m.put("href", e.select("a").attr("href"));
			m.put("corporatename", e.select("a").text());
			m.put("address", e.select("span[class=t3]").text());
			m.put("salary", e.select("span[class=t4]").text());
			m.put("releasedate", e.select("span[class=t5]").text());
			recruitmentList.add(m);
		}
		screen.put("recruitmentlist", recruitmentList);

		return screen;
	}

	private void saveFile(String src, String path) throws IOException {

		// InputStream in = new FileInputStream(path);
		OutputStream out = new FileOutputStream(path);
		BufferedOutputStream bos = new BufferedOutputStream(out);

		byte[] bytes = src.getBytes("utf-8");

		bos.write(bytes, 0, bytes.length);
	}

}