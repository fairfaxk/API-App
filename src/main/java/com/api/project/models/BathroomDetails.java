package com.api.project.models;

public class BathroomDetails {
	private String place_id;
	private Bathroom mens_room;
	private Bathroom womens_room;
	private Bathroom gender_neutral;

	public String getPlaceId() {
		return place_id;
	}

	public void setPlaceId(String place_id) {
		this.place_id = place_id;
	}

	public Bathroom getMensRoom() {
		return mens_room;
	}

	public void setMensRoom(Bathroom mens_room) {
		this.mens_room = mens_room;
	}

	public Bathroom getWomensRoom() {
		return womens_room;
	}

	public void setWomensRoom(Bathroom womens_room) {
		this.womens_room = womens_room;
	}

	public Bathroom getGenderNeutral() {
		return gender_neutral;
	}

	public void setGenderNeutral(Bathroom gender_neutral) {
		this.gender_neutral = gender_neutral;
	}
}
