package br.com.lucaslprimo.popmovies.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.lucaslprimo.popmovies.Review;

/**
 * Created by Lucas Primo on 29-Dec-17.
 */

public class ReviewJsonUtils {

    private static final String TAG = ReviewJsonUtils.class.getSimpleName();

    private static final String REVIEW_ID = "id";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";

    public static Review[] getReviewsFromJson(String reviewsJsonString)
    {
        Log.v(TAG, "JSON String " + reviewsJsonString);

        Review[] reviews = null;

        try
        {
            JSONObject reviewsJson = new JSONObject(reviewsJsonString);

            JSONArray results = reviewsJson.getJSONArray("results");

            reviews = new Review[results.length()];

            for(int i = 0; i<results.length();i++)
            {
                JSONObject itemReview = results.getJSONObject(i);

                Review review = new Review();

                review.setId(itemReview.getString(REVIEW_ID));
                review.setAuthor(itemReview.getString(REVIEW_AUTHOR));
                review.setContent(itemReview.getString(REVIEW_CONTENT));

                reviews[i] = review;
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        return reviews;
    }
}
