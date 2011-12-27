package com.greylurk.hellotv.services;

import java.util.HashSet;
import java.util.Set;

import android.net.Uri;

public class ImageService {
	private static String[] urls = {
		"http://background.tjeute.be/wp-content/uploads/2011/04/windows-7-wallpaper-19.jpg",
		"http://background.tjeute.be/wp-content/uploads/2011/04/Fire-Flower-vectors-abstract-wallpapers-1920x1080.jpg",
		"http://background.tjeute.be/wp-content/uploads/2011/04/Space_Background_by_Igniuss.png",
		"http://www.wallpapersfame.com/view_image/657/13"
	};
	
	private Set<Uri> uris;
	private Uri lastUri;
	
	public ImageService() {
		this.uris = new HashSet<Uri>();
		for( String url: urls ) {
			uris.add( Uri.parse(url));
		}
	}

	public Uri getRandomImageURI() {
		Uri selectedUri;
		if( lastUri != null ) {
			uris.remove( lastUri );
			selectedUri = (Uri)(uris.toArray())[ (int) Math.floor(Math.random() * uris.size() )];
			uris.add( lastUri );
		} else {
			selectedUri = (Uri)(uris.toArray())[ (int) Math.floor(Math.random() * uris.size() )];
		}
		lastUri = selectedUri;
		return lastUri;
	}
	
}
