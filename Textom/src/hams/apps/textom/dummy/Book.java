package hams.apps.textom.dummy;

import hams.apps.textom.BookListReader;
import hams.apps.textom.HttpConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;

public class Book {

	private Handler _handler;

	public static final int STARTED = 0;
	public static final int FINISHED = 1;
	public static final int LOAD_ERROR = 2;

	public Book() {
		_handler = new Handler();
		new HttpConnection(listHandler)
				.get("http://hgs3896.netai.net/books/xml/list.xml");
	}

	public Book(Handler _handler) {
		this._handler = _handler;
		new HttpConnection(listHandler)
				.get("http://hgs3896.netai.net/books/xml/list.xml");
	}

	public static class BookItem {

		public String id;
		public String link;
		public String title;
		public String author;
		public int pop;

		public BookItem(String id, String link, String title, String author,
				int pop) {
			this.id = id;
			this.link = link;
			this.title = title;
			this.author = author;
			this.pop = pop;
		}

		@Override
		public String toString() {
			return title.concat(" - ").concat(author);
		}

	}

	public static List<BookItem> ITEMS = new ArrayList<BookItem>();
	public static Map<String, BookItem> ITEM_MAP = new HashMap<String, BookItem>();

	private static void addItem(BookItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	private static void setItems(ArrayList<BookItem> items) {
		ITEMS.clear();
		ITEM_MAP.clear();
		for (BookItem item : items) {
			addItem(item);
		}
	}

	private Handler listHandler = new Handler() {

		public void handleMessage(Message message) {

			switch (message.what) {
			case HttpConnection.DID_START:
				// Start Download XML
				break;
			case HttpConnection.DID_SUCCEED:
				// Download Complete
				String response = (String) message.obj;
				BookListReader listReader = new BookListReader();
				listReader.get(afterparseHandler, response);
				break;
			case HttpConnection.DID_ERROR:
				// Fail to Download XML
				Exception e = (Exception) message.obj;
				break;
			}
		}
	};
	private Handler afterparseHandler = new Handler() {

		public void handleMessage(Message message) {

			switch (message.what) {
			case 0:
				Exception e = (Exception) message.obj;
				_handler.sendEmptyMessage(LOAD_ERROR);
				break;
			case 1:
				if (message.obj instanceof ArrayList<?>) {
					@SuppressWarnings("unchecked")
					ArrayList<BookItem> items = (ArrayList<BookItem>) message.obj;
					setItems(items);
					_handler.sendEmptyMessage(FINISHED);
				} else {

				}
				break;
			case 2:
				// Start Parsing
				_handler.sendEmptyMessage(STARTED);
			}

		}
	};
}
