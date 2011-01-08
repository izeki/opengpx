package com.bcaching.georg;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.opengpx.lib.geocache.FieldNote;

public class Communication
{
	private String	mUsername;
	private String	mHashword;
	private String	mBaseUrl;
	private int			mTimeout;

	public Communication(String baseUrl)
	{
		mBaseUrl = baseUrl;
	}

	public void setBaseUrl(String baseUrl)
	{
		mBaseUrl = baseUrl;
	}

	public void setLoginCredentials(String username, String password, boolean validate) throws Exception
	{

		mUsername = username;
		mHashword = encodeHashword(username, password);
		if (validate)
		{
			// attempt to login at server
			// failure will throw an exception
			sendRequest("a=login");
		}
	}

	public void setTimeout(int timeout)
	{
		mTimeout = timeout;
	}

	public String encodeHashword(String username, String password) throws Exception
	{

		return encodeMd5Base64(password + username);
	}

	public String encodeQueryString(String username, String hashword, String params) throws Exception
	{

		if (username == null) throw new IllegalArgumentException("username is required.");
		if (hashword == null) throw new IllegalArgumentException("hashword is required.");
		if (params == null || params.length() == 0) throw new IllegalArgumentException("params are required.");

		StringBuffer sb = new StringBuffer();
		sb.append("u=");
		sb.append(URLEncoder.encode(username));
		sb.append("&");
		sb.append(params);
		sb.append("&time=");
		java.util.Date date = java.util.Calendar.getInstance().getTime();
		sb.append(date.getTime());
		String signature = encodeMd5Base64(sb.toString() + hashword);
		sb.append("&sig=");
		sb.append(URLEncoder.encode(signature));
		return sb.toString();
	}

	public InputStream sendRequest(Hashtable<String, String> params) throws Exception
	{

		if (params == null || params.size() == 0) throw new IllegalArgumentException("params are required.");
		if (!params.containsKey("a")) throw new IllegalArgumentException("params must include an action (key=a)");

		StringBuffer sb = new StringBuffer();
		Enumeration<String> keys = params.keys();
		while (keys.hasMoreElements())
		{
			if (sb.length() > 0) sb.append('&');
			String k = keys.nextElement();
			sb.append(k);
			sb.append('=');
			sb.append(URLEncoder.encode(params.get(k)));
		}

		return sendRequest(sb.toString());
	}

	public InputStream sendRequest(String query) throws Exception
	{
		if (query == null || query.length() == 0) throw new IllegalArgumentException("query is required");

		final URL url = getURL(mUsername, mHashword, query);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(mTimeout);
		conn.setConnectTimeout(mTimeout);
		conn.addRequestProperty("Accept-encoding", "gzip");
		int responseCode = conn.getResponseCode();
		InputStream in = conn.getInputStream();
		if (conn.getContentEncoding().equalsIgnoreCase("gzip"))
		{
			in = new java.util.zip.GZIPInputStream(in);
		}
		if (responseCode == HttpURLConnection.HTTP_OK)
		{
			return in;
		}
		else
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}

			throw new Exception(sb.toString());
		}
	}

	public String sendFieldNotes(List<FieldNote> notes) throws Exception
	{
		final URL url = getFieldNotesURL(mUsername, mHashword, "a=add");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(mTimeout);
		conn.setConnectTimeout(mTimeout);

		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		
		StringBuilder p = new StringBuilder();
		
		for (FieldNote note : notes)
		{
			p.append(note.gcId);
			p.append(",");
			p.append(note.getDateAsISOString());
			p.append(",");
			p.append(note.logType.text);
			p.append(",\"");
			p.append(note.logText);
			p.append("\"\n");
		}

		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.print(p.toString());
		out.close();		

		int responseCode = conn.getResponseCode();
		
		if (responseCode == HttpURLConnection.HTTP_OK)
		{
			StringBuilder b = new StringBuilder();
			
			InputStream in = conn.getInputStream();
			byte[] buf = new byte[512];
			int count = 0;
			while ((count = in.read(buf, 0, buf.length)) != -1)
			{
				String tmp = new String(buf, 0, count);
				b.append(tmp);
			}
			
			return b.toString(); 
		}
		else 
			return null;
	}
	
	private URL getURL(String username, String hashword, String params) throws Exception
	{
		return new URL(mBaseUrl + "/q.ashx?" + encodeQueryString(username, hashword, params));
	}
	
	private URL getFieldNotesURL(String username, String hashword, String params) throws Exception
	{
		return new URL(mBaseUrl + "/notes.ashx?" + encodeQueryString(username, hashword, params));
	}

	public String encodeMd5Base64(String s) throws Exception
	{

		byte[] buf = s.getBytes();
		java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		md.update(buf, 0, buf.length);
		buf = new byte[16];
		md.digest(buf, 0, buf.length);
		return base64Encode(buf);
	}

	private static char[]	map1	= new char[64];
	static
	{
		int i = 0;
		for (char c = 'A'; c <= 'Z'; c++)
		{
			map1[i++] = c;
		}
		for (char c = 'a'; c <= 'z'; c++)
		{
			map1[i++] = c;
		}
		for (char c = '0'; c <= '9'; c++)
		{
			map1[i++] = c;
		}
		map1[i++] = '+';
		map1[i++] = '/';
	}

	public static String base64Encode(byte[] in)
	{
		int iLen = in.length;
		int oDataLen = (iLen * 4 + 2) / 3;// output length without padding
		int oLen = ((iLen + 2) / 3) * 4;// output length including padding
		char[] out = new char[oLen];
		int ip = 0;
		int op = 0;
		int i0, i1, i2, o0, o1, o2, o3;
		while (ip < iLen)
		{
			i0 = in[ip++] & 0xff;
			i1 = ip < iLen ? in[ip++] & 0xff : 0;
			i2 = ip < iLen ? in[ip++] & 0xff : 0;
			o0 = i0 >>> 2;
			o1 = ((i0 & 3) << 4) | (i1 >>> 4);
			o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
			o3 = i2 & 0x3F;
			out[op++] = map1[o0];
			out[op++] = map1[o1];
			out[op] = op < oDataLen ? map1[o2] : '=';
			op++;
			out[op] = op < oDataLen ? map1[o3] : '=';
			op++;
		}
		return new String(out);
	}
}
