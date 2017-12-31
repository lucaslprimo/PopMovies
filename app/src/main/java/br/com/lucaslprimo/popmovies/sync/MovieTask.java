package br.com.lucaslprimo.popmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Parcelable;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import br.com.lucaslprimo.popmovies.Movie;
import br.com.lucaslprimo.popmovies.data.MovieContract;


/**
 * Created by Lucas Primo on 30-Dec-17.
 */

public class MovieTask {

    private static final String TAG = MovieTask.class.getSimpleName();

    public static final String ACTION_ADD_FAVORITE = "add_favorite";

    public static final String ACTION_REMOVE_FAVORITE = "remove_favorite";

    public static final String ACTION_SYNC_MOVIES = "sync_movies";

    public static void executeTask(String action, Intent intent, final Context context)
    {
        if(ACTION_ADD_FAVORITE.equals(action))
        {
            String id = intent.getStringExtra(Movie.MOVIE_INTENT_ID);

            ContentValues values = new ContentValues();

            values.put(MovieContract.MovieEntrys.COLUMN_FAVORITE,true);

            long updated = context.getContentResolver().update(MovieContract.MovieEntrys.CONTENT_URI,values,"_id=?",new String[]{id});
            if(updated > 0)
            {
                Log.v(TAG,"Updated");
            }else
                throw new SQLException("Failed to update");
        }else
        if(ACTION_REMOVE_FAVORITE.equals(action))
        {
            String id = intent.getStringExtra(Movie.MOVIE_INTENT_ID);

            ContentValues values = new ContentValues();

              values.put(MovieContract.MovieEntrys.COLUMN_FAVORITE,false);

            long updated = context.getContentResolver().update(MovieContract.MovieEntrys.CONTENT_URI,values,"_id=?",new String[]{id});
            if(updated > 0)
            {
                Log.v(TAG,"Updated");
            }else
                throw new SQLException("Failed to update");
        }else
        if(ACTION_SYNC_MOVIES.equals(action))
        {
            Parcelable[] arrayMovies = intent.getParcelableArrayExtra(Movie.MOVIE_INTENT);

            List<ContentValues> listValues = new ArrayList<>();

            for(final Parcelable parcelable:arrayMovies)
            {
                final Movie movie = (Movie) parcelable;

                ContentValues values = new ContentValues();

                values.put(MovieContract.MovieEntrys.COLUMN_TITLE,movie.getOriginalTitle());
                values.put(MovieContract.MovieEntrys.COLUMN_OVERVIEW,movie.getOverview());
                values.put(MovieContract.MovieEntrys.COLUMN_POSTER,movie.getMoviePoster());
                values.put(MovieContract.MovieEntrys.COLUMN_VOTE_AVERAGE,movie.getVoteAverage());
                values.put(MovieContract.MovieEntrys.COLUMN_POPULARITY,movie.getPopularity());
                values.put(MovieContract.MovieEntrys.COLUMN_RELEASE_DATE,movie.getReleaseDate());
                values.put(MovieContract.MovieEntrys.COLUMN_STRING_ID,String.valueOf(movie.getId()));

                Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntrys.CONTENT_URI,new String[]{},MovieContract.MovieEntrys.COLUMN_STRING_ID+"=?",new String[]{String.valueOf(movie.getId())},null);

                if(cursor!=null && cursor.moveToFirst())
                {
                    context.getContentResolver().update(MovieContract.MovieEntrys.CONTENT_URI,values,MovieContract.MovieEntrys.COLUMN_STRING_ID+"=?",new String[]{String.valueOf(movie.getId())});

                    cursor.close();
                }else
                {
                    listValues.add(values);
                }


            }

            if(!listValues.isEmpty()) {
                ContentValues[] arrayValues = new ContentValues[listValues.size()];
                context.getContentResolver().bulkInsert(MovieContract.MovieEntrys.CONTENT_URI, listValues.toArray(arrayValues));
            }
        }
    }
}
