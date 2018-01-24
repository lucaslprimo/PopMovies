package br.com.lucaslprimo.popmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.lucaslprimo.popmovies.data.MovieContract;
import br.com.lucaslprimo.popmovies.sync.MovieTask;
import br.com.lucaslprimo.popmovies.sync.MoviesIntentService;
import br.com.lucaslprimo.popmovies.utilities.NetworkUtils;
import br.com.lucaslprimo.popmovies.utilities.ReviewJsonUtils;
import br.com.lucaslprimo.popmovies.utilities.VideoJsonUtils;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private RecyclerView mRecyclerViewVideos;
    private RecyclerView mRecyclerViewReviews;
    private ProgressBar mProgressBarVideos;
    private ProgressBar mProgressBarReviews;
    private TextView mReviewsAlert;
    private TextView mVideosAlert;

    private static final int LOADER_REVIEWS_ID = 1;
    private static final int LOADER_VIDEOS_ID = 2;

    private static final String INSTANCE_REVIEWS = "reviews_instance";
    private static final String INSTANCE_VIDEOS = "videos_instance";

    private Snackbar mSnackBar;

    private Review[] mReviews;
    private Video[] mVideos;

    private VideosAdapter mVideosAdapter;
    private ReviewsAdapter mReviewsAdapter;

    private Movie mMovie;
    private MenuItem menuItemStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView mTitle = findViewById(R.id.tv_movie_title);
        TextView mOverview = findViewById(R.id.tv_overview);
        TextView mVoteAverage = findViewById(R.id.tv_vote_average);
        TextView mReleaseDate = findViewById(R.id.tv_release_date);
        ImageView mPoster = findViewById(R.id.iv_poster);
        mRecyclerViewVideos = findViewById(R.id.rv_trailers);
        mRecyclerViewReviews = findViewById(R.id.rv_reviews);

        mProgressBarVideos = findViewById(R.id.progress_bar_videos);
        mProgressBarReviews = findViewById(R.id.progress_bar_reviews);

        mReviewsAlert = findViewById(R.id.tv_reviews_alert);
        mVideosAlert = findViewById(R.id.tv_videos_alert);

        mSnackBar = Snackbar.make(findViewById(R.id.scroll),R.string.label_snack_text,Snackbar.LENGTH_INDEFINITE);
        mSnackBar.setAction(R.string.label_snack_try_again, new TryAgainInternetListener());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerViewVideos.setLayoutManager(layoutManager);
        mRecyclerViewReviews.setLayoutManager(layoutManager2);

        mRecyclerViewVideos.setNestedScrollingEnabled(false);
        mRecyclerViewReviews.setNestedScrollingEnabled(false);

        mRecyclerViewVideos.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerViewReviews.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mRecyclerViewVideos.setHasFixedSize(true);

        mVideosAdapter =  new VideosAdapter();
        mRecyclerViewVideos.setAdapter(mVideosAdapter);

        mReviewsAdapter = new ReviewsAdapter();
        mRecyclerViewReviews.setAdapter(mReviewsAdapter);

        Intent intent = getIntent();

        if(intent.getExtras()!= null)
        {
            if(intent.hasExtra(MainActivity.EXTRA_MOVIE))
            {
                Movie movie = intent.getExtras().getParcelable(MainActivity.EXTRA_MOVIE);
                if(movie !=null) {

                    mMovie = movie;

                    mTitle.setText(movie.getOriginalTitle());
                    mOverview.setText(movie.getOverview());

                    try {
                        Date date =  new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(movie.getReleaseDate());
                        String releaseDate = DateFormat.getDateInstance(DateFormat.LONG).format(date);
                        mReleaseDate.setText(releaseDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Picasso.with(this).load(NetworkUtils.BASE_IMAGE_URL+NetworkUtils.IMAGE_SIZE+ movie.getMoviePoster()).into(mPoster);
                    mVoteAverage.setText(movie.getVoteAverage());


                    if(savedInstanceState !=null && savedInstanceState.containsKey(INSTANCE_REVIEWS))
                    {
                        mVideos = (Video[]) savedInstanceState.getParcelableArray(INSTANCE_VIDEOS);
                        mReviews = (Review[]) savedInstanceState.getParcelableArray(INSTANCE_REVIEWS);

                        if(mReviews != null) {
                            mReviewsAdapter.setArrayReview(mReviews);
                            showDataReviews();
                        }
                        if(mVideos !=null) {
                            mVideosAdapter.setArrayVideo(mVideos);
                            showDataVideos();
                        }
                        if(mVideos == null && mReviews == null)
                            loadOnlineData();
                    }else
                        loadOnlineData();

                    new MovieDbTask().execute();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArray(INSTANCE_REVIEWS,mReviews);
        outState.putParcelableArray(INSTANCE_VIDEOS,mVideos);
        super.onSaveInstanceState(outState);
    }

    public class TryAgainInternetListener implements  View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            loadOnlineData();
        }
    }

    private void loadOnlineData()
    {
        if(NetworkUtils.isOnline(DetailsActivity.this))
        {
            Bundle bundle = new Bundle();
            bundle.putString("id",String.valueOf(mMovie.getId()));

            getSupportLoaderManager().initLoader(LOADER_REVIEWS_ID,bundle,DetailsActivity.this);
            getSupportLoaderManager().initLoader(LOADER_VIDEOS_ID,bundle,DetailsActivity.this);
        }else
            showOfflineMessage();
    }

    private void showOfflineMessage() {

        mRecyclerViewReviews.setVisibility(View.INVISIBLE);
        mReviewsAlert.setText(R.string.unavailable_offline);
        mReviewsAlert.setVisibility(View.VISIBLE);

        mVideosAlert.setVisibility(View.VISIBLE);
        mRecyclerViewVideos.setVisibility(View.INVISIBLE);
        mVideosAlert.setText(R.string.unavailable_offline);
        mVideosAlert.setVisibility(View.VISIBLE);

        mProgressBarVideos.setVisibility(View.INVISIBLE);
        mProgressBarReviews.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSnackBar.show();
            }
        }, 1000);
    }

    private void showLoadingReviews()
    {
        mProgressBarReviews.setVisibility(View.VISIBLE);
        mReviewsAlert.setVisibility(View.INVISIBLE);
        mRecyclerViewReviews.setVisibility(View.INVISIBLE);
    }

    private void showDataReviews()
    {
        mProgressBarReviews.setVisibility(View.INVISIBLE);
        if(mReviews==null || mReviews.length==0)
        {
            mReviewsAlert.setVisibility(View.VISIBLE);
            mRecyclerViewReviews.setVisibility(View.INVISIBLE);
            mReviewsAlert.setText(R.string.empty_content);
        }else {
            mReviewsAlert.setVisibility(View.INVISIBLE);
            mRecyclerViewReviews.setVisibility(View.VISIBLE);
        }
    }

    private void showLoadingVideos()
    {
        mProgressBarVideos.setVisibility(View.VISIBLE);
        mVideosAlert.setVisibility(View.INVISIBLE);
        mRecyclerViewVideos.setVisibility(View.INVISIBLE);
    }

    private void showDataVideos()
    {
        mProgressBarVideos.setVisibility(View.INVISIBLE);
        if(mVideos==null || mVideos.length==0)
        {
            mVideosAlert.setVisibility(View.VISIBLE);
            mRecyclerViewVideos.setVisibility(View.INVISIBLE);
            mVideosAlert.setText(R.string.empty_content);
        }else {
            mVideosAlert.setVisibility(View.INVISIBLE);
            mRecyclerViewVideos.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(NetworkUtils.isOnline(DetailsActivity.this)) {
            showDataVideos();
            showDataReviews();
        }else
        {
            showOfflineMessage();
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {

        switch (id)
        {
            case LOADER_REVIEWS_ID:

                return new AsyncTaskLoader<String>(this) {

                    String jsonReviews;

                    @Override
                    protected void onStartLoading() {
                        showLoadingReviews();

                        if(jsonReviews!=null)
                            deliverResult(jsonReviews);
                        else
                            forceLoad();
                    }

                    @Override
                    public String loadInBackground() {

                        String id  = args.getString("id");

                        URL url = NetworkUtils.buildUrlReviews(id);

                        String result = null;
                        try {
                            result = NetworkUtils.getResponseFromHttpUrl(url);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        return result;
                    }

                    @Override
                    public void deliverResult(String data) {
                        jsonReviews = data;
                        super.deliverResult(data);
                    }
                };


            case LOADER_VIDEOS_ID:

                return new AsyncTaskLoader<String>(this) {

                    String jsonVideos;

                    @Override
                    protected void onStartLoading() {
                        showLoadingVideos();

                        if(jsonVideos!=null)
                            deliverResult(jsonVideos);
                        else
                            forceLoad();
                    }

                    @Override
                    public String loadInBackground() {

                        String id  = args.getString("id");

                        URL url = NetworkUtils.buildUrlVideos(id);

                        String result = null;
                        try {
                            result = NetworkUtils.getResponseFromHttpUrl(url);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return result;
                    }

                    @Override
                    public void deliverResult(String data) {
                        jsonVideos = data;
                        super.deliverResult(data);
                    }
                };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        if(data!=null)
        {

            switch (loader.getId()) {

                case LOADER_REVIEWS_ID:
                    mReviews = ReviewJsonUtils.getReviewsFromJson(data);
                    mReviewsAdapter.setArrayReview(mReviews);

                    showDataReviews();

                    break;
                case LOADER_VIDEOS_ID:
                    mVideos = VideoJsonUtils.getVideosFromJson(data);
                    mVideosAdapter.setArrayVideo(mVideos);

                    showDataVideos();

                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    public class MovieDbTask extends AsyncTask<Void,Void,Cursor>
    {

        @Override
        protected Cursor doInBackground(Void... strings) {

            return getContentResolver()
                    .query(
                            MovieContract.MovieEntrys.CONTENT_URI,
                            null,
                            MovieContract.MovieEntrys.COLUMN_STRING_ID+"=?",
                            new String[]{String.valueOf(mMovie.getId())},
                            null
                    );
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            if(cursor !=null && cursor.moveToFirst())
            {
                mMovie.setDbId(cursor.getInt(cursor.getColumnIndex("_id")));
                mMovie.setFavorite(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntrys.COLUMN_FAVORITE)) == 1);
                if(menuItemStar!=null)
                    updateFavoriteState();

                cursor.close();
            }
        }
    }

    private void updateFavoriteState()
    {
        if(mMovie.isFavorite())
        {
            menuItemStar.setIcon(getResources().getDrawable(R.drawable.ic_star_rate_18px));
        }else
        {
            menuItemStar.setIcon(getResources().getDrawable(R.drawable.ic_star_border_18px));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.details_menu,menu);

        menuItemStar = menu.findItem(R.id.action_favorite);

        updateFavoriteState();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_favorite)
        {
            if(mMovie.isFavorite())
            {
                removeFavorite();
                mMovie.setFavorite(false);
                updateFavoriteState();
            }else
            {
                saveFavorite();
                mMovie.setFavorite(true);
                updateFavoriteState();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveFavorite()
    {
        Intent intent = new Intent(this, MoviesIntentService.class);
        intent.putExtra(Movie.MOVIE_INTENT_ID,String.valueOf(mMovie.getDbId()));
        intent.setAction(MovieTask.ACTION_ADD_FAVORITE);

        startService(intent);
    }

    private void removeFavorite()
    {
        Intent intent = new Intent(this, MoviesIntentService.class);
        intent.putExtra(Movie.MOVIE_INTENT_ID,String.valueOf(mMovie.getDbId()));
        intent.setAction(MovieTask.ACTION_REMOVE_FAVORITE);

        startService(intent);
    }
}
