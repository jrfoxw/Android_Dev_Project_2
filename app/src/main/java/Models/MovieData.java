package Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by PY-DEV on 4/15/2016.
 */
public class MovieData extends RealmObject{

    public MovieData(String id,
                     String group,
                     String titleItem,
                     String releaseDate,
                     String posterItem,
                     String posterItemLarge,
                     String overView,
                     String popularityItem,
                     String votesItems,
                     String voteItemsAverageItems,
                     Boolean isfavorite) {

        this.id = id;
        this.group = group;
        this.posterItem = posterItem;
        this.titleItem = titleItem;
        this.posterItemLarge = posterItemLarge;
        this.overView = overView;
        this.releaseDate = releaseDate;
        this.popularityItem = popularityItem;
        this.votesItems = votesItems;
        this.voteItemsAverageItems = voteItemsAverageItems;
        this.isfavorite = isfavorite;
    }




    @PrimaryKey
    private String id;
    private String group;
    private String posterItem;
    private String titleItem;
    private String posterItemLarge;
    private String overView;
    private String releaseDate;
    private String popularityItem;
    private String votesItems;
    private String voteItemsAverageItems;
    private Boolean isfavorite;


    public MovieData(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPosterItem() {
        return posterItem;
    }

    public void setPosterItem(String posterItem) {
        this.posterItem = posterItem;
    }

    public String getTitleItem() {
        return titleItem;
    }

    public void setTitleItem(String titleItem) {
        this.titleItem = titleItem;
    }

    public String getPosterItemLarge() {
        return posterItemLarge;
    }

    public void setPosterItemLarge(String posterItemLarge) {
        this.posterItemLarge = posterItemLarge;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPopularityItem() {
        return popularityItem;
    }

    public void setPopularityItem(String popularityItem) {
        this.popularityItem = popularityItem;
    }

    public String getVotesItems() {
        return votesItems;
    }

    public void setVotesItems(String votesItems) {
        this.votesItems = votesItems;
    }

    public String getVoteItemsAverageItems() {
        return voteItemsAverageItems;
    }

    public void setVoteItemsAverageItems(String voteItemsAverageItems) {
        this.voteItemsAverageItems = voteItemsAverageItems;
    }


    public Boolean getIsfavorite() {
        return isfavorite;
    }

    public void setIsfavorite(Boolean isfavorite) {
        this.isfavorite = isfavorite;
    }

    public MovieData[] newArray(int size){
        return new MovieData[size];
    }
}
