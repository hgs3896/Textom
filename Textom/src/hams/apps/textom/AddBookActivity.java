package hams.apps.textom;

import hams.apps.textom.dummy.BookParams;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class AddBookActivity extends Activity {

	private EditText title, author, content;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_book);
		title = (EditText) findViewById(R.id.et_title);
		author = (EditText) findViewById(R.id.et_author);
		content = (EditText) findViewById(R.id.et_content);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "책 만들기");
		menu.add(0, 1, 0, "취소");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			String arg1 = title.getText().toString();
			String arg2 = author.getText().toString();
			String arg3 = content.getText().toString();
			BookParams book = new BookParams(arg1, arg2, arg3);

			new HttpConnection(posthandler).post(
					"http://hgs3896.netai.net/books/addBook.php", book);
			break;
		case 1:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private Handler posthandler = new Handler() {

		public void handleMessage(Message message) {

			switch (message.what) {
			case HttpConnection.DID_START:
				// Start Post
				CToast.show(getApplicationContext(), "전송 중...");
				title.setVisibility(EditText.INVISIBLE);
				author.setVisibility(EditText.INVISIBLE);
				content.setVisibility(EditText.INVISIBLE);
				break;
			case HttpConnection.DID_SUCCEED:
				// Complete
				CToast.show(getApplicationContext(), "전송 성공");
				title.setVisibility(EditText.VISIBLE);
				author.setVisibility(EditText.VISIBLE);
				content.setVisibility(EditText.VISIBLE);
				finish();
				break;
			case HttpConnection.DID_ERROR:
				// Fail
				CToast.show(getApplicationContext(), "실패");
				title.setVisibility(EditText.VISIBLE);
				author.setVisibility(EditText.VISIBLE);
				content.setVisibility(EditText.VISIBLE);
				break;
			}
		}
	};
}
