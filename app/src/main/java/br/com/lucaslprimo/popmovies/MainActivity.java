package br.com.lucaslprimo.popmovies;

import android.os.AsyncTask;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private ProgressBar mLoading;
    private LinearLayout mErrorView;
    private ImageView mErrorImage;
    private TextView mErrorMessage;

    private final static int numColumnsGridView = 2;

    private final static int ERROR_NO_INTERNET = 1;
    private final static int ERROR_FETCH_FAILED = 2;

    private String mSortBy = NetworkUtils.ORDER_BY_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_movies);

        mLoading = findViewById(R.id.pb_loading);

        mErrorView = findViewById(R.id.error_view);
        mErrorImage = findViewById(R.id.iv_error);
        mErrorMessage = findViewById(R.id.tv_error_message);

        mMoviesAdapter = new MoviesAdapter();
        mRecyclerView.setAdapter(mMoviesAdapter);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,numColumnsGridView);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        loadMoviesData();
    }

    private void showErrorMessage(int errorCode)
    {
        if(errorCode == ERROR_NO_INTERNET)
        {
            mErrorImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_perm_scan_wifi_white_48dp));
            mErrorMessage.setText(R.string.error_no_internet);
        }else
        if(errorCode == ERROR_FETCH_FAILED)
        {
            mErrorImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_white_48dp));
            mErrorMessage.setText(R.string.error_fetch_failed);
        }

        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void showData()
    {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }


    private void loadMoviesData()
    {
        mMoviesAdapter.setMoviesList(null);

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
                mMoviesAdapter.setMoviesList(movies);
                showData();
            }else
                showErrorMessage(ERROR_FETCH_FAILED);

        }
    }
}
