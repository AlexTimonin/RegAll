package com.regall.old.network.geocode.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Result implements Serializable {

	private static final long serialVersionUID = -2999896131719596393L;

	@SerializedName("address_components")
	private ArrayList<AddressComponent> mAddressComponents = new ArrayList<AddressComponent>();
	
	@SerializedName("formatted_address")
	private String mFormattedAddress;
	
	@SerializedName("geometry")
	private Geometry mGeometry;
	
	@SerializedName("partial_match")
	private Boolean mPartialMatch;
	
	@SerializedName("types")
	private ArrayList<String> mTypes = new ArrayList<String>();

	public List<AddressComponent> getAddressComponents() {
		return mAddressComponents;
	}

	public void setAddressComponents(ArrayList<AddressComponent> addressComponents) {
		this.mAddressComponents = addressComponents;
	}

	public String getFormattedAddress() {
		return mFormattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.mFormattedAddress = formattedAddress;
	}

	public Geometry getGeometry() {
		return mGeometry;
	}

	public void setGeometry(Geometry geometry) {
		this.mGeometry = geometry;
	}

	public Boolean getPartialMatch() {
		return mPartialMatch;
	}

	public void setPartialMatch(Boolean partialMatch) {
		this.mPartialMatch = partialMatch;
	}

	public List<String> getTypes() {
		return mTypes;
	}

	public void setTypes(ArrayList<String> types) {
		this.mTypes = types;
	}
}
