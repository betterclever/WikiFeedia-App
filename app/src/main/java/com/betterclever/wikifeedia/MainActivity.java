package com.betterclever.wikifeedia;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.betterclever.wikifeedia.Contract.FeedviewData;
import com.betterclever.wikifeedia.adapters.FeedviewAdapter;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
	implements NavigationView.OnNavigationItemSelectedListener {

	private ArrayList<FeedviewData> feedviewDataList = new ArrayList<FeedviewData>();
	private FeedviewAdapter adapter;
	private SwipeRefreshLayout swipeContainer;
	private MaterialSearchView searchView;
	String category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		final Handler mHandler = new Handler();

		//Recycler View Code

		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.feed_recycler_view);
		adapter = new FeedviewAdapter(feedviewDataList);
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(adapter);

		category = "featured_articles";
		//call the data fetching function
		for(int i =0;i < 15;i++) {
			fetchRandomArticleData(category);
		}

		swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

		swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				adapter.clearAll();
				feedviewDataList.clear();

				for(int i =0;i < 15;i++) {
					fetchRandomArticleData(category);
				}
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						swipeContainer.setRefreshing(false);
					}
				}, 10000);
			}
		});

		searchView = (MaterialSearchView) findViewById(R.id.search_view);
		searchView.setVoiceSearch(false);
		searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if(query.equals("")){
					category = "featured_articles";
				}
				else category = query;
				Log.d("query",query);
				adapter.clearAll();
				feedviewDataList.clear();

				for(int i =0;i < 15;i++) {
					fetchRandomArticleData(category);
				}

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
			@Override
			public void onSearchViewShown() {

			}

			@Override
			public void onSearchViewClosed() {

			}
		});



		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show();
			}
		});

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
			this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}

		if (searchView.isSearchOpen()) {
			searchView.closeSearch();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_scrolling, menu);

		MenuItem item = menu.findItem(R.id.action_search);
		searchView.setMenuItem(item);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_camera) {
			// Handle the camera action
		} else if (id == R.id.nav_gallery) {

		} else if (id == R.id.nav_slideshow) {

		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	public void fetchRandomArticleData(String category){

		String HEROKU_API_URL = "http://wikifeedia.herokuapp.com/index.php?category="+category+"&callback=?";
		Log.d("Heroku URL",HEROKU_API_URL);
		final String[] title = {""};
		StringRequest stringRequest = new StringRequest(Request.Method.GET, HEROKU_API_URL,
			new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					response = response.substring(1,response.length()-1);
					response = response.substring(1,response.length()-1);
					response = response.substring(1,response.length()-1);
					response = response.substring(1,response.length()-1);
					title[0] = response.substring(13);
					Log.d("Extracted title", title[0]);
					if(title[0].equals("t")){
						fetchRandomArticleData("featured_articles");
						return;
					}
					getItemDetails(response.substring(13));

				}
			}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
		Log.d("title",title[0]);
		WikifeediaApplication.getInstance().addToRequestQueue(stringRequest);

	}

	public void getItemDetails(String title){
		String WIKI_DESCRIPTION_FETCH_API_URL = "https://en.wikipedia.org/w/api.php?action=query&titles="+ title+"&prop=pageimages|extracts&exintro=&explaintext=&format=json&pithumbsize=500";
		StringRequest stringRequest1 = new StringRequest(Request.Method.GET,WIKI_DESCRIPTION_FETCH_API_URL + title,
			new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject query = jsonObject.getJSONObject("query");
						JSONObject pages = query.getJSONObject("pages");
						String id = pages.names().getString(0);
						JSONObject pageInfo = pages.getJSONObject(id);
						String title = pageInfo.getString("title");
						String description = pageInfo.getString("extract");

						String imgURL = "";
						if(pageInfo.has("thumbnail")){
							imgURL = pageInfo.getJSONObject("thumbnail").getString("source");
						};

						Log.d("title",title);
						Log.d("description",description);
						Log.d("source",imgURL);
						FeedviewData data = new FeedviewData(title,description,imgURL);
						feedviewDataList.add(data);
						adapter.notifyDataSetChanged();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
		WikifeediaApplication.getInstance().addToRequestQueue(stringRequest1);
	}



}
