package com.betterclever.wikifeedia.adapters;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.betterclever.wikifeedia.Contract.FeedviewData;
import com.betterclever.wikifeedia.R;
import com.betterclever.wikifeedia.WikifeediaApplication;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Pranjal Paliwal on 7/18/2016.
 */
public class FeedviewAdapter extends RecyclerView.Adapter<FeedviewAdapter.ViewHolder> {

	private ArrayList<FeedviewData> data;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView title, description;
		public ImageView articleThumbnail;

		// each data item is just a string in this case
		public ViewHolder(View v) {
			super(v);
			title = (TextView) itemView.findViewById(R.id.title);
			description = (TextView) itemView.findViewById(R.id.description);
			articleThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
		}
	}

	public FeedviewAdapter(ArrayList<FeedviewData> fetchedData) {
		this.data = fetchedData;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View itemView = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.article_card, parent, false);

		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		FeedviewData articleInfo = data.get(position);
		holder.title.setText(articleInfo.getTitle());
		holder.description.setText(articleInfo.getDescription());

		String imgUrl = articleInfo.getImageUrl();
		if(imgUrl.equals("")){
			Drawable drawable = ContextCompat.getDrawable(WikifeediaApplication.getInstance().getApplicationContext(),R.drawable.no_image);
			holder.articleThumbnail.setImageDrawable(drawable);
		}
		else {
			Picasso.with(WikifeediaApplication.getInstance().getApplicationContext())
				.load(imgUrl)
				.placeholder(R.drawable.clock_loading)
				.error(R.drawable.no_image)
				.into(holder.articleThumbnail);
		}
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public void addFeedlist (ArrayList<FeedviewData> list){
		data.addAll(list);
		notifyDataSetChanged();
	}

	public void clearAll (){
		data.clear();
		notifyDataSetChanged();
	}


}


