package hams.apps.textom;

import hams.apps.textom.dummy.Book;

import java.util.Random;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookAdapter extends BaseAdapter {

	public class ViewHolder {
		ImageView iv;
		TextView tv1, tv2, rank;
	}

	private LayoutInflater inflater;
	private Context ct;

	private final int[] img = { R.drawable.red, R.drawable.orange,
			R.drawable.yellow, R.drawable.green, R.drawable.skyblue,
			R.drawable.blue, R.drawable.purple, R.drawable.pink,
			R.drawable.brown, R.drawable.black };
	private final int[] layouts = { R.layout.book_item_layout,
			R.layout.book_item_layout2 };

	private int n;
	private int m;

	public BookAdapter(Context ct) {
		this.ct = ct;

		new Book(handler);

		inflater = LayoutInflater.from(ct);

		n = new Random().nextInt(img.length);
		m = new Random().nextInt(2);
	}

	@Override
	public int getCount() {
		return Book.ITEMS.size();
	}

	@Override
	public Object getItem(int position) {
		return Book.ITEMS.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder vh;

		if (convertView == null) {
			convertView = inflater.inflate(layouts[m], parent, false);
			vh = new ViewHolder();
			vh.iv = (ImageView) convertView.findViewById(R.id.book_image);
			vh.tv1 = (TextView) convertView.findViewById(R.id.book_title);
			vh.tv2 = (TextView) convertView.findViewById(R.id.book_author);
			vh.rank = (TextView) convertView.findViewById(R.id.book_ranking);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		try {
			vh.iv.setImageResource(img[(n + position) % img.length]);
			vh.tv1.setText(Book.ITEMS.get(position).title);
			vh.tv2.setText(Book.ITEMS.get(position).author);
			vh.rank.setText(Book.ITEMS.get(position).pop + "↑");
		} catch (Exception e) {
			vh.tv1.setText(ct.getString(R.string.unkwown));
			vh.tv2.setText(ct.getString(R.string.unkwown));
			vh.rank.setText("-");
		}

		return convertView;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Book.STARTED:
				break;
			case Book.FINISHED:
				notifyDataSetChanged();
				break;
			case Book.LOAD_ERROR:
				CToast.show(ct, ct.getString(R.string.fail_loading_booklist));
				break;
			}
		}
	};
}
