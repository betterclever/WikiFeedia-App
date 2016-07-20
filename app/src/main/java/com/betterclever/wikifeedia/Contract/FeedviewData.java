package com.betterclever.wikifeedia.Contract;

/**
 * Created by Pranjal Paliwal on 7/18/2016.
 */
public class FeedviewData {

	private String title,description,imageUrl;

	public FeedviewData(String title, String description, String imageUrl){
		this.title = title;
		this.description = description;
		this.imageUrl = imageUrl;
	}

	public String getTitle(){
		return title;
	}

	public String getDescription(){
		return description;
	}

	public String getImageUrl(){
		return imageUrl;
	}

}
