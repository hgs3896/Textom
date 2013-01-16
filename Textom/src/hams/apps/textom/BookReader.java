package hams.apps.textom;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BookReader {

	private Context ct;
	private LayoutParams matchAttrs, wrapAttrs;
	private LinearLayout ll, body;

	private ArrayList<ImageView> iv;

	public BookReader(Context ct) {
		this.ct = ct;
	}

	public void get(Handler handler, String xml) {

		matchAttrs = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		wrapAttrs = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		iv = new ArrayList<ImageView>();

		ll = new LinearLayout(ct);
		ll.setLayoutParams(matchAttrs);
		ll.setOrientation(LinearLayout.VERTICAL);

		body = new LinearLayout(ct);
		body.setLayoutParams(matchAttrs);
		body.setOrientation(LinearLayout.VERTICAL);
		body.setGravity(Gravity.LEFT);

		final Typeface typeFace = Typeface.createFromAsset(ct.getAssets(),
				"malgun.ttf");

		try {
			XmlPullParserFactory parserCreator = XmlPullParserFactory
					.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();

			parser.setInput(new ByteArrayInputStream(xml.getBytes()), "UTF-8");

			boolean start_tag = false;

			boolean tag_title = false;
			boolean tag_author = false;
			boolean tag_body = false;
			boolean tag_img = false;

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {

				String tag = parser.getName();

				switch (eventType) {

				case XmlPullParser.START_DOCUMENT:
					// Log.i("PARSING", "START document");
					break;

				case XmlPullParser.END_DOCUMENT:
					// Log.i("PARSING", "End document");
					break;

				case XmlPullParser.START_TAG:

					if (tag.equals("content"))
						start_tag = true;

					if (start_tag) {
						if (tag.equals("title")) {
							tag_title = true;
						}
						if (tag.equals("author")) {
							tag_author = true;
						}
						if (tag.equals("body")) {
							tag_body = true;
						}
						if (tag.equals("img")) {
							tag_img = true;
						}

					}

					break;

				case XmlPullParser.END_TAG:

					if (tag.equals("content"))
						start_tag = false;

					if (start_tag) {
						if (tag.equals("title")) {
							tag_title = false;
						}
						if (tag.equals("author")) {
							tag_author = false;
						}
						if (tag.equals("body")) {
							tag_body = false;
						}
						if (tag.equals("img")) {
							tag_img = false;
						}

					}

					break;

				case XmlPullParser.TEXT:

					TextView tv = new TextView(ct);
					tv.setLayoutParams(wrapAttrs);
					tv.setTypeface(typeFace);

					if (tag_title) {
						StringBuffer buff = new StringBuffer(
								ct.getString(R.string.book_name));
						buff.append(" : ");
						buff.append(parser.getText());
						buff.append("\n");
						tv.setText(buff.toString());
						tv.setTextSize(30f);
						ll.addView(tv);
					}
					if (tag_author) {
						StringBuffer buff = new StringBuffer(
								ct.getString(R.string.book_author));
						buff.append(" : ");
						buff.append(parser.getText());
						buff.append("\n");
						tv.setText(buff.toString());
						tv.setTextSize(20f);
						tv.setPadding(10, 10, 10, 10);
						ll.addView(tv);
					}
					if (tag_body) {

						if (tag_img) {
							ImageView img = new ImageView(ct);
							img.setLayoutParams(wrapAttrs);
							img.setX(body.getWidth() / 2.0f);
							img.setPivotX(body.getWidth() / 2.0f);
							img.setImageResource(R.drawable.book);
							img.setAdjustViewBounds(true);
							body.addView(img);
							String url = parser.getText().trim();
							new HttpConnection(imageHandler).bitmap(url,
									iv.size());
							iv.add(img);
						} else {
							tv.setText(parser.getText() + "\n");
							tv.setTextSize(20f);
							tv.setPadding(10, 10, 10, 10);
							body.addView(tv);
						}
					}

					break;
				}
				eventType = parser.next();
			}

		} catch (Exception e) {
			Message msg = new Message();
			msg.obj = e;
			handler.sendMessage(msg);
		}
		ll.addView(body);
		Message msg = new Message();
		msg.obj = ll;
		handler.sendMessage(msg);
	}

	// Image Downloader

	Handler imageHandler = new Handler() {

		public void handleMessage(Message message) {

			switch (message.what) {
			case HttpConnection.DID_START:
				break;
			case HttpConnection.DID_SUCCEED:
				Bitmap bitmap = (Bitmap) message.obj;

				if (bitmap != null) {
					iv.get(message.arg1).setImageBitmap(bitmap);
					iv.get(message.arg1).setOnClickListener(
							new OnClickListener() {

								@Override
								public void onClick(View v) {
								}
							});
				}
				break;

			case HttpConnection.DID_ERROR:
				Exception e = (Exception) message.obj;
				iv.get(message.arg1).setVisibility(ImageView.GONE);
				e.printStackTrace();
				break;

			}

		}
	};
}
