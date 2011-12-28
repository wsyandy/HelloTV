package com.greylurk.hellotv.services;

import java.util.ArrayList;
import java.util.List;

public class ImageService {
	private final static String[] URLS = {
		"http://background.tjeute.be/wp-content/uploads/2011/04/windows-7-wallpaper-19.jpg",
		"http://background.tjeute.be/wp-content/uploads/2011/04/Fire-Flower-vectors-abstract-wallpapers-1920x1080.jpg",
		"http://background.tjeute.be/wp-content/uploads/2011/04/Space_Background_by_Igniuss.png",
		"http://www.wallpapersfame.com/view_image/657/13"
	};
	
	private final List<String> uris;
	private String lastUri;
	
	public ImageService() {
		this.uris = new ArrayList<String>( URLS.length );
		for( String url: URLS ) {
			uris.add( url);
		}
	}

	public String getRandomImageURI() {
		String selectedUri;
		if( lastUri != null ) {
			uris.remove( lastUri );
			selectedUri = uris.get((int) Math.floor(Math.random() * uris.size() ));
			uris.add( lastUri );
		} else {
			selectedUri = uris.get((int) Math.floor(Math.random() * uris.size() ));
		}
		lastUri = selectedUri;
		return lastUri;
	}
	
}
