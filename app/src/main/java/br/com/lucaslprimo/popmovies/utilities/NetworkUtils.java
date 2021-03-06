package br.com.lucaslprimo.popmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import br.com.lucaslprimo.popmovies.BuildConfig;

/**
 * Created by Lucas Primo on 07-Dec-17.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    //API KEY - THE MOVIES DB

    private final static String API_KEY = BuildConfig.API_KEY;

    //BASE API URL
    private final static String BASE_API_URL = "http://api.themoviedb.org/3/";
    //Base query to get popular movies
    private final static String MOVIES_QUERY_BASE = "movie/";

    //The api key to access the api service
    private final static String API_KEY_PARAM = "api_key";
    //Set the order of the movies list
    public final static String ORDER_BY_POPULAR = "popular";
    public final static String ORDER_BY_RATING = "top_rated";
    //videos query
    private final static String VIDEOS_QUERY = "/videos";

    //reviews query
    private final static String REVIEWS_QUERY = "/reviews";

    public final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

    public final static String IMAGE_SIZE = "w500/";



    public static URL buildUrlVideos(String id) {

        Uri builtUri = Uri.parse(String.format("%s%s%s%s",BASE_API_URL,MOVIES_QUERY_BASE,id,VIDEOS_QUERY)).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildUrlReviews(String id) {

        Uri builtUri = Uri.parse(String.format("%s%s%s%s",BASE_API_URL,MOVIES_QUERY_BASE,id,REVIEWS_QUERY)).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildUrlMovies(String orderBy) {

        Uri builtUri = Uri.parse(String.format("%s%s%s",BASE_API_URL,MOVIES_QUERY_BASE,orderBy)).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm!=null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }else
            return false;
    }
}
