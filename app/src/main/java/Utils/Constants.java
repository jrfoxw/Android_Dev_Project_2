package Utils;

/**
 * Created by PY-DEV on 2/29/2016.
 */
public class Constants {


    // JSON CONSTANTS **************************************************************************
    public static final String API_KEY = "API GOES HERE";
    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/popular"+API_KEY;
    public static final String SINGLE_MOVIE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String SORT_BY_VOTES = "http://api.themoviedb.org/3/movie/top_rated"+API_KEY+"&sort_by=vote_count.desc";
    public static final String POSTER_URL_185 = "http://image.tmdb.org/t/p/w185";
    public static final String POSTER_URL_342 = "http://image.tmdb.org/t/p/w342";
    //******************************************************************************************


    // SHARED PREFERENCES  ************************************************
    public static final String TEMPDATA = "tempData";
    //*********************************************************************


    // DATA BASE CONSTANTS SQLITE ONLY ************************************************
    public static final String TABLE_NAME = "movie";
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String POSTER = "poster";
    public static final String POSTERLARGE = "posterlarge";
    public static final String OVERVIEW = "overview";
    public static final String RELEASEDATE = "release";
    public static final String POPULARITY = "popularity";
    public static final String VOTES = "votes";
    public static final String VOTES_AVERAGE = "votesAverage";
    //********************************************************************************


    // TAGS ******************************************************
    public static final String newMovieTag = "TAG--> newMovie";
    //************************************************************


    // RESPONSE MESSAGES *************************************************
    public static final String[] ERROR_TEXT = {"No online connection, attempting to pull mData from last use...",
            "No online connection, app requires online connection for first initial use. exiting in "};
    public static final String NOFAVORITES = "You have not selected any favorites, " +
            "click on the star while viewing the details, " +
            "to select a movie as a favorite.";
    //********************************************************************

}
