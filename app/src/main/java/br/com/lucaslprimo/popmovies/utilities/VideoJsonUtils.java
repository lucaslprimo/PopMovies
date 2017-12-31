package br.com.lucaslprimo.popmovies.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.lucaslprimo.popmovies.Video;

/**
 * Created by Lucas Primo on 29-Dec-17.
 */

public class VideoJsonUtils {

    private static final String TAG = VideoJsonUtils.class.getSimpleName();

    private static final String VIDEO_ID = "id";
    private static final String VIDEO_KEY = "key";
    private static final String VIDEO_NAME = "name";
    private static final String VIDEO_TYPE = "type";

    public static Video[] getVideosFromJson(String reviewsJsonString)
    {
        Log.v(TAG, "JSON String " + reviewsJsonString);

        Video[] videos = null;

        try
        {
            JSONObject videosJson = new JSONObject(reviewsJsonString);

            JSONArray results = videosJson.getJSONArray("results");

            videos = new Video[results.length()];

            for(int i = 0; i<results.length();i++)
            {
                JSONObject itemReview = results.getJSONObject(i);

                Video video = new Video();

                video.setId(itemReview.getString(VIDEO_ID));
                video.setKey(itemReview.getString(VIDEO_KEY));
                video.setName(itemReview.getString(VIDEO_NAME));
                video.setType(itemReview.getString(VIDEO_TYPE));

                videos[i] = video;
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        return videos;
    }
}
