package br.com.lucaslprimo.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucas Primo on 30-Dec-17.
 */

class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pop_movies.db";
    private static final int VERSION_CODE = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+ MovieContract.MovieEntrys.TABLE_NAME + "(" +
                MovieContract.MovieEntrys._ID +" INTEGER PRIMARY KEY," +
                MovieContract.MovieEntrys.COLUMN_STRING_ID +" TEXT NOT NULL," +
                MovieContract.MovieEntrys.COLUMN_TITLE +" TEXT NOT NULL," +
                MovieContract.MovieEntrys.COLUMN_OVERVIEW +" TEXT NOT NULL," +
                MovieContract.MovieEntrys.COLUMN_POPULARITY +" REAL NOT NULL," +
                MovieContract.MovieEntrys.COLUMN_POSTER +" TEXT NOT NULL, " +
                MovieContract.MovieEntrys.COLUMN_VOTE_AVERAGE +" REAL NOT NULL," +
                MovieContract.MovieEntrys.COLUMN_RELEASE_DATE +" DATE NOT NULL," +
                MovieContract.MovieEntrys.COLUMN_FAVORITE +" INTEGER NOT NULL DEFAULT 0 )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MovieContract.MovieEntrys.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
