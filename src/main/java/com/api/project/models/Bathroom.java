package com.api.project.models;

import java.util.List;

public class Bathroom {
	private boolean exists;
	private boolean handicap;
	private List<Double> ratings;
	private List<Code> codes;

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public boolean isHandicap() {
		return handicap;
	}

	public void setHandicap(boolean handicap) {
		this.handicap = handicap;
	}

	public List<Double> getRatings() {
		return ratings;
	}

	public void setRatings(List<Double> ratings) {
		this.ratings = ratings;
	}

	public List<Code> getCodes() {
		return codes;
	}

	public void setCodes(List<Code> codes) {
		this.codes = codes;
	}
}
