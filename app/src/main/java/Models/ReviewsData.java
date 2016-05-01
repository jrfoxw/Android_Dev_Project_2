package Models;

import java.util.ArrayList;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by PY-DEV on 4/18/2016.
 */
public class ReviewsData extends RealmObject {


    public ReviewsData() {

    }


    public ReviewsData(String id, String author, String review) {
        this.id = id;
        this.author = author;
        this.review = review;


    }

//    @PrimaryKey
    String id;
    String author;
    String review;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}