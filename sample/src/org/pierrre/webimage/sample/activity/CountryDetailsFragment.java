package org.pierrre.webimage.sample.activity;

import org.pierrre.webimage.WebImageView;
import org.pierrre.webimage.sample.R;
import org.pierrre.webimage.sample.data.Country;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class CountryDetailsFragment extends SherlockFragment implements ViewTreeObserver.OnGlobalLayoutListener {
	public static final String ARGUMENT_COUNTRY = "country";
	
	private Country country;
	
	private TextView name;
	private WebImageView flag;
	
	private int flagWidth;
	
	public static Bundle createArguments(Country country) {
		Bundle arguments = new Bundle();
		arguments.putParcelable(CountryDetailsFragment.ARGUMENT_COUNTRY, country);
		
		return arguments;
	}
	
	public static CountryDetailsFragment newInstance(Country country) {
		return CountryDetailsFragment.newInstance(CountryDetailsFragment.createArguments(country));
	}
	
	public static CountryDetailsFragment newInstance(Bundle arguments) {
		CountryDetailsFragment fragment = new CountryDetailsFragment();
		fragment.setArguments(arguments);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle arguments = this.getArguments();
		this.country = arguments.getParcelable(CountryDetailsFragment.ARGUMENT_COUNTRY);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_country_details, null);
		
		this.name = (TextView) view.findViewById(R.id.name);
		this.flag = (WebImageView) view.findViewById(R.id.flag);
		
		this.flagWidth = 0;
		
		view.getViewTreeObserver().addOnGlobalLayoutListener(this);
		
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		this.getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}
	
	public void refresh() {
		this.name.setText(this.country.getName());
		this.flag.setImageUrl(this.country.getFlagUrl(this.flagWidth));
	}
	
	@Override
	public void onGlobalLayout() {
		int newFlagWidth = this.flag.getWidth();
		
		if (newFlagWidth != this.flagWidth) {
			this.flagWidth = newFlagWidth;
			
			this.refresh();
		}
	}
}
