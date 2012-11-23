package com.gmwork.casual.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class Utility {
	private static Handler sMainThreadHandler;
	/**
	 * @return Filename of a content of {@code contentUri}. If the provider
	 *         doesn't provide the filename, returns the last path segment of
	 *         the URI.
	 */
	public static String getContentFileName(Context context, Uri contentUri) {
		String name = null;
		if (name == null) {
			name = contentUri.getLastPathSegment();
		}
		return name;
	}
	
    /**
     * A thread safe way to show a Toast.  Can be called from any thread.
     *
     * @param context context
     * @param resId Resource ID of the message string.
     */
    public static void showToast(Context context, int resId) {
        showToast(context, context.getResources().getString(resId));
    }

    /**
     * A thread safe way to show a Toast.  Can be called from any thread.
     *
     * @param context context
     * @param message Message to show.
     */
    public static void showToast(final Context context, final String message) {
        getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    /**
     * @return a {@link Handler} tied to the main thread.
     */
    public static Handler getMainThreadHandler() {
        if (sMainThreadHandler == null) {
            // No need to synchronize -- it's okay to create an extra Handler, which will be used
            // only once and then thrown away.
            sMainThreadHandler = new Handler(Looper.getMainLooper());
        }
        return sMainThreadHandler;
    }
}
