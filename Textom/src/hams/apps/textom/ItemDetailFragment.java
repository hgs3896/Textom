package hams.apps.textom;

import hams.apps.textom.dummy.Book;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ItemDetailFragment extends Fragment {

	public static final String ARG_ITEM_ID = "item_id";

	private Context ct;
	private ScrollView sv;
	private ProgressDialog downloadDialog;

	Book.BookItem mItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.ct = getActivity();

		downloadDialog = new ProgressDialog(ct);
		downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			mItem = Book.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// View Loading
		View rootView = inflater.inflate(R.layout.fragment_item_detail,
				container, false);
		sv = (ScrollView) rootView.findViewById(R.id.detail_scrollview);
		// Available Book
		if (mItem != null) {
			// Download Book
			downloadBook(mItem.link);
		}

		return rootView;
	}

	public void downloadBook(String link) {
		// Download Books
		new HttpConnection(handler).get(link);
	}

	final Handler handler = new Handler() {

		public void handleMessage(Message message) {
			switch (message.what) {
			case HttpConnection.DID_START: {

				downloadDialog.setTitle(mItem.title + " : 다운로드 중");
				downloadDialog.setProgress(0);
				downloadDialog.show();

				progressHandler.sendEmptyMessage(0);

				break;
			}
			case HttpConnection.DID_SUCCEED: {
				String response = (String) message.obj;

				downloadDialog.dismiss();

				downloadDialog.setTitle(mItem.title + " : 로딩 중");
				downloadDialog.setProgress(0);
				downloadDialog.show();

				addReader();

				new BookReader(ct).get(loadHandler, response);

				progressHandler.sendEmptyMessage(0);

				break;
			}
			case HttpConnection.DID_ERROR: {
				downloadDialog.dismiss();

				Exception e = (Exception) message.obj;
				Log.d("HAMS", e.toString());

				CToast.show(ct, R.string.download_fail);
				break;
			}
			}
		}
	};

	Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			int progress = downloadDialog.getProgress();
			if (progress >= 0 && progress < 100) {
				downloadDialog.setProgress(progress + 4);
				sendEmptyMessageDelayed(0, 3);
			} else {
				downloadDialog.setProgress(100);
			}
		}
	};

	Handler loadHandler = new Handler() {
		public void handleMessage(Message msg) {
			LinearLayout ll = (LinearLayout) msg.obj;
			sv.addView(ll);
			downloadDialog.dismiss();
		}
	};

	private void addReader() {
		TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = mTelephonyMgr.getDeviceId();
		final String url = "http://hgs3896.netai.net/books/addReader.php?bookId"
				+ mItem.id + "&devId=" + imei;
		new HttpConnection().get(url);

		new Thread() {
			public void run() {
				URL urls;
				try {
					urls = new URL(url);
					URLConnection con = urls.openConnection();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			};
		}.start();

	}
}
