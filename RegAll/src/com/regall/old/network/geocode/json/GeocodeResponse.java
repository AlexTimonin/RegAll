package com.regall.old.network.geocode.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GeocodeResponse {

	@SerializedName("results")
	private List<Result> mResults = new ArrayList<Result>();

	@SerializedName("status")
	private String mStatus;

	public List<Result> getResults() {
		return mResults;
	}

	public void setResults(List<Result> results) {
		this.mResults = results;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		this.mStatus = status;
	}

}
