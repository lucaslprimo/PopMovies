package br.com.lucaslprimo.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Lucas Primo on 30-Dec-17.
 */

public class MoviesContentProvider extends ContentProvider {

    private MoviesDbHelper mDbHelper;

    private static final int URI_MOVIES = 100;
    private static final int URI_MOVIES_WITH_ID = 101;

    private final UriMatcher sUriMatcher = buildURIMatcher();

    private static UriMatcher buildURIMatcher()
    {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MOVIES_PATH, URI_MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MOVIES_PATH+"/#", URI_MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        mDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase db =  mDbHelper.getReadableDatabase();

        Cursor cursor;

        switch (sUriMatcher.match(uri))
        {
            case URI_MOVIES:

                cursor = db.query(MovieContract.MovieEntrys.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

                break;


            case URI_MOVIES_WITH_ID:

                String id = uri.getLastPathSegment();
                cursor = db.query(MovieContract.MovieEntrys.TABLE_NAME,projection,"_ID=?",new String[]{id},null,null,sortOrder);

                if(getContext()!=null)
                    getContext().getContentResolver().notifyChange(uri,null);

                break;

            default:
                throw new UnsupportedOperationException("URI Unknown "+uri.toString());

        }

        getContext().getContentResolver().notifyChange(uri,null);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Uri returnUri;

        switch (sUriMatcher.match(uri))
        {
            case URI_MOVIES:

                long id = db.insert(MovieContract.MovieEntrys.TABLE_NAME,null,contentValues);
                if(id > 0)
                    returnUri = ContentUris.withAppendedId(MovieContract.BASE_CONTENT_URI,id);
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("URI Unknown "+uri.toString());
        }

        if(getContext()!=null)
            getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int deleted;

        switch (sUriMatcher.match(uri))
        {
            case URI_MOVIES:

                deleted = db.delete(MovieContract.MovieEntrys.TABLE_NAME,selection,selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("URI Unknown "+uri.toString());
        }

        if(deleted > 0) {
            if(getContext()!=null)
                getContext().getContentResolver().notifyChange(uri,null);
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int updated;

        switch (sUriMatcher.match(uri))
        {
            case URI_MOVIES:

                updated = db.update(MovieContract.MovieEntrys.TABLE_NAME,contentValues,selection,selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("URI Unknown "+uri.toString());
        }
        if(updated > 0)
        {
            if(getContext()!=null)
                getContext().getContentResolver().notifyChange(uri,null);
        }

        return updated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case URI_MOVIES:

                db.beginTransaction();
                int rowsInserted = 0;

                try{
                    for (ContentValues value:values)
                    {
                        long id = db.insert(MovieContract.MovieEntrys.TABLE_NAME,null,value);

                        if(id>0)
                            rowsInserted++;
                    }

                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }

                if(rowsInserted > 0){
                    if(getContext()!=null)
                        getContext().getContentResolver().notifyChange(uri,null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
