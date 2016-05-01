package Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.py_dev.movieshow.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Adapters.ImageAdapter;
import Adapters.ReviewAdapter;
import Models.MovieData;
import Models.ReviewsData;
import Models.TrailerData;
import Utils.CleanString;
import Utils.Constants;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {


    Realm realm;
    SharedPreferences tempdata;
    CleanString cString = new CleanString();
    ArrayList<MovieData> fMovies = new ArrayList<>();

    SharedPreferences tempData;
    SharedPreferences.Editor editor;

    DetailsFragment detailsFragment;

    ArrayList<ReviewsData> rData = new ArrayList<>();
    ArrayList<MovieData> mData = new ArrayList<>();

    int curOrientation = 0;


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
    TextView trailerLabel;
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


    public DetailsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        realm = Realm.getDefaultInstance();
        View view = getView();
        if (view != null){
        Log.v("DetailsFragment","onStart");

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        realm = Realm.getDefaultInstance();
        tempdata = getContext().getSharedPreferences(Constants.TEMPDATA, Context.MODE_PRIVATE);
        curOrientation = getResources().getConfiguration().orientation;

        String tempId = tempdata.getString("id", "none");

        RealmResults<MovieData> results = realm.where(MovieData.class)
                .equalTo("id",tempId)
                .findAll();

        if(!results.isEmpty()) {

            changeData(tempId);

        }
    }



    public void changeData(final String idfinal){

        ButterKnife.bind(getActivity());

        Log.v("DETAILS FRAGMENT","DATA UPDATED  ===>"+idfinal);
        realm = Realm.getDefaultInstance();


        /**
         * CHECK IF FAVORITE
         */
        RealmResults<MovieData> favorite_result = realm.where(MovieData.class)
                .equalTo("id", idfinal)
                .equalTo("isfavorite", true)
                .findAll();

        if(!favorite_result.isEmpty()){

            favorMark.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        } else {

            favorMark.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
        }


        /**
         * SET TRAILER LINKS VISIBLE/INVISIBLE
         */

        RealmResults<TrailerData> results2 = realm.where(TrailerData.class)
                .equalTo("id",idfinal)
                .findAll();


        try {
            if (results2.get(0).getKeys().split(",").length < 2){
                trailer2.setVisibility(View.INVISIBLE);
                trailerLabel.setVisibility(View.INVISIBLE);
            }else{
                trailer2.setVisibility(View.VISIBLE);
                trailerLabel.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * SET TRAILER LINKS
         */

        RealmResults<MovieData> results = realm.where(MovieData.class)
                .equalTo("id",idfinal)
                .findAll();

        final MovieData pResults = results.get(0);

        trailer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int trailerIndex = 0;
                Log.v("Video Data","Video ID: ====> "+pResults.getId());
                String video = cString.clean(trailerIndex,idfinal);
                Log.i("Video Data", "Video ====> " + video);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video)));
                Log.i("Video", "Video Playing.... " + "http://www.youtube.com/watch?v="+video );
            }
        });


        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                share.putExtra(Intent.EXTRA_SUBJECT,"Movie Trailer "+pResults.getTitleItem());
                int trailerIndex = 0;
                String video = cString.clean(trailerIndex,pResults.getId());
                share.putExtra(Intent.EXTRA_TEXT,"http://www.youtube.com/watch?v="+video);

                startActivity(Intent.createChooser(share, "Sharing Link!"));

            }
        });

        trailer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int trailerIndex = 1;
                Log.v("Video Data","Video ID: ====> "+pResults.getId());
                String video = cString.clean(trailerIndex,idfinal);
                Log.i("Video Data", "Video ====> " + video);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video)));
//                Log.i("Video", "Video Playing.... " + "http://www.youtube.com/watch?v="+video );
            }
        });





        favorMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm = Realm.getDefaultInstance();

                RealmResults<MovieData> results = realm.where(MovieData.class)
                        .equalTo("id", idfinal)
                        .findAll();

                if (!results.get(0).getIsfavorite()) {
                    realm.beginTransaction();
                    results.get(0).setIsfavorite(true);
                    realm.commitTransaction();
                    favorMark.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                    Toast.makeText(getActivity(), "Added Favorite", Toast.LENGTH_SHORT).show();

                    fMovies.clear();
                    RealmResults<MovieData> fResults = realm.where(MovieData.class)
                            .equalTo("isfavorite", true)
                            .findAll();

                    for (int r = 0; r < fResults.size(); r++) {
                        fMovies.add(fResults.get(r));
                    }

                } else {

                    favorMark.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                    realm.beginTransaction();
                    results.get(0).setIsfavorite(false);
                    realm.commitTransaction();
                    Toast.makeText(getActivity(), "Removed Favorite", Toast.LENGTH_SHORT).show();
                    Log.v(" === Details Frag ===","Favorite Removed...");

                }

                if (getActivity().getTitle() == "Favorite Movies") {
                    fMovies.clear();
                    RealmResults<MovieData> fResults = realm.where(MovieData.class)
                            .equalTo("isfavorite", true)
                            .findAll();

                    for (int r = 0; r < fResults.size(); r++) {
                        fMovies.add(fResults.get(r));
                    }



                    gridView = (GridView) getActivity().findViewById(R.id.gridView);
                    ImageAdapter adapter = new ImageAdapter(getActivity(), fResults.size(), fMovies);
                    adapter.notifyDataSetChanged();
                    gridView.setAdapter(adapter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            MovieData data = fMovies.get(position);

                            tempData = getActivity().getSharedPreferences(Constants.TEMPDATA, Context.MODE_PRIVATE);

                            editor = tempData.edit();
                            editor.putString("id", data.getId());
                            editor.apply();

                            detailsFragment = (DetailsFragment) getFragmentManager()
                                    .findFragmentById(R.id.detailsInfo);
                            detailsFragment.changeData(data.getId());


                        }

                    });
                }
            }

            });




        realm = Realm.getDefaultInstance();
        RealmResults<MovieData> oResults = realm.where(MovieData.class)
                .equalTo("id",idfinal)
                .findAll();

        RealmResults<ReviewsData> rResults = realm.where(ReviewsData.class)
                .equalTo("id",idfinal)
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
            overviewView.setVisibility(View.VISIBLE);
            overviewBtn.setTextColor(Color.parseColor("#c1b02c"));

        }

        scrollView.setVisibility(View.VISIBLE);
        overviewView.setVisibility(View.VISIBLE);
        overviewView.setText(pResults.getOverView());
        final MovieData mData = oResults.get(0);


        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reviewBtn.setTextColor(Color.parseColor("#c1b02c"));
                overviewBtn.setTextColor(Color.DKGRAY);

                scrollView.setVisibility(View.GONE);
                overviewView.setVisibility(View.GONE);
                reviewDetails = (ListView) getActivity().findViewById(R.id.reviewDetails);
                reviewDetails.setVisibility(View.VISIBLE);
                ListAdapter adapter = new ReviewAdapter(getActivity(), rData);
                reviewDetails.setAdapter(adapter);
            }
        });

        overviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                scrollView.setVisibility(View.VISIBLE);
                overviewView.setVisibility(View.VISIBLE);
                reviewDetails.setVisibility(View.GONE);

                overviewBtn.setTextColor(Color.parseColor("#c1b02c"));
                reviewBtn.setTextColor(Color.DKGRAY);
                overviewView.setText(mData.getOverView());

                Log.v("== Details Fragment ==","OverView View ====> "+ mData.getOverView());

            }
        });




        titleText.setText(mData.getTitleItem());
        releaseDate.setText(mData.getReleaseDate());
        popularityView.setText(mData.getPopularityItem());
        votesView.setText(mData.getVotesItems());
        votesAverageView.setText(mData.getVoteItemsAverageItems());
        Picasso.with(getContext())
                .load(mData.getPosterItemLarge())
                .error(R.drawable.nodata)
                .placeholder(R.drawable.nodata)
                .into(posterView);



    }
}
