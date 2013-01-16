package hams.apps.textom;

import hams.apps.textom.dummy.Book;
import hams.apps.textom.dummy.Book.BookItem;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Handler;
import android.os.Message;

public class BookListReader {

	private ArrayList<BookItem> mItem;

	public BookListReader() {
		mItem = new ArrayList<Book.BookItem>();
	}

	public void get(Handler handler, String xml) {

		Message msg = new Message();
		msg.what = 2;
		handler.sendMessage(msg);

		try {
			XmlPullParserFactory parserCreator = XmlPullParserFactory
					.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();

			parser.setInput(new ByteArrayInputStream(xml.getBytes()), "UTF-8");

			boolean tag_book = false;

			boolean tag_id = false;
			boolean tag_title = false;
			boolean tag_author = false;
			boolean tag_link = false;
			boolean tag_pop = false;

			int eventType = parser.getEventType();

			String id = "", title = "", author = "", link = "";
			int pop = 0;

			while (eventType != XmlPullParser.END_DOCUMENT) {

				String tag = parser.getName();

				switch (eventType) {

				case XmlPullParser.START_TAG:

					if (tag_book) {
						if (tag.equals("id")) {
							tag_id = true;
						}
						if (tag.equals("title")) {
							tag_title = true;
						}
						if (tag.equals("author")) {
							tag_author = true;
						}
						if (tag.equals("link")) {
							tag_link = true;
						}
						if (tag.equals("pop")) {
							tag_pop = true;
						}
					}

					if (tag.equals("book")) {
						tag_book = true;
					}

					break;

				case XmlPullParser.END_TAG:

					if (tag_book) {
						if (tag.equals("id")) {
							tag_id = false;
						}
						if (tag.equals("title")) {
							tag_title = false;
						}
						if (tag.equals("author")) {
							tag_author = false;
						}
						if (tag.equals("link")) {
							tag_link = false;
						}
						if (tag.equals("pop")) {
							tag_pop = false;
						}
					}

					if (tag.equals("book")) {
						mItem.add(new BookItem(id, link, title, author, pop));
						tag_book = false;
					}

					break;

				case XmlPullParser.TEXT:

					if (tag_book) {
						if (tag_id) {
							id = parser.getText();
						}
						if (tag_title) {
							title = parser.getText();
						}
						if (tag_author) {
							author = parser.getText();
						}
						if (tag_link) {
							link = parser.getText();
						}
						if (tag_pop) {
							pop = Integer.parseInt(parser.getText());
						}
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			msg = new Message();
			msg.what = 0;
			msg.obj = e;
			handler.sendMessage(msg);
		}

		msg = new Message();
		msg.what = 1;
		msg.obj = mItem;
		handler.sendMessage(msg);

	}
}
