package Utils;

import android.app.Activity;
import android.content.Context;
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

import Models.MovieData;
import Models.TrailerData;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by PY-DEV on 4/18/2016.
 */
public class GetTrailerData {

    String trailerKey;
    TrailerData trailerData;
    ArrayList<String> trailerKeys = new ArrayList<>();
    String url;
    Realm realm;
    MovieData pMovie = new MovieData();


    public GetTrailerData(){

    }

    /**
     * Pull Trailer Data from Selected Movie.
     *
     */
    public String getTrailerData(String url, final Object object, RequestQueue requestQueue) {

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("ONRESPONSE", ">>> " + response + "\n");
                        Log.d("---#####","########################################################---\n");

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("results");
                            Log.d("TRAILER DATA","JSON DATA ====> "+data);
                            Log.d("TRAILER DATA","Object length ====> "+data.length());

                            if(object instanceof MovieData) {
                                pMovie = (MovieData) object;

                                for (int i = 0; i < data.length(); i++) {
                                    if (data.length() > 1) {

                                        trailerKey = data.getJSONObject(i).getString("key");

                                        Log.d("TRAILER KEY", "Trailer #" + i + " ====> " + trailerKey);
                                        trailerKeys.add(trailerKey);
                                    } else {

                                        trailerKey = data.getJSONObject(i).getString("key");
                                        Log.d("TRAILER KEY", "Trailer #" + i + " ====> " + trailerKey);
                                        trailerKeys.add(trailerKey);
                                        break;
                                    }
                                }


                                trailerData = new TrailerData(
                                        pMovie.getId(),
                                        trailerKeys
                                );

                                realm = Realm.getDefaultInstance();
                                RealmQuery<TrailerData> query = realm.where(TrailerData.class);
                                query.equalTo("id", pMovie.getId());
                                RealmResults<TrailerData> results = query.findAll();


                                if(results.isEmpty()) {

                                    realm.beginTransaction();
                                    realm.copyToRealm(trailerData);
                                    realm.commitTransaction();
                                }

                                Log.d("TRAILERS STORED", "=====> " + trailerKeys.size());
                                trailerKeys.clear();

                            }



                        } catch (JSONException e) {
                            Log.d("ERROR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("ERROR ", "Error Occurred");
            }
        });

        requestQueue.add(request);
        return null;

    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
