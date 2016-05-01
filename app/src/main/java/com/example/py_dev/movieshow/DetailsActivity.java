package com.example.py_dev.movieshow;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//import Fragments.DetailsFragment;
//
//import Fragments.OverviewFragment;


import Adapters.ImageAdapter;
import Adapters.ReviewAdapter;
import Fragments.DetailsFragment;
import Fragments.MovieFragment;
import Models.MovieData;
import Models.ReviewsData;
import Models.TrailerData;
import Utils.CleanString;
import Utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {




    public Intent extras;
    public Context context;
    public CleanString cString = new CleanString();

    Realm realm;
    MovieData data = new MovieData();
    Boolean favoritesOn = false;
    String video;
    DetailsFragment detailsFragment;

    MovieData mData;

    MovieFragment movieFragment;
    SharedPreferences tempData;
    SharedPreferences.Editor editor;

    ArrayList<MovieData> fMovies = new ArrayList<>();
    ArrayList<ReviewsData> rData = new ArrayList<>();
    int currOrient;

    // BUTTER KNIFE API ///////////////////////////////////

    @Bind(R.id.titleView)
    TextView titleText;
    @Bind(R.id.posterView)
    ImageView posterView;
    @Bind(R.id.releaseDateView)
    TextView releaseDate;
    @Bind(R.id.votesView)
    TextView votesView;
    @Bind(R.id.popularityView)
    TextView popularityView;
    @Bind(R.id.votesAverageView)
    TextView votesAverageView;
    @Bind(R.id.favorite_btn)
    ImageView favorMark;
    @Bind(R.id.ibtn_trailer1)
    ImageView trailer1;
    @Bind(R.id.ibtn_trailer2)
    ImageView trailer2;
    @Bind(R.id.text_trailer2)
    TextView trailerlabel;
    @Bind(R.id.shareBtn)
    ImageView shareBtn;

    GridView gridView;
    @Bind(R.id.reviewDetails)
    ListView reviewDetails;
    @Bind(R.id.overviewBtn)
    TextView overviewBtn;
    @Bind(R.id.reviewBtn)
    TextView reviewBtn;
    @Bind(R.id.overviewView)
    TextView overviewView;
    @Bind(R.id.scrollView2)
    ScrollView scrollView;

    //////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setTitle("Movie Show");
        extras = getIntent();
        currOrient = getResources().getConfiguration().orientation;
        tempData = this.getSharedPreferences(Constants.TEMPDATA, Context.MODE_PRIVATE);

        if(currOrient == Configuration.ORIENTATION_PORTRAIT) {
            Log.v("++TEMPDATA++", " ======> " + tempData.getString("id", null));

            String idMovie = tempData.getString("id", null);

            realm = Realm.getDefaultInstance();
            RealmResults<MovieData> results = realm.where(MovieData.class)
                    .equalTo("id", idMovie)
                    .findAll();

            mData = results.get(0);

            titleText.setText(mData.getTitleItem());
            releaseDate.setText(mData.getReleaseDate());
            popularityView.setText(mData.getPopularityItem());
            votesView.setText(mData.getVotesItems());
            votesAverageView.setText(mData.getVoteItemsAverageItems());
            Picasso.with(DetailsActivity.this)
                    .load(mData.getPosterItemLarge())
                    .placeholder(R.drawable.nodata)
                    .error(R.drawable.nodata)
                    .into(posterView);


            /* Reviews and Overview */

            RealmResults<ReviewsData> rResults = realm.where(ReviewsData.class)
                    .equalTo("id",idMovie)
                    .findAll();

            String rText = "Reviews("+rResults.size()+")";
            reviewBtn.setText(rText);
            reviewBtn.setTextColor(Color.DKGRAY);
            rData.clear();
            for(int r=0; r < rResults.size(); r++){
                rData.add(rResults.get(r));
            }

            /**
             * If ReviewDetails is currently visible and not empty, change list adapter to
             * chosen movie reviews, else change visibilty of overview and populate.
             */
            if(reviewDetails.getVisibility() == View.VISIBLE) {
                reviewDetails.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                overviewBtn.setTextColor(Color.parseColor("#c1b02c"));

            }

            scrollView.setVisibility(View.VISIBLE);
            overviewView.setText(mData.getOverView());

            reviewClick();
            overViewClick();


            realm = Realm.getDefaultInstance();
            /**
             * Check Trailer Data
             * Verify if only one trailer hide trailer 2 button.
             */
            RealmResults<TrailerData> temp_result = realm.where(TrailerData.class)
                    .equalTo("id", tempData.getString("id", "none")).findAll();

            try {
                if (temp_result.get(0).getKeys().split(",").length < 2) {
                    trailer2.setVisibility(View.INVISIBLE);
                    trailerlabel.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            /**
             * Check if Movie has been marked as favorite
             */
            RealmResults<MovieData> favorite_result = realm.where(MovieData.class)
                    .equalTo("id", tempData.getString("id", "none"))
                    .equalTo("isfavorite", true)
                    .findAll();

            if (!favorite_result.isEmpty()) {

                favorMark.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

            }

            /**
             * Play Trailer 1
             */
            trailer1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("Video Data ID", "====> ID: " + tempData.getString("id", "none"));
                    int trailerIndex = 0;
                    video = cString.clean(trailerIndex, tempData.getString("id", "none"));
                    Log.i("Video Data", "Video ====> " + video);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video)));
                    Log.i("Video", "Video Playing.... " + "http://www.youtube.com/watch?v=" + video);


                }
            });


            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    RealmResults<MovieData> mResults = realm.where(MovieData.class)
                            .equalTo("id", tempData.getString("id", "none"))
                            .findAll();
                    share.putExtra(Intent.EXTRA_SUBJECT, "Movie Trailer " + mResults.get(0).getTitleItem());
                    int trailerIndex = 0;
                    video = cString.clean(trailerIndex, tempData.getString("id", "none"));
                    share.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + video);

                    startActivity(Intent.createChooser(share, "Sharing Link!"));

                }
            });


            /**
             * Play Trailer 2
             */
            trailer2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Video Data ID", "====> ID: " + tempData.getString("id", "none"));
                    int trailerIndex = 1;
                    String video = cString.clean(trailerIndex, tempData.getString("id", "none"));
                    Log.i("Video Data", "Video ====> " + video);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video)));
                    Log.i("Video", "Video Playing.... " + "http://www.youtube.com/watch?v=" + video);


                }
            });


            favorMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    realm = Realm.getDefaultInstance();
                    String fId = tempData.getString("id", "none");
                    RealmResults<MovieData> results = realm.where(MovieData.class)
                            .equalTo("id", fId)
                            .findAll();
                    if (!results.get(0).getIsfavorite()) {
                        realm.beginTransaction();
                        results.get(0).setIsfavorite(true);
                        realm.commitTransaction();
                        favorMark.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                        Toast.makeText(DetailsActivity.this, "Added Favorite", Toast.LENGTH_SHORT).show();
                    } else {
                        favorMark.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                        realm.beginTransaction();
                        results.get(0).setIsfavorite(false);
                        realm.commitTransaction();
                        Toast.makeText(DetailsActivity.this, "Removed Favorite", Toast.LENGTH_SHORT).show();
                    }
                    realm.close();
                    if (DetailsActivity.this.getTitle() == "Favorite Movies") {
                        fMovies.clear();
                        RealmResults<MovieData> fResults = realm.where(MovieData.class)
                                .equalTo("isfavorite", true)
                                .findAll();
                        for (int r = 0; r < fResults.size(); r++) {
                            fMovies.add(fResults.get(r));
                        }

                        gridView = (GridView) findViewById(R.id.gridView);
                        ImageAdapter adapter = new ImageAdapter(DetailsActivity.this, fResults.size(), fMovies);
                        adapter.notifyDataSetChanged();
                        gridView.setAdapter(adapter);

                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v,
                                                    int position, long id) {

                                MovieData data = fMovies.get(position);

                                tempData = getSharedPreferences(Constants.TEMPDATA, Context.MODE_PRIVATE);

                                editor = tempData.edit();
                                editor.putString("id", data.getId());
                                editor.apply();

                                detailsFragment = (DetailsFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.detailsInfo);
                                detailsFragment.changeData(data.getId());


                            }

                        });
                    }
                }
            });

        }

        if(currOrient == Configuration.ORIENTATION_LANDSCAPE){

            movieFragment = (MovieFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.movieInfo);
            movieFragment.changeData("popular", data.getId());
        }

    }

    @OnClick(R.id.reviewBtn)
    public void reviewClick(){

        reviewBtn.setTextColor(Color.parseColor("#c1b02c"));
        overviewBtn.setTextColor(Color.DKGRAY);

        scrollView.setVisibility(View.GONE);
        overviewView.setVisibility(View.GONE);
//                    reviewDetails = (ListView) findViewById(R.id.reviewDetails);
        reviewDetails.setVisibility(View.VISIBLE);
        ListAdapter adapter = new ReviewAdapter(DetailsActivity.this,rData);
        reviewDetails.setAdapter(adapter);

    }

    @OnClick(R.id.overviewBtn)
    public void overViewClick(){

        scrollView.setVisibility(View.VISIBLE);
        overviewView.setVisibility(View.VISIBLE);


        reviewDetails.setVisibility(View.GONE);

        overviewBtn.setTextColor(Color.parseColor("#c1b02c"));
        reviewBtn.setTextColor(Color.DKGRAY);

        overviewView.setText(mData.getOverView());

        Log.v("== Details Fragment ==","OverView View ====> "+ mData.getOverView());
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        if(currOrient == Configuration.ORIENTATION_LANDSCAPE){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_menu, menu);
        return true;
        }
        return false;
    }
//
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        movieFragment = (MovieFragment) getSupportFragmentManager()
                .findFragmentById(R.id.movieInfo);

        switch (item.getItemId()) {

            case R.id.most_popular_id:
                Log.d("OPTIONS MENU"," SORT BY POPULARITY SELECTED");
                this.setTitle("Popular Films");
                favoritesOn = false;
                movieFragment.changeData("popular", data.getId());
                break;

            case R.id.highest_rated_id:
                Log.d("OPTIONS MENU"," SORT BY VOTES SELECTED");
                this.setTitle("Highest Rated Films");
                favoritesOn = false;
                movieFragment.changeData("rated", data.getId());
                break;

            case R.id.favorites_id:
                this.setTitle("Favorite Movies");
                realm = Realm.getDefaultInstance();
                RealmResults<MovieData> fResults = realm.where(MovieData.class)
                        .equalTo("isfavorite", true)
                        .findAll();

                if(fResults.isEmpty()){
                    Toast.makeText(DetailsActivity.this,"You have not selected any favorites, " +
                            "click on the star while viewing the details, " +
                            "to select a movie as a favorite.",Toast.LENGTH_LONG).show();
                }else{

                    fMovies.clear();
                    for(int m=0; m < fResults.size(); m++){
                        MovieData mData = fResults.get(m);
                        fMovies.add(mData);
                    }
                      movieFragment.changeData("favorites", data.getId());
                }
            }
        return super.onOptionsItemSelected(item);
    }

}

