package br.com.lucaslprimo.popmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;


import br.com.lucaslprimo.popmovies.data.MovieContract;
import br.com.lucaslprimo.popmovies.sync.MovieTask;
import br.com.lucaslprimo.popmovies.sync.MoviesIntentService;
import br.com.lucaslprimo.popmovies.utilities.MovieJsonUtils;
import br.com.lucaslprimo.popmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnClickListenerMovies, LoaderManager.LoaderCallbacks<Movie[]>{

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private ProgressBar mLoading;
    private LinearLayout mErrorView;
    private ImageView mErrorImage;
    private TextView mErrorMessage;
    private Movie[] mMovies;

    private Snackbar mSnackBar;

    private final static int NUM_COLUMNS_VIEWS = 2;

    private final static int ERROR_NO_INTERNET = 1;
    private final static int ERROR_FETCH_FAILED = 2;

    public final static String EXTRA_MOVIE = "movie";
    private final static String INSTANCE_STATE = "arrayMovies";

    private String mSortBy = NetworkUtils.ORDER_BY_POPULAR;

    private final static int LOADER_MOVIES_WEB_ID = 1;
    private final static int LOADER_MOVIES_LOCAL_ID = 2;

    private final static String ORDER_BY_FAVORITES = "favorites";

    private boolean mLoaderWebStarted = false;
    private boolean mLoaderLocalStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_movies);

        mSnackBar = Snackbar.make(mRecyclerView,R.string.label_snack_text,Snackbar.LENGTH_INDEFINITE);
        mSnackBar.setAction(R.string.label_snack_try_again, new TryAgainInternetListener());

        mLoading = findViewById(R.id.pb_loading);

        mErrorView = findViewById(R.id.error_view);
        mErrorImage = findViewById(R.id.iv_error);
        mErrorMessage = findViewById(R.id.tv_error_message);

        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,NUM_COLUMNS_VIEWS);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        if(savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_STATE))
        {
            showData();
            mMovies = (Movie[]) savedInstanceState.getParcelableArray(INSTANCE_STATE);

            if(mMovies != null)
                mMoviesAdapter.setMoviesList(mMovies);
            else
                loadMoviesData();
        }else
            loadMoviesData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mSortBy.equals(ORDER_BY_FAVORITES))
        {
            loadMoviesData();
        }
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int id, final Bundle args) {

        if(id == LOADER_MOVIES_WEB_ID) {

            return new AsyncTaskLoader<Movie[]>(this) {

                Movie[] mMovies;

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();

                    if (mMovies != null)
                        deliverResult(mMovies);
                    else
                        forceLoad();
                }

                @Override
                public Movie[] loadInBackground() {

                    String orderBy = args.getString("orderBy");

                    URL url = NetworkUtils.buildUrlMovies(orderBy);

                    Movie[] movies = null;
                    try {
                        String result = NetworkUtils.getResponseFromHttpUrl(url);

                        if (result != null)
                            movies = MovieJsonUtils.getMoviesFromJson(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return movies;
                }

                @Override
                public void deliverResult(Movie[] data) {
                    mMovies = data;
                    super.deliverResult(data);
                }
            };
        }else
            if(id == LOADER_MOVIES_LOCAL_ID)
            {
                return new AsyncTaskLoader<Movie[]>(this) {



                    Movie[] mMovies;

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();

                        if (mMovies != null)
                            deliverResult(mMovies);
                        else
                            forceLoad();
                    }

                    @Override
                    public Movie[] loadInBackground() {

                        String orderBy = args.getString("orderBy");

                        Movie[] movies = null;
                        Cursor cursor;

                        String orderByColumn;
                        String selection = null;

                        if(orderBy!=null)
                        {
                            if(orderBy.equals(NetworkUtils.ORDER_BY_POPULAR))
                                orderByColumn = MovieContract.MovieEntrys.COLUMN_POPULARITY + " DESC";
                            else
                            if(orderBy.equals(NetworkUtils.ORDER_BY_RATING))
                                orderByColumn = MovieContract.MovieEntrys.COLUMN_VOTE_AVERAGE + " DESC";
                            else
                            {
                                selection = MovieContract.MovieEntrys.COLUMN_FAVORITE+"=1";
                                orderByColumn = MovieContract.MovieEntrys.COLUMN_TITLE + " ASC";
                            }

                            cursor = getContentResolver().query(MovieContract.MovieEntrys.CONTENT_URI,null, selection,null, orderByColumn);

                            movies = MovieContract.getMoviesFromCursor(cursor);
                        }

                        return movies;
                    }

                    @Override
                    public void deliverResult(Movie[] data) {
                        mMovies = data;
                        super.deliverResult(data);
                    }
                };
            }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] movies) {

        mLoading.setVisibility(View.INVISIBLE);

        if(movies !=null)
        {
            mMovies = movies;
            mMoviesAdapter.setMoviesList(movies);
            showData();

            runSyncService(mMovies);

        }else
            showMessage(ERROR_FETCH_FAILED);

    }

    private void runSyncService(Movie[] arrayMovies)
    {
        Intent intent = new Intent(this, MoviesIntentService.class);
        intent.putExtra(Movie.MOVIE_INTENT,arrayMovies);
        intent.setAction(MovieTask.ACTION_SYNC_MOVIES);

        startService(intent);
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }

    public class TryAgainInternetListener implements  View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            loadMoviesData();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArray(INSTANCE_STATE, mMovies);
        super.onSaveInstanceState(outState);
    }

    private void showMessage(int errorCode)
    {
        if(errorCode == ERROR_NO_INTERNET)
        {
            mErrorImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_perm_scan_wifi_48px));
            mErrorImage.setContentDescription(getString(R.string.label_content_desc_wifi));
            mErrorMessage.setText(R.string.error_no_internet);
            mSnackBar.setText(R.string.error_no_internet);

        }else
        if(errorCode == ERROR_FETCH_FAILED)
        {
            mErrorImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_48px));
            mErrorImage.setContentDescription(getString(R.string.label_content_desc_error));
            mErrorMessage.setText(R.string.error_fetch_failed);
            mSnackBar.setText(R.string.error_fetch_failed_snack);

        }

        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSnackBar.show();
            }
        }, 1000);
    }

    private void showData()
    {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    private void loadMoviesData()
    {
        mMoviesAdapter.setMoviesList(null);
        showData();

        mLoading.setVisibility(View.VISIBLE);

        if(NetworkUtils.isOnline(MainActivity.this))
        {
            if(mSortBy.equals(ORDER_BY_FAVORITES))
            {
                runLoaderLocal();
            }else {
               runLoaderWeb();
            }

        }else
        {
            showMessage(ERROR_NO_INTERNET);
            runLoaderLocal();
        }
    }

    private void runLoaderWeb()
    {
        Bundle bundle = new Bundle();
        bundle.putString("orderBy", mSortBy);

        if (!mLoaderWebStarted) {
            getSupportLoaderManager().initLoader(LOADER_MOVIES_WEB_ID, bundle, this);
            mLoaderWebStarted = true;
        } else
            getSupportLoaderManager().restartLoader(LOADER_MOVIES_WEB_ID, bundle, this);
    }

    private void runLoaderLocal()
    {
        Bundle bundle = new Bundle();
        bundle.putString("orderBy", mSortBy);
        if (!mLoaderLocalStarted) {
            getSupportLoaderManager().initLoader(LOADER_MOVIES_LOCAL_ID, bundle, this);
            mLoaderLocalStarted = true;
        } else
            getSupportLoaderManager().restartLoader(LOADER_MOVIES_LOCAL_ID, bundle, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_sort_by_popular)
        {
            mSortBy = NetworkUtils.ORDER_BY_POPULAR;
            loadMoviesData();
        }

        if(item.getItemId() == R.id.action_sort_by_rating)
        {
            mSortBy = NetworkUtils.ORDER_BY_RATING;
            loadMoviesData();
        }

        if(item.getItemId() == R.id.action_sort_by_favorites)
        {
            mSortBy = ORDER_BY_FAVORITES;
            loadMoviesData();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnItemClick(Movie movieCliked) {
        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra(EXTRA_MOVIE, movieCliked);
        startActivity(intent);
    }
}
