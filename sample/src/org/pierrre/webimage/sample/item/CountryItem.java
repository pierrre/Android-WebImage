package org.pierrre.webimage.sample.item;

import org.pierrre.adapteritem.AdapterItem;
import org.pierrre.webimage.WebImageView;
import org.pierrre.webimage.sample.R;
import org.pierrre.webimage.sample.data.Country;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class CountryItem extends AdapterItem {
	private TextView name;
	private WebImageView flag;
	
	public CountryItem(Context context) {
		super(context, R.layout.item_country);
		
		View view = this.getView();
		this.name = (TextView) view.findViewById(R.id.name);
		this.flag = (WebImageView) view.findViewById(R.id.flag);
	}
	
	public void update(Country country) {
		this.name.setText(country.getName());
		this.flag.setImageUrl(country.getFlagUrl(this.getContext().getResources().getDimensionPixelSize(R.dimen.item_country_flag)));
	}
	
	@Override
	public void onViewMovedToScrapHeap() {
		super.onViewMovedToScrapHeap();
		
		this.flag.setImageUrl(null);
	}
}
