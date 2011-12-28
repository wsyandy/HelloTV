package com.greylurk.hellotv;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class AsyncImageDownloader {
	private static final String LOG_TAG = "AsyncImageDownloader";
	private BitmapDownloaderTask task;
	private static final int HARD_CACHE_CAPACITY = 10;
	
	private final HashMap<String, Bitmap> sHardBitmapCache = 
			new LinkedHashMap<String, Bitmap>( HARD_CACHE_CAPACITY / 2, 0.75f, true) {

		private static final long serialVersionUID = -6981916524340338378L;

		@Override
		protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
			if( size() > HARD_CACHE_CAPACITY ) {
				sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else {
				return false;
			}
		}
	};
	
	private final ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = 
			new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY /2 );
				
	public void download(String url, ImageView view ) {
		Bitmap bitmap = getBitmapFromCache( url );
		if( bitmap == null ) {
			forceDownload(url, view);
		} else {
			cancelPotentialDownload(url, view);
			view.setImageBitmap(bitmap);
		}
	}
	
	protected void clearCache() {
		sHardBitmapCache.clear();
		sSoftBitmapCache.clear();
	}
	
	private Bitmap getBitmapFromCache( String url ) {
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(url);
			if( bitmap != null ) {
				// Move to the front of the Cache
				sHardBitmapCache.remove(url);
				sHardBitmapCache.put(url, bitmap);
				return bitmap;
			}
		}
		SoftReference<Bitmap> bitmapRef = sSoftBitmapCache.get(url);
		if( bitmapRef != null ) {
			final Bitmap bitmap = bitmapRef.get();
			if( bitmap == null ) {
				sSoftBitmapCache.remove(url);
			}
			return bitmap;
		}
		return null;
	}
	
	private void forceDownload( String url, ImageView view ) {
		if( url ==  null ) {
			view.setImageDrawable( null );
			return;
		}
		if( cancelPotentialDownload(url, view ) ) {
			task = new BitmapDownloaderTask( view );
			DownloadedDrawable drawable = new DownloadedDrawable(task);
			view.setImageDrawable(drawable);
			view.setMinimumHeight(156);
			task.execute(url);
		}
	}

	
	Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if( statusCode != HttpStatus.SC_OK ) {
				Log.w( LOG_TAG, "Error " + statusCode + " while retrieving bitmap from " + url );
				return null;
			}
			
			final HttpEntity entity = response.getEntity();
			if( entity != null ) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					return BitmapFactory.decodeStream(inputStream);
				} finally {
					if( inputStream != null ) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (IOException e){
			getRequest.abort();
			Log.w(LOG_TAG, "I/O Error while retrieving bitmap from " + url, e);
		} catch ( IllegalStateException e ) {
			getRequest.abort();
			Log.w(LOG_TAG, "Invalid URL: " + url, e);
		} catch ( Exception e ) {
			getRequest.abort();
			Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
		} finally {
			if( client != null ) {
				client.close();
			}
		}
		return null;
	}

	/**
	 * Returns true if the download has been canceled, or if there were no 
	 * pending downloads to cancel.
	 * Returns false if the download in progress deals with the same
	 * url, in which case it's not stopped
	 * @param url
	 * @param view
	 * @return
	 */
	private static boolean cancelPotentialDownload( String url, ImageView view ) {
		BitmapDownloaderTask task = getBitmapDownloaderTask( view );
		if( task != null ) {
			String downloadUrl = task.url;
			if( downloadUrl == null || !downloadUrl.equals(url) ) {
				task.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}
	
	private static BitmapDownloaderTask getBitmapDownloaderTask( ImageView view ) {
		if( view != null ) {
			Drawable drawable = view.getDrawable();
			if( drawable instanceof DownloadedDrawable ) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}
	
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private final WeakReference<ImageView> viewRef;

		public BitmapDownloaderTask( ImageView view ) {
			this.viewRef = new WeakReference<ImageView>( view );
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			this.url = params[0];
			return downloadBitmap(this.url);
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap ) { 
			if( isCancelled() ) {
				bitmap = null;
			}
			
			addBitmapToCache( this.url, bitmap );
			
			if( this.viewRef != null ) { 
				ImageView view = this.viewRef.get();
				BitmapDownloaderTask task = getBitmapDownloaderTask( view );
				if( this == task ) {
					view.setImageBitmap( bitmap );
				}
			}
		}
	}
	
	static class DownloadedDrawable extends ColorDrawable {
		private final WeakReference<BitmapDownloaderTask> taskRef;
		
		public DownloadedDrawable( BitmapDownloaderTask task ) {
			super(Color.BLACK);
			this.taskRef = new WeakReference<BitmapDownloaderTask>(task);
		}
		
		public BitmapDownloaderTask getBitmapDownloaderTask() {
			if( this.taskRef != null ) {
				return this.taskRef.get();
			}
			return null;
		}
	}

	private void addBitmapToCache(String url, Bitmap bitmap) {
		if( bitmap != null ) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put( url , bitmap);
			}
		}
	}
}