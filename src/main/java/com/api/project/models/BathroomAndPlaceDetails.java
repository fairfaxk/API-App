package com.api.project.models;

import com.google.maps.model.PlaceDetails;

public class BathroomAndPlaceDetails {
	private BathroomDetails bathroomDetails;
	private PlaceDetails placeDetails;

	public BathroomDetails getBathroomDetails() {
		return bathroomDetails;
	}
	
	public void setBathroomDetails(BathroomDetails bathroomDetails) {
		this.bathroomDetails = bathroomDetails;
	}
	
	public PlaceDetails getPlaceDetails() {
		return placeDetails;
	}
	
	public void setPlaceDetails(PlaceDetails placeDetails) {
		this.placeDetails = placeDetails;
	}
}
