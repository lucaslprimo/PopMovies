package br.com.lucaslprimo.popmovies.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.lucaslprimo.popmovies.Movie;

/**
 * Created by Lucas Primo on 07-Dec-17.
 */

public class MovieJsonUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_ORIGINAL_TITLE = "original_title";
    private static final String MOVIE_POSTER_PATH = "poster_path";
    private static final String MOVIE_VOTE_AVERAGE = "vote_average";
    private static final String MOVIE_OVERVIEW = "overview";
    private static final String MOVIE_RELEASE_DATE = "release_date";

    public static Movie[] getMoviesFromJson(String moviesJsonString)
    {
        Log.v(TAG, "JSON String " + moviesJsonString);

        Movie[] moviesArray = null;

        try {
            JSONObject moviesJson = new JSONObject(moviesJsonString);

            JSONArray results = moviesJson.getJSONArray("results");

            moviesArray = new Movie[results.length()];

            for(int i = 0; i<results.length();i++)
            {
                JSONObject itemMovie = results.getJSONObject(i);

                Movie movie = new Movie();

                movie.setOriginalTitle(itemMovie.getString(MOVIE_ORIGINAL_TITLE));
                movie.setMoviePoster(itemMovie.getString(MOVIE_POSTER_PATH));
                movie.setOverview(itemMovie.getString(MOVIE_OVERVIEW));
                movie.setVoteAverage(itemMovie.getString(MOVIE_VOTE_AVERAGE));
                movie.setReleaseDate(itemMovie.getString(MOVIE_RELEASE_DATE));

                moviesArray[i] = movie;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return moviesArray;
    }
}
