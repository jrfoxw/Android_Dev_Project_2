package Adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.py_dev.movieshow.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Models.MovieData;
import Utils.Constants;


/**
 * Created by PY-DEV on 2/10/2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private int counter;
    private ArrayList<MovieData> data;
    private String [] mItem;
    private LayoutInflater mInflater;




    public ImageAdapter(Context c, int count, ArrayList<MovieData> data){
        this.mContext = c;
        this.counter = count;
        this.data = data;

        mInflater = LayoutInflater.from(mContext);

    }



    @Override
    public int getCount() {
        return counter;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int mCurrOrient = mContext.getResources().getConfiguration().orientation;

        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(mContext);

            mInflater.inflate(R.layout.movie_item_layout, parent, false );


            if(mCurrOrient == Configuration.ORIENTATION_PORTRAIT) {
                imageView.setPadding(5, 5, 5, 5);
            }else{
                imageView.setLayoutParams(new GridView.LayoutParams(195, 225));
                imageView.setPadding(5, 5, 5, 5);


            }



        }else{
            imageView = (ImageView) convertView;
            mInflater.inflate(R.layout.movie_item_layout, parent, false );

        }


        Picasso.with(mContext)
                .load(data.get(position)
                        .getPosterItemLarge()).into(imageView);
        Log.v(Constants.newMovieTag, " IM====> "+ data.get(position).getTitleItem());
        return imageView;

    }

}
