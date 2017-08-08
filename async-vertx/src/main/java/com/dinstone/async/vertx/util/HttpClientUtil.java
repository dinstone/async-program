package com.dinstone.async.vertx.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

public class HttpClientUtil {

	public static final String DEF_CHATSET = "UTF-8";

	public static final int DEF_CONN_TIMEOUT = 3000;

	public static final int DEF_READ_TIMEOUT = 30000;

	public static String USERAGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

	/**
	 * 发送基于表单的GET/POST请求，响应为字符串。
	 *
	 * @param reqUrl
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param method
	 *            请求方法
	 * @return 网络请求字符串
	 * @throws Exception
	 */
	public static String netForm(String reqUrl, Map<String, String> params, String method) throws Exception {
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		String rs = null;
		try {
			if (method == null || method.equals("GET")) {
				reqUrl = reqUrl + "?" + urlencode(params);
			}
			URL url = new URL(reqUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (method == null || method.equals("GET")) {
				conn.setRequestMethod("GET");
			} else {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
			}
			conn.setRequestProperty("User-agent", USERAGENT);
			conn.setUseCaches(false);
			conn.setConnectTimeout(DEF_CONN_TIMEOUT);
			conn.setReadTimeout(DEF_READ_TIMEOUT);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			if (params != null && method.equals("POST")) {
				DataOutputStream out = new DataOutputStream(conn.getOutputStream());
				out.writeBytes(urlencode(params));
			}

			StringBuilder sb = new StringBuilder();
			String strRead = null;
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), DEF_CHATSET));
			while ((strRead = reader.readLine()) != null) {
				sb.append(strRead);
			}
			rs = sb.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rs;
	}

	// 将map型转为请求参数型
	public static String urlencode(Map<String, String> data) {
		if (data == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> i : data.entrySet()) {
			try {
				sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue(), "UTF-8")).append("&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
