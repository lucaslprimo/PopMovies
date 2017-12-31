package br.com.lucaslprimo.popmovies.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import br.com.lucaslprimo.popmovies.Movie;

/**
 * Created by Lucas Primo on 30-Dec-17.
 */

public class MovieContract
{
    public static final String AUTHORITY= "br.com.lucaslprimo.popmovies";

    public static final String MOVIES_PATH= "movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);

    public final static class MovieEntrys implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_STRING_ID = "id_string";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_FAVORITE = "favorite";
    }

    public static Movie[] getMoviesFromCursor(Cursor cursor)
    {
        if(cursor!=null && cursor.moveToFirst())
        {
            Movie[] movies = new Movie[cursor.getCount()];

            for (int i=0; i<cursor.getCount();i++)
            {
                Movie movie = new Movie();

                movie.setDbId(cursor.getInt(cursor.getColumnIndex(MovieEntrys._ID)));
                movie.setId(cursor.getInt(cursor.getColumnIndex(MovieEntrys.COLUMN_STRING_ID)));
                movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MovieEntrys.COLUMN_TITLE)));
                movie.setMoviePoster(cursor.getString(cursor.getColumnIndex(MovieEntrys.COLUMN_POSTER)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieEntrys.COLUMN_OVERVIEW)));
                movie.setPopularity(String.valueOf(cursor.getFloat(cursor.getColumnIndex(MovieEntrys.COLUMN_POPULARITY))));
                movie.setVoteAverage(String.valueOf(cursor.getFloat(cursor.getColumnIndex(MovieEntrys.COLUMN_VOTE_AVERAGE))));
                movie.setReleaseDate(String.valueOf(cursor.getString(cursor.getColumnIndex(MovieEntrys.COLUMN_RELEASE_DATE))));
                movie.setFavorite(cursor.getInt(cursor.getColumnIndex(MovieEntrys.COLUMN_FAVORITE))==1);

                movies[i]=movie;

                cursor.moveToNext();
            }

            return movies;
        }

        return null;

    }
}
