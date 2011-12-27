package com.greylurk.hellotv;

import com.greylurk.hellotv.services.ImageService;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PictureFragment extends Fragment {
	private Handler mTimerHandler = new Handler();
	private ImageService mImageService;
	private static long DELAY = 1000*30;
	private ImageView mImageView;
	
	private Runnable mUpdatePictureTask = new Runnable() {
		public void run() {
			updateImage();
			long millis = SystemClock.elapsedRealtime();
			mTimerHandler.postAtTime( this, millis + DELAY );
		}
	};
	
	public PictureFragment() {
	}
	
	public void onCreate( Bundle savedInstanceState ) {
		super.onActivityCreated(savedInstanceState);
    	mImageService = new ImageService();
	}

	private void updateImage() {
		if( mImageView != null && mImageService != null ) {
			mImageView.setImageURI( mImageService.getRandomImageURI() );
		}
	}
	
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
    	View v = inflater.inflate( R.layout.details , container, false);
    	mImageView = (ImageView)v.findViewById( R.id.image );
    	mImageView.setImageResource( R.drawable.scorpion_in_sand );
    	mTimerHandler.removeCallbacks(mUpdatePictureTask);
    	mTimerHandler.postDelayed( mUpdatePictureTask, DELAY);
    	return v;
    }
    
    @Override
    public void onPause() {
    	
    }

}
