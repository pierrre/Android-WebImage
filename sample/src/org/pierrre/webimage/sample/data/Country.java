package org.pierrre.webimage.sample.data;

import java.util.ArrayList;
import java.util.List;

import org.pierrre.webimage.sample.R;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

public class Country implements Parcelable {
	public static final ParcelableCreator CREATOR;
	
	private static List<Country> countries = null;
	
	private String name;
	private String flagUrl;
	
	static {
		CREATOR = new ParcelableCreator();
	}
	
	public static List<Country> getCountries(Resources resources) {
		if (Country.countries == null) {
			Country.countries = Country.initCountries(resources);
		}
		
		return Country.countries;
	}
	
	private static List<Country> initCountries(Resources resources) {
		List<Country> countries = new ArrayList<Country>();
		
		String[] names = resources.getStringArray(R.array.country_names);
		String[] flagUrls = resources.getStringArray(R.array.country_flag_urls);
		
		int size = Math.min(names.length, flagUrls.length);
		
		for (int i = 0; i < size; i++) {
			countries.add(new Country(names[i], flagUrls[i]));
		}
		
		return countries;
	}
	
	public Country(String name, String flagUrl) {
		this.name = name;
		this.flagUrl = flagUrl;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getFlagUrl(int width) {
		return this.flagUrl.replaceFirst("%width%", Integer.toString(width));
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.flagUrl);
	}
	
	private static class ParcelableCreator implements Parcelable.Creator<Country> {
		@Override
		public Country createFromParcel(Parcel source) {
			String name = source.readString();
			String flagUrl = source.readString();
			
			Country country = new Country(name, flagUrl);
			
			return country;
		}
		
		@Override
		public Country[] newArray(int size) {
			return new Country[size];
		}
	}
}
