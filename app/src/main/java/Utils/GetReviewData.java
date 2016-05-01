package Utils;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import Adapters.ReviewAdapter;
import Models.MovieData;
import Models.ReviewsData;
//import Models.StoreReviews;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by PY-DEV on 4/8/2016.
 */
public class GetReviewData extends Activity {



    ReviewsData reviewsData = new ReviewsData();
    MovieData pMovie = new MovieData();

    Realm realm;

    public GetReviewData(){

    }


    public String getReviewData(String url, final Object object, RequestQueue requestQueue) {

        StringRequest request = new StringRequest(Request.Method.GET, url,

                new Response.Listener<String>() {



                    @Override
                    public void onResponse(String response) {
                        Log.d("ON RESPONSE", ">>> " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("results");

                            System.out.println("===========================================================================================\n");
                            Log.d("DATA RESPONSE", ".. DATA .. " + data);
                            Log.d("DATA RESPONSE", ".. DATA LENGTH .. " + data.length());
                            Log.d("REVIEW DATA", " ====> STORED " + pMovie.getClass());
                            System.out.println("\n=========================================================================================\n");


                                pMovie = (MovieData) object;
                                if (data.length() > 0) {
                                    for (int x = 0; x < data.length(); x++) {
                                        String author = data.getJSONObject(x).getString("author");
                                        String review = data.getJSONObject(x).getString("content");
                                        Log.d("REVIEW DATA", x + " ====> " + data.getJSONObject(x).getString("content"));


                                        reviewsData = new ReviewsData(
                                                pMovie.getId(),
                                                author,
                                                review
                                        );
                                        realm = Realm.getDefaultInstance();

                                        RealmResults<ReviewsData> pResults = realm.where(ReviewsData.class)
                                                .equalTo("id",pMovie.getId())
                                                .equalTo("review",review)
                                                .findAll();

                                        if(pResults.isEmpty()) {
                                            realm.beginTransaction();
                                            realm.copyToRealm(reviewsData);
                                            realm.commitTransaction();
                                        }
                                    }



                                }

                                Log.d("REVIEW STORED", "======> " + pMovie.getTitleItem());





                        } catch (JSONException e) {
                            Log.d("JSON ERROR", e.getMessage());

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("ERROR ", "error with request ");
            }
        });

        requestQueue.add(request);
        return null;
    }

}
