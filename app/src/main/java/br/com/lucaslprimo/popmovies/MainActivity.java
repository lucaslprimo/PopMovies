package br.com.lucaslprimo.popmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
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

import br.com.lucaslprimo.popmovies.utilities.MovieJsonUtils;
import br.com.lucaslprimo.popmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnClickListenerMovies{

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
    public final static String INSTANCE_STATE = "arrayMovies";

    private String mSortBy = NetworkUtils.ORDER_BY_POPULAR;

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

    public class TryAgainInternetListener implements  View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            loadMoviesData();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArray(INSTANCE_STATE,mMovies);
        super.onSaveInstanceState(outState);
    }

    private void showErrorMessage(int errorCode)
    {
        if(errorCode == ERROR_NO_INTERNET)
        {
            mErrorImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_perm_scan_wifi_white_48dp));
            mErrorImage.setContentDescription(getString(R.string.label_content_desc_wifi));
            mErrorMessage.setText(R.string.error_no_internet);

        }else
        if(errorCode == ERROR_FETCH_FAILED)
        {
            mErrorImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_white_48dp));
            mErrorImage.setContentDescription(getString(R.string.label_content_desc_error));
            mErrorMessage.setText(R.string.error_fetch_failed);
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

        if(NetworkUtils.isOnline(MainActivity.this))
        {
            new FetchMoviesTask().execute(mSortBy);
        }else
        {
            showErrorMessage(ERROR_NO_INTERNET);
        }
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnItemClick(Movie movieCliked) {
        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra(EXTRA_MOVIE,movieCliked);
        startActivity(intent);
    }

    private class FetchMoviesTask extends AsyncTask<String,Void,Movie[]>
    {
        @Override
        protected void onPreExecute() {

            mLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            String orderBy = params[0];

            URL url = NetworkUtils.buildUrl(orderBy);

            Movie[] movies = null;
            try {
                String result = NetworkUtils.getResponseFromHttpUrl(url);

                movies = MovieJsonUtils.getMoviesFromJson(result);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {

            mLoading.setVisibility(View.INVISIBLE);

            if(movies!=null)
            {
                mMovies = movies;
                mMoviesAdapter.setMoviesList(movies);
                showData();
            }else
                showErrorMessage(ERROR_FETCH_FAILED);

        }
    }
}
