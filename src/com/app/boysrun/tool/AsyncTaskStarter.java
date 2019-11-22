package com.app.boysrun.tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.AsyncTask;
import android.os.Build;

/**
 * @Class Name 檔案或類別名稱: AsyncTaskStarter.java
 * @Description程式描述: 簡化AsyncTask因版本不同需使用不同程式碼的麻煩
 */
public class AsyncTaskStarter {
	private static ExecutorService LIMITED_TASK_EXECUTOR = Executors
			.newFixedThreadPool(10);

	public static <P> void startAsyncTask(AsyncTask<P, ?, ?> asyn, P... params) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			asyn.executeOnExecutor(LIMITED_TASK_EXECUTOR, params);
		} else {
			asyn.execute(params);
		}
	}

	public static void startAsyncTask(AsyncTask<Void, ?, ?> asyn) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			asyn.executeOnExecutor(LIMITED_TASK_EXECUTOR, (Void[]) null);
		} else {
			asyn.execute();
		}
	}
}