package hams.apps.textom;

import android.content.Context;
import android.widget.Toast;

public class CToast {
	static public void show(Context ct, String str) {
		Toast.makeText(ct, str, Toast.LENGTH_SHORT).show();
	}
	static public void show(Context ct, int resId) {
		Toast.makeText(ct, ct.getString(resId), Toast.LENGTH_SHORT).show();
	}
}
