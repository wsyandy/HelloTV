package com.greylurk.hellotv;

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
    	Uri u;
    	ImageView image = (ImageView)v.findViewById( R.id.image );
    	try {
    		u = Uri.parse( savedInstanceState.getString( "src" ) );
	    	image.setImageURI( u );
    	} catch ( NullPointerException e ) {
    		image.setImageResource( R.drawable.scorpion_in_sand );
    	}
    	return v;
    }
    
    @Override
    public void onPause() {
    	
    }

}
