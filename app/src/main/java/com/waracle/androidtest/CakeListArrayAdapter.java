package com.waracle.androidtest;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CakeListArrayAdapter extends ArrayAdapter<CakeModel> {
	
	public CakeListArrayAdapter(Context context, String jsonCakeList) {
		super(context, 0);
		clear();
		if (jsonCakeList != null && !jsonCakeList.isEmpty() && !jsonCakeList.equals("{}")) {
			try {
				JSONArray jsonArray = new JSONArray(jsonCakeList);
				for (int i = 0, size = jsonArray.length(); i < size; i++) {
					add(new CakeModel(jsonArray.getJSONObject(i)));
				}
			} catch (JSONException e) {
				Log.e(CakeListArrayAdapter.class.getName(), e.getLocalizedMessage());
			}
		}
	}
	
	private static class ViewHolder {
		TextView titleView;
		TextView descView;
		ImageView imageView;
	}
	
	public interface DataSetChangedInterface {
		void refresh();
	}
	
	private void dataSetChanged() {
		notifyDataSetChanged();
	}
	
	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, null);
			holder = new ViewHolder();
			holder.titleView = convertView.findViewById(R.id.title);
			holder.descView = convertView.findViewById(R.id.desc);
			holder.imageView = convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		CakeModel cake = getItem(position);
		if (cake != null) {
			holder.titleView.setText(cake.getTitle());
			holder.descView.setText(cake.getDesc());
			if (cake.getImage() == null) {
				DownloadCakeImageAsyncTask.getInstance(position, new DataSetChangedInterface() {
					@Override
					public void refresh() {
						dataSetChanged();
					}
				}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cake);
			} else
				holder.imageView.setImageBitmap(cake.getImage());
		}
		return convertView;
	}
	
	private static class DownloadCakeImageAsyncTask extends AsyncTask<CakeModel, Void, Void> {
		
		static DownloadCakeImageAsyncTask getInstance(int index, DataSetChangedInterface dataSetChangedInterface) {
			DownloadCakeImageAsyncTask downloader = new DownloadCakeImageAsyncTask();
			downloader.dataSetChangedInterface = dataSetChangedInterface;
			return downloader;
		}
		
		private DataSetChangedInterface dataSetChangedInterface = null;
		
		@Override
		protected Void doInBackground(CakeModel... cakes) {
			try {
				CakeModel cake = cakes[0];
				cake.setImage(BitmapFactory.decodeStream(new URL(cake.getImageUrl()).openStream()));
			} catch (Exception e) {
				Log.e(getClass().getName(), e.getLocalizedMessage());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if (dataSetChangedInterface != null)
				dataSetChangedInterface.refresh();
		}
		
	}
}
