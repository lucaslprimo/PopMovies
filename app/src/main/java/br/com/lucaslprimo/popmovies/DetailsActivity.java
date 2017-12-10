package br.com.lucaslprimo.popmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.lucaslprimo.popmovies.utilities.NetworkUtils;

public class DetailsActivity extends AppCompatActivity {

    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private ImageView mPoster;
    private TextView mOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mTitle = findViewById(R.id.tv_movie_title);
        mOverview = findViewById(R.id.tv_overview);
        mVoteAverage = findViewById(R.id.tv_vote_average);
        mReleaseDate = findViewById(R.id.tv_release_date);
        mPoster = findViewById(R.id.iv_poster);

        Intent intent = getIntent();

        if(intent.getExtras()!= null)
        {
            if(intent.hasExtra(MainActivity.EXTRA_MOVIE))
            {
                Movie movie = intent.getExtras().getParcelable(MainActivity.EXTRA_MOVIE);
                if(movie!=null) {
                    mTitle.setText(movie.getOriginalTitle());
                    mOverview.setText(movie.getOverview());

                    try {
                        Date date =  new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(movie.getReleaseDate());
                        String releaseDate = DateFormat.getDateInstance(DateFormat.LONG).format(date) ;
                        mReleaseDate.setText(releaseDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    Picasso.with(this).load(NetworkUtils.BASE_IMAGE_URL+NetworkUtils.IMAGE_SIZE+movie.getMoviePoster()).into(mPoster);
                    mVoteAverage.setText(movie.getVoteAverage());
                }
            }
        }
    }
}
