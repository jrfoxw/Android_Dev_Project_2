package com.example.py_dev.movieshow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import Adapters.ImageAdapter;

import Fragments.DetailsFragment;

import Models.MovieData;

import Utils.Constants;
import Utils.GetReviewData;
import Utils.GetTrailerData;
import Utils.IsOnline;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public Realm realm;
    public MovieData mData = new MovieData();
    public Boolean bFavoritesOn = false;
    public RequestQueue requestQueue;


    public String popularity;
    public GetTrailerData gTData = new GetTrailerData();
    public GetReviewData gRData = new GetReviewData();
    private SharedPreferences tempData;
    private SharedPreferences.Editor editor;

    public DetailsFragment detailsFragment;




    String popular = Uri.parse(Constants.BASE_URL)
            .buildUpon()
            .appendPath("popular")
            .appendQueryParameter("api_key", Constants.API_KEY)
            .build()
            .toString();

    String rated = Uri.parse(Constants.BASE_URL)
            .buildUpon()
            .appendPath("top_rated")
            .appendQueryParameter("api_key", Constants.API_KEY)
            .build()
            .toString();

    public ArrayList<MovieData> fMovies = new ArrayList<>();
    public ArrayList<MovieData> pMovies = new ArrayList<>();


    private int currOrient;

    @Nullable
    @Bind(R.id.errorView)
    TextView errorView;
    @Nullable
    @Bind(R.id.errorCount)
    TextView errorCount;
    @Nullable
    @Bind(R.id.errorZone)
    LinearLayout errorZone;
    @Bind(R.id.gridView)
    GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("Movie Show");
        currOrient = getResources().getConfiguration().orientation;

        if(currOrient == Configuration.ORIENTATION_LANDSCAPE){
            this.setTitle("Popular Movies");
        }


        tempData = this.getSharedPreferences(Constants.TEMPDATA, Context.MODE_PRIVATE);
        realm = Realm.getDefaultInstance();


        /*DEBUG*/
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestQueue = Volley.newRequestQueue(this);
        RealmResults<MovieData> pResults = realm.where(MovieData.class).findAll();



        getMovies(popular, "popular");

        getMovies(rated, "rated");


        /**
         * Check for internet connection
         */
        errorCheck(pResults);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.most_popular_id:
                Log.d("OPTIONS MENU", " SORT BY POPULARITY SELECTED");
                this.setTitle("Popular Films");
                bFavoritesOn = false;
                getMovies(popular, "popular");
                break;

            case R.id.highest_rated_id:
                Log.d("OPTIONS MENU", " SORT BY VOTES SELECTED");
                this.setTitle("Highest Rated Films");
                bFavoritesOn = false;
                getMovies(rated, "rated");
                break;

            case R.id.favorites_id:
                this.setTitle("Favorite Movies");
                realm = Realm.getDefaultInstance();
                RealmResults<MovieData> fResults = realm.where(MovieData.class)
                        .equalTo("isfavorite", true)
                        .findAll();

                if (fResults.isEmpty()) {
                    Toast.makeText(MainActivity.this, Constants.NOFAVORITES, Toast.LENGTH_LONG).show();
                } else {

                    fMovies.clear();
                    for (int m = 0; m < fResults.size(); m++) {

                        MovieData mData = fResults.get(m);
                        fMovies.add(mData);

                    }

                    bFavoritesOn = true;

                    ImageAdapter adapter = new ImageAdapter(this, fResults.size(), fMovies);
                    adapter.notifyDataSetChanged();
                    gridView.setAdapter(adapter);
                    realm.close();

                    if (currOrient == Configuration.ORIENTATION_PORTRAIT) {
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v,
                                                    int position, long id) {

                                MovieData mData = fMovies.get(position);
                                editor = tempData.edit();
                                editor.putString("id", mData.getId());
                                editor.apply();

                                if (currOrient == Configuration.ORIENTATION_PORTRAIT) {
                                    Intent detailIntent = new Intent(MainActivity.this, DetailsActivity.class);
                                    startActivity(detailIntent);
                                } else {
                                    detailsFragment = (DetailsFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.detailsInfo);
                                    detailsFragment.changeData(mData.getId());
                                }
                            }

                        });

                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }


    public void errorCheck(RealmResults<MovieData> mResults) {

        if (!mResults.isEmpty() || IsOnline.getInstance(this).checkOnline()) {
            getMovies(popular, "popular");
        } else {

            if (!mResults.isEmpty() && !IsOnline.getInstance(this).checkOnline()) {

                errorView.setText(Constants.ERROR_TEXT[0]);
                errorZone.setVisibility(View.VISIBLE);

                getMovies(popular, "popular");

            } else if (mResults.isEmpty() && !IsOnline.getInstance(this).checkOnline()) {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

                errorView.setText(Constants.ERROR_TEXT[1]);
                gridView.setVisibility(View.INVISIBLE);
                errorZone.setVisibility(View.VISIBLE);
                new CountDownTimer(11000, 1000) {

                    public void onTick(long millisUntilFinished) {

                        String error1 = Constants.ERROR_TEXT[1] + millisUntilFinished / 1000 + " seconds...";
                        errorView.setText(error1);
                        errorCount.setText("" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        finish();
                    }

                }.start();

            }
            getMovies(rated, "rated");
            for (int r = 0; r < mResults.size(); r++) {
                pMovies.add(mResults.get(r));
            }

            ImageAdapter adapter = new ImageAdapter(this, pMovies.size(), pMovies);
            adapter.notifyDataSetChanged();
            gridView.setAdapter(adapter);
            getMovies(popular, "popular");

        }
    }


    public MovieData getMovies(String url, final String group) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(Constants.newMovieTag, "====> " + response);
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            Log.v(Constants.newMovieTag, "====> " + jsonArray);

                            pMovies.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject newMovie = jsonArray.getJSONObject(i);
                                Log.v(Constants.newMovieTag, "====> " + newMovie);
                                String posterLarge = Constants.POSTER_URL_342 + newMovie.getString("poster_path");
                                String posterThumb = Constants.POSTER_URL_185 + newMovie.getString("poster_path");
                                Log.v(Constants.newMovieTag, "====> " + newMovie.getString("original_title"));

                                if (newMovie.getString("popularity").substring(0, 2).contains(".")) {
                                    popularity = "0" + newMovie.getString("popularity").substring(0, 2);
                                } else {
                                    popularity = newMovie.getString("popularity").substring(0, 2);
                                }
                                mData = new MovieData(newMovie.getString("id"),
                                        group,
                                        newMovie.getString("original_title"),
                                        newMovie.getString("release_date"),
                                        posterThumb,
                                        posterLarge,
                                        newMovie.getString("overview"),
                                        popularity,
                                        newMovie.getString("vote_count"),
                                        newMovie.getString("vote_average"),
                                        false);

                                pMovies.add(mData);

                                RealmResults<MovieData> pResults =
                                        realm.where(MovieData.class)
                                                .equalTo("id", mData.getId())
                                                .findAll();
                                if (pResults.isEmpty()) {
                                    realm.beginTransaction();
                                    realm.copyToRealmOrUpdate(mData);
                                    realm.commitTransaction();
                                }

                                try {

                                   String builder = Uri.parse(Constants.BASE_URL)
                                            .buildUpon()
                                            .appendPath(newMovie.getString("id"))
                                            .appendPath("videos")
                                            .appendQueryParameter("api_key", Constants.API_KEY)
                                            .build()
                                            .toString();


                                    final String SHOW_TRAILERS = builder;
                                    gTData.getTrailerData(SHOW_TRAILERS, mData, requestQueue);

                                    builder = Uri.parse(Constants.BASE_URL)
                                            .buildUpon()
                                            .appendPath(newMovie.getString("id"))
                                            .appendPath("reviews")
                                            .appendQueryParameter("api_key", Constants.API_KEY)
                                            .build()
                                            .toString();


                                    final String SHOW_REVIEWS = builder;
                                    gRData.getReviewData(SHOW_REVIEWS, mData, requestQueue);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                gridView = (GridView) findViewById(R.id.gridView);
                                ImageAdapter adapter = (new ImageAdapter(MainActivity.this, i, pMovies));
                                adapter.notifyDataSetChanged();
                                gridView.setAdapter(adapter);

                                if(currOrient == Configuration.ORIENTATION_LANDSCAPE){
                                    detailsFragment = (DetailsFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.detailsInfo);
                                    detailsFragment.changeData(pMovies.get(0).getId());
                                }

                            }

                            /* Set gridView listener */

                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View v,
                                                        int position, long id) {

                                    mData = pMovies.get(position);

                                    editor = tempData.edit();
                                    editor.putString("id", mData.getId());
                                    editor.apply();

                                    if(currOrient == Configuration.ORIENTATION_PORTRAIT) {
                                        Intent detailIntent = new Intent(MainActivity.this, DetailsActivity.class);
                                        startActivity(detailIntent);
                                    }else{
                                        detailsFragment = (DetailsFragment) getSupportFragmentManager()
                                                .findFragmentById(R.id.detailsInfo);
                                        detailsFragment.changeData(mData.getId());
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("ERROR", error.getMessage());

                    }
                }
        );


        Log.d("REQUEST QUEUE JSON", " ====> " + jsonObjectRequest);
        this.requestQueue.add(jsonObjectRequest);


        return null;
    }
}
