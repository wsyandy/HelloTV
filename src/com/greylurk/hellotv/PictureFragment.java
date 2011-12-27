package com.greylurk.hellotv;

import android.R;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PictureFragment extends Fragment {
	String pictureSource;

	static PictureFragment newInstance( String pictureSource ) {
		PictureFragment f = new PictureFragment();
		
		Bundle args = new Bundle();
		args.putString( "src", pictureSource );
		f.setArguments(args);
		return f;
	}
	
	public void onCreate( Bundle savedInstanceState ) {
		super.onActivityCreated(savedInstanceState);
	}
	
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
    	View v = inflater.inflate( R.layout.details , container, false);
    	Uri u = Uri.parse( savedInstanceState.getString( "src" ) );
    	ImageView image = (ImageView)v.findViewById( R.id.image );
    	image.setImageURI( u );
    }
    
    @Override
    public void onPause() {
    	
    }

}
