package hams.apps.textom;

import hams.apps.textom.dummy.Book;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.WindowManager;

public class ItemDetailActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_item_detail);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {

			Bundle arguments = new Bundle();

			String id = getIntent().getStringExtra(
					ItemDetailFragment.ARG_ITEM_ID);
			arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);

			String title = Book.ITEM_MAP.get(id).title;
			String author = Book.ITEM_MAP.get(id).author;

			actionbar.setTitle(getString(R.string.title_item_detail) + " - "
					+ title + "(" + author + ")");

			actionbar.setDisplayShowTitleEnabled(true);
			ItemDetailFragment fragment = new ItemDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.item_detail_container, fragment).commit();

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
