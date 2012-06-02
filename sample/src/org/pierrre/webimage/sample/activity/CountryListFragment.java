package org.pierrre.webimage.sample.activity;

import java.util.List;

import org.pierrre.adapteritem.AdapterItem;
import org.pierrre.webimage.sample.data.Country;
import org.pierrre.webimage.sample.item.CountryItem;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class CountryListFragment extends SherlockListFragment {
	private List<Country> countries;
	
	private CountryListAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.countries = Country.getCountries(this.getResources());
		
		this.adapter = new CountryListAdapter();
		this.setListAdapter(this.adapter);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		this.getListView().setFastScrollEnabled(true);
		
		AdapterItem.setRecyclerLister(this.getListView());
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Country country = this.countries.get(position);
		
		((CountryActivity) this.getActivity()).selectCountry(country);
	}
	
	public void refresh() {
		this.adapter.notifyDataSetChanged();
	}
	
	private class CountryListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return CountryListFragment.this.countries.size();
		}
		
		@Override
		public Object getItem(int position) {
			return CountryListFragment.this.countries.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CountryItem countryItem;
			
			if (convertView != null) {
				countryItem = AdapterItem.getItem(convertView);
			} else {
				countryItem = new CountryItem(CountryListFragment.this.getActivity());
				convertView = countryItem.getView();
			}
			
			countryItem.update(CountryListFragment.this.countries.get(position));
			
			return convertView;
		}
	}
}