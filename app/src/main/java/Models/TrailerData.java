package Models;

import java.util.ArrayList;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by PY-DEV on 4/18/2016.
 */
public class TrailerData extends RealmObject{



    public TrailerData(){

    }


    public TrailerData(String id, ArrayList<String> arraykeys){
        this.id = id;
        this.keys = arraykeys.toString();


    }

    @PrimaryKey
    String id;
    String keys;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }


}
