package Utils;

import android.content.Intent;
import android.util.Log;

import Models.TrailerData;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by PY-DEV on 4/21/2016.
 */
public class CleanString {

    Realm realm;
    String eId;
    int trailerIndex;

    public CleanString(){


    }


    public String clean(int trailerIndex, String eId){

        this.eId = eId;
        this.trailerIndex = trailerIndex;

        Log.d("Video Data ID", " Clean String Info ====> ID: " + eId);

        realm = Realm.getDefaultInstance();
        RealmResults<TrailerData> temp_result = realm.where(TrailerData.class)
                .equalTo("id", eId).findAll();



        String key = temp_result.get(0).getKeys();
        String tempKey = key.replace(" ","").replace("[","").replace("]","");
        String[] keys = tempKey.split(",");

        Log.i("Video Data","Keys ===> "+keys.length);

        for (int x=0; x < keys.length; x++){
            Log.i("Video Data",x+" Key ===> "+keys[x]);
        }

        String video = keys[trailerIndex];
        return video;

    }
}
