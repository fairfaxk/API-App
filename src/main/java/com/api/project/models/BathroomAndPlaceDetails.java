package com.api.project.models;

import com.google.maps.model.PlaceDetails;

public class BathroomAndPlaceDetails {
	private BathroomDetails bathroom_details;
	private PlaceDetails place_details;

	public BathroomDetails getBathroomDetails() {
		return bathroom_details;
	}
	
	public void setBathroomDetails(BathroomDetails bathroomDetails) {
		this.bathroom_details = bathroomDetails;
	}
	
	public PlaceDetails getPlaceDetails() {
		return place_details;
	}
	
	public void setPlaceDetails(PlaceDetails placeDetails) {
		this.place_details = placeDetails;
	}
}
