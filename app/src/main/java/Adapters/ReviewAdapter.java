package Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.py_dev.movieshow.R;

import java.util.ArrayList;

import Models.ReviewsData;
import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by PY-DEV on 2/28/2016.
 */
public class ReviewAdapter extends ArrayAdapter<ReviewsData>  {



    public ReviewAdapter(Context context, ArrayList<ReviewsData> resource) {
        super(context, R.layout.reviews_details, resource);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.reviews_details, parent, false);
            String textAuthor = getItem(position).getAuthor();
            String textReview = getItem(position).getReview();


            TextView textAuthorView = (TextView) view.findViewById(R.id.authorView);
            TextView textReviewView = (TextView) view.findViewById(R.id.contentView);

            textAuthorView.setText(textAuthor);




            textReviewView.setText(textReview);


            return view;
        }

        return convertView;
    }


}
