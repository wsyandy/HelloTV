package com.greylurk.hellotv;

import com.greylurk.hellotv.services.ImageService;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PictureFragment extends Fragment {
	private static final long UPDATE_DELAY = 1000*30;
	private Handler mTimerHandler = new Handler();
	private final ImageService mImageService;
	private final AsyncImageDownloader mImageDownloader;
	private ImageView mImageView;
	
	private Runnable mUpdatePictureTask = new Runnable() {
		public void run() {
			updateImage();
			long millis = SystemClock.elapsedRealtime();
			mTimerHandler.postAtTime( this, millis + UPDATE_DELAY );
		}
	};
	
	public PictureFragment() {
    	mImageService = new ImageService();
    	mImageDownloader = new AsyncImageDownloader();
	}
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onActivityCreated(savedInstanceState);
	}

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
    	View v = inflater.inflate( R.layout.details , container, false);
    	mImageView = (ImageView)v.findViewById( R.id.image );
    	mImageView.setImageResource( R.drawable.scorpion_in_sand );
    	mTimerHandler.removeCallbacks(mUpdatePictureTask);
    	mTimerHandler.postDelayed( mUpdatePictureTask, UPDATE_DELAY);
    	return v;
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	mTimerHandler.removeCallbacks( mUpdatePictureTask );
    }

    @Override
    public void onResume() {
    	super.onResume();
    	mUpdatePictureTask.run();
    }
    
	private void updateImage() {
		if( mImageView != null && mImageService != null ) {
			final String imageUrl = mImageService.getRandomImageURI();
			mImageDownloader.download(imageUrl, mImageView);
		}
	}
    
}
