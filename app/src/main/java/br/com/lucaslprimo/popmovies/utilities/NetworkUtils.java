package br.com.lucaslprimo.popmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Lucas Primo on 07-Dec-17.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    //API KEY - THE MOVIES DB
    private final static String API_KEY = "[API_KEY]";
    //BASE API URL
    private final static String BASE_API_URL = "http://api.themoviedb.org/3/";
    //Base query to get popular movies
    private final static String MOVIES_QUERY_BASE = "movie/popular";

    //The api key to access the api service
    private final static String API_KEY_PARAM = "api_key";
    //Set the order of the movies list
    private final static String ORDER_BY_PARAM = "order_by";

    private final static String ORDER_BY_POPULAR = "popularity";
    private final static String ORDER_BY_RATING = "rating";


    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param locationQuery The location that will be queried for.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(String locationQuery, String orderBy) {
        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(ORDER_BY_PARAM, orderBy)
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
}
