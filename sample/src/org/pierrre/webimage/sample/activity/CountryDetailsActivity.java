package org.pierrre.webimage.sample.activity;

import org.pierrre.webimage.sample.R;
import org.pierrre.webimage.sample.data.Country;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CountryDetailsActivity extends SherlockFragmentActivity {
	private CountryDetailsFragment detailsFragment;
	
	public static void open(Context context, Country country) {
		Intent intent = new Intent(context, CountryDetailsActivity.class);
		intent.putExtras(CountryDetailsFragment.createArguments(country));
		
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (!this.getResources().getBoolean(R.bool.activity_country_details_allow_display)) {
			this.finish();
			
			return;
		}
		
		this.detailsFragment = CountryDetailsFragment.newInstance(this.getIntent().getExtras());
		
		FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
		transaction.replace(android.R.id.content, this.detailsFragment);
		transaction.commit();
	}
}