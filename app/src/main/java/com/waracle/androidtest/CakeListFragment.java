package com.waracle.androidtest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

/**
 * Fragment is responsible for loading in some JSON and
 * then displaying a list of cakes with images.
 * Fix any crashes
 * Improve any performance issues
 * Use good coding practices to make code more secure
 */
public class CakeListFragment extends ListFragment {
	private static final String TAG = CakeListFragment.class.getSimpleName();
	private static final String savedJson = "offline";
	private static String JSON_URL = "https://gist.githubusercontent.com/t-reed/739df99e9d96700f17604a3971e701fa/raw/1d4dd9c5a0ec758ff5ae92b7b13fe4d57d34e1dc/waracle_cake-android-client";
	
	public CakeListFragment() {
	}
	
	private Context context;
	
	private void refreshCakeList(String jsonArray) {
		setListAdapter(new CakeListArrayAdapter(context, jsonArray));
	}
	
	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		if (savedInstanceState == null)
			loadData();
		refreshCakeList(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(savedJson, null));
	}
	
	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_main, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.action_refresh) {
			loadData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void loadData() {
		if (context != null) {
			Toast.makeText(context, context.getString(R.string.refreshing_list_of_cakes), Toast.LENGTH_SHORT).show();
			new Thread(new Runnable() {
				@Override
				public void run() {
					HttpURLConnection connection = null;
					try {
						connection = (HttpURLConnection) (new URL(JSON_URL)).openConnection();
						connection.setDoInput(true);
						InputStream inputStream = connection.getErrorStream();
						if (inputStream == null) inputStream = connection.getInputStream();
						
						BufferedReader stream = new BufferedReader(new InputStreamReader(inputStream));
						final StringBuilder body = new StringBuilder();
						String line;
						while ((line = stream.readLine()) != null)
							body.append(line);
						PreferenceManager.getDefaultSharedPreferences(context).edit().putString(savedJson, body.toString()).apply();
						((Activity) context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								refreshCakeList(body.toString());
							}
						});
					} catch (Exception e) {
						Log.e(TAG, e.getLocalizedMessage());
					} finally {
						if (connection != null)
							connection.disconnect();
					}
				}
			}).start();
		}
	}
}
