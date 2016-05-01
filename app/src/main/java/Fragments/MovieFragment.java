package Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.py_dev.movieshow.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Adapters.ImageAdapter;
import Models.MovieData;
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
public class MovieFragment extends Fragment {


    private Realm realm;
    private MovieData data = new MovieData();
    public RealmResults<MovieData> results;

    private DetailsFragment detailsFragment;
    private CleanString cString = new CleanString();
    private ArrayList<MovieData> mMovies = new ArrayList<>();



    private SharedPreferences tempData;
    private SharedPreferences.Editor editor;


    // Bindings  ///////////////////////////////////
    @Bind(R.id.favorite_btn)
    ImageView favorMark;
    @Bind(R.id.ibtn_trailer1)
    ImageView trailer1;
    @Bind(R.id.ibtn_trailer2)
    ImageView trailer2;
    @Bind(R.id.text_trailer2)
    TextView trailerlabel;
    @Bind(R.id.gridView)
    GridView gridView;
    @Bind(R.id.shareBtn)
    ImageView shareBtn;
    //////////////////////////////////////////////////////

    public MovieFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_movie, container, false);


        return view;
    }


    public void changeData(final String group, final String mId) {
//        getActivity().setTitle("Movie Show");
        ButterKnife.bind(this, getActivity());




        realm = Realm.getDefaultInstance();

        if (group.equals("favorites")) {
            results = realm.where(MovieData.class)
                    .equalTo("isfavorite", true)
                    .findAll();
        } else {
            results = realm.where(MovieData.class)
                    .equalTo("group", group)
                    .findAll();
        }


        mMovies.clear();
        for (int r = 0; r < results.size(); r++) {
            Log.v("MOVIE FRAGMENT", "====> " + results.get(r).getTitleItem());
            mMovies.add(results.get(r));


        }


        ImageAdapter adapter = new ImageAdapter(getActivity(), results.size(), mMovies);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);

        tempData = getActivity().getSharedPreferences(Constants.TEMPDATA, Context.MODE_PRIVATE);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                data = mMovies.get(position);

                editor = tempData.edit();
                editor.putString("id", data.getId());
                editor.apply();


               detailsFragment = (DetailsFragment) getFragmentManager()
                        .findFragmentById(R.id.detailsInfo);
                detailsFragment.changeData(data.getId());




                /**
                 * Check Trailer Data
                 * Verify if only one trailer hide trailer 2 button.
                 */

                Log.v("MOVIE FRAGEMENT", "TRAILER DATA ====> " + data.getTitleItem());
                Log.v("MOVIE FRAGEMENT", "TRAILER DATA ====> " + data.getId());

                RealmResults<TrailerData> temp_result = realm.where(TrailerData.class)
                        .equalTo("id", data.getId())
                        .findAll();

                try {
                    if (temp_result.get(0).getKeys().split(",").length < 2) {
                        trailer2.setVisibility(View.INVISIBLE);
                        trailerlabel.setVisibility(View.INVISIBLE);
                    } else {
                        trailer2.setVisibility(View.VISIBLE);
                        trailerlabel.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                /**
                 * Check if Movie has been marked as favorite
                 */


                RealmResults<MovieData> favorite_result = realm.where(MovieData.class)
                        .equalTo("id", data.getId())
                        .equalTo("isfavorite", true)
                        .findAll();

                if (!favorite_result.isEmpty()) {

                    favorMark.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

                } else {

                    favorMark.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                }




                /**
                 * Play Trailer 1
                 */
                trailer1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int trailerIndex = 0;
                        String video = cString.clean(trailerIndex, data.getId());

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
                        String video = cString.clean(trailerIndex, tempData.getString("id", "none"));
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

                        int trailerIndex = 1;
                        String video = cString.clean(trailerIndex, data.getId());

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video)));
                        Log.i("Video", "Video Playing.... " + "http://www.youtube.com/watch?v=" + video);


                    }
                });


                favorMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        realm = Realm.getDefaultInstance();
                        ImageView favorMark = (ImageView) getActivity().findViewById(R.id.favorite_btn);

                        RealmResults<MovieData> results = realm.where(MovieData.class)
                                .equalTo("id", data.getId())
                                .findAll();

                        if (!results.get(0).getIsfavorite()) {
                            realm.beginTransaction();
                            results.get(0).setIsfavorite(true);
                            realm.commitTransaction();
                            favorMark.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                            Toast.makeText(getActivity(), "Added Favorite", Toast.LENGTH_SHORT).show();
                        } else {

                            favorMark.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                            realm.beginTransaction();
                            results.get(0).setIsfavorite(false);
                            realm.commitTransaction();
                            Toast.makeText(getActivity(), "Removed Favorite", Toast.LENGTH_SHORT).show();
                        }

                        if(getActivity().getTitle() == "Favorite Movies"){

                                mMovies.clear();
                                RealmResults<MovieData> fResults = realm.where(MovieData.class)
                                        .equalTo("isfavorite", true)
                                        .findAll();

                                for(int r=0; r < fResults.size(); r++) {
                                    mMovies.add(fResults.get(r));
                                }

                                gridView = (GridView) getActivity().findViewById(R.id.gridView);
                                ImageAdapter adapter = new ImageAdapter(getActivity(), fResults.size(), mMovies);
                                adapter.notifyDataSetChanged();
                                gridView.setAdapter(adapter);

                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    public void onItemClick(AdapterView<?> parent, View v,
                                                            int position, long id) {

                                        MovieData data = mMovies.get(position);

                                        tempData = getActivity().getSharedPreferences(Constants.TEMPDATA,Context.MODE_PRIVATE);

                                        editor = tempData.edit();
                                        editor.putString("id",data.getId());
                                        editor.apply();


                                        detailsFragment.changeData(data.getId());


                                    }

                                });
                            }

                        realm.close();
                    }
                });
            }

        });


    }

}
