package com.example.py_dev.movieshow;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by PY-DEV on 4/15/2016.
 */
public class AppMovieHandler extends Application {

    Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();

            RealmConfiguration configuration = new RealmConfiguration.Builder(this).build();
            
            Realm.setDefaultConfiguration(configuration);
            realm = Realm.getDefaultInstance();


        }


}
