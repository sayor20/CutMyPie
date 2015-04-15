package com.sayor.cutmypie;

public class FoodData {

	int ind;
	public int getInd() {
		return ind;
	}
	public void setInd(int ind) {
		this.ind = ind;
	}
	public String getFooddesc() {
		return fooddesc;
	}
	public void setFooddesc(String fooddesc) {
		this.fooddesc = fooddesc;
	}
	public String getFeedcap() {
		return feedcap;
	}
	public void setFeedcap(String feedcap) {
		this.feedcap = feedcap;
	}
	public String getTimeexp() {
		return timeexp;
	}
	public void setTimeexp(String timeexp) {
		this.timeexp = timeexp;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	  public String toString() {
	      return "fooddesc = " + fooddesc + ", feedcap = " + feedcap + ", timeexp = " + timeexp + ", lat = " + lat + ", lon = " + lon;
	    }
	String fooddesc;
	String feedcap;
	String timeexp;
	double lat;
	double lon;
	
}
