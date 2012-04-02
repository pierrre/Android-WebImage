package org.pierrre.webimage.sample.activity;

import org.pierrre.webimage.WebImageManager;
import org.pierrre.webimage.sample.R;
import org.pierrre.webimage.sample.data.Country;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

public class CountryActivity extends SherlockFragmentActivity {
	private static final String INSTANCE_STATE_COUNTRY = "country";
	
	private WebImageManager webImageManager;
	
	private Country selectedCountry;
	
	private CountryListFragment listFragment;
	private CountryDetailsFragment detailsFragment;
	
	private boolean layoutDualPane;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_country);
		
		this.webImageManager = WebImageManager.getInstance(this);
		
		this.selectedCountry = null;
		
		this.listFragment = (CountryListFragment) this.getSupportFragmentManager().findFragmentById(R.id.list);
		this.detailsFragment = null;
		
		this.layoutDualPane = this.findViewById(R.id.details) != null;
		
		if (savedInstanceState != null) {
			Country country = savedInstanceState.getParcelable(CountryActivity.INSTANCE_STATE_COUNTRY);
			
			if (country != null) {
				
				if (this.layoutDualPane) {
					this.selectCountry(country);
				} else {
					this.selectedCountry = country;
				}
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if (this.selectedCountry != null) {
			outState.putParcelable(CountryActivity.INSTANCE_STATE_COUNTRY, this.selectedCountry);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getSupportMenuInflater().inflate(R.menu.country, menu);
		
		menu.findItem(R.id.memory_cache).setChecked(this.webImageManager.isMemoryCacheEnabled());
		menu.findItem(R.id.file_cache).setChecked(this.webImageManager.isFileCacheEnabled());
		
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.share_subject));
		shareIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.share_text, this.getString(R.string.website_url)));
		((ShareActionProvider) menu.findItem(R.id.share).getActionProvider()).setShareIntent(shareIntent);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.memory_cache: {
			this.webImageManager.setMemoryCacheEnabled(!item.isChecked());
			this.supportInvalidateOptionsMenu();
			
			this.refreshFragments();
			
			return true;
		}
		case R.id.file_cache: {
			this.webImageManager.setFileCacheEnabled(!item.isChecked());
			this.supportInvalidateOptionsMenu();
			
			this.refreshFragments();
			
			return true;
		}
		case R.id.clear_cache: {
			new ClearCacheAsyncTask().execute();
			
			return true;
		}
		case R.id.about: {
			Uri uri = Uri.parse(this.getString(R.string.website_url));
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			this.startActivity(intent);
			
			return true;
		}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void selectCountry(Country country) {
		this.selectedCountry = country;
		
		if (this.layoutDualPane) {
			this.detailsFragment = CountryDetailsFragment.newInstance(country);
			
			FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.details, this.detailsFragment);
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			transaction.commit();
		} else {
			CountryDetailsActivity.open(this, country);
		}
	}
	
	private void refreshFragments() {
		this.listFragment.refresh();
		
		if (this.layoutDualPane && this.detailsFragment != null) {
			this.detailsFragment.refresh();
		}
	}
	
	private class ClearCacheAsyncTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			this.progressDialog = ProgressDialog.show(CountryActivity.this, "", "", true, false);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			CountryActivity.this.webImageManager.clearCache();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			this.progressDialog.dismiss();
			
			CountryActivity.this.refreshFragments();
		}
	}
}
