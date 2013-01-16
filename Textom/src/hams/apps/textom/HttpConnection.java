package hams.apps.textom;

import hams.apps.textom.dummy.BookParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class HttpConnection implements Runnable {
	public static final int DID_START = 0;
	public static final int DID_ERROR = 1;
	public static final int DID_SUCCEED = 2;

	private static final int GET = 0;
	private static final int POST = 1;
	private static final int PUT = 2;
	private static final int DELETE = 3;
	private static final int BITMAP = 4;

	private String url;
	private int method;
	private Handler handler;
	private String data;
	private BookParams params;

	private int imageId;

	private HttpClient httpClient;

	public HttpConnection() {
		this(new Handler());
	}

	public HttpConnection(Handler _handler) {
		handler = _handler;
	}

	public void create(int method, String url, String data) {
		this.method = method;
		this.url = url;
		this.data = data;
		ConnectionManager.getInstance().push(this);
	}

	public void get(String url) {
		create(GET, url, null);
	}

	public void post(String url, String data) {
		create(POST, url, data);
	}

	public void post(String url, BookParams book) {
		params = book;
		create(POST, url, data);
	}

	public void put(String url, String data) {
		create(PUT, url, data);
	}

	public void delete(String url) {
		create(DELETE, url, null);
	}

	public void bitmap(String url) {
		create(BITMAP, url, null);
	}

	public void bitmap(String url, int id) {
		create(BITMAP, url, null);
		imageId = id;
	}

	public void run() {
		handler.sendMessage(Message.obtain(handler, HttpConnection.DID_START));
		httpClient = new DefaultHttpClient();

		HttpConnectionParams
				.setConnectionTimeout(httpClient.getParams(), 40000);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 25000);

		try {
			HttpResponse response = null;
			StringEntity entity = null;

			switch (method) {
			case GET:
				response = httpClient.execute(new HttpGet(url));
				break;
			case POST:
				HttpPost httpPost = new HttpPost(url);
				httpPost.setHeader("Connection", "Keep-Alive");
				httpPost.setHeader("Accept-Charset", "UTF-8");

				try {
					// Body에 담아가야할 데이터를 만들고 body에 담기
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("title",
							params.title));
					nameValuePairs.add(new BasicNameValuePair("author",
							params.author));
					nameValuePairs.add(new BasicNameValuePair("content",
							params.content));
					UrlEncodedFormEntity ent = new UrlEncodedFormEntity(
							nameValuePairs, "UTF-8");
					httpPost.setEntity(ent);

					response = httpClient.execute(httpPost);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}

				break;
			case PUT:
				HttpPut httpPut = new HttpPut(url);
				entity = new StringEntity(data);
				entity.setContentEncoding("UTF-8");
				httpPut.setEntity(entity);
				response = httpClient.execute(httpPut);
				break;
			case DELETE:
				response = httpClient.execute(new HttpDelete(url));
				break;
			case BITMAP:
				response = httpClient.execute(new HttpGet(url));
				processBitmapEntity(response.getEntity());
				break;
			}
			if (method < BITMAP)
				processEntity(response.getEntity());
		} catch (Exception e) {
			Message msg = new Message();
			msg.what = HttpConnection.DID_ERROR;
			msg.arg1 = imageId;
			msg.obj = e;
			handler.sendMessage(msg);
		}
		ConnectionManager.getInstance().didComplete(this);
	}

	private void processEntity(HttpEntity entity) throws IllegalStateException,
			IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				entity.getContent()));
		String line, result = "";
		while ((line = br.readLine()) != null) {
			result += line + "\n";
		}
		Message message = Message.obtain(handler, DID_SUCCEED, result);
		handler.sendMessage(message);
	}

	private void processBitmapEntity(HttpEntity entity) throws IOException {
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		Bitmap bm = BitmapFactory.decodeStream(bufHttpEntity.getContent());
		Message msg = Message.obtain(handler, DID_SUCCEED, bm);
		msg.arg1 = imageId;
		handler.sendMessage(msg);

	}
}
