package br.com.lucaslprimo.popmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Lucas Primo on 08-Dec-17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private Movie[] mMoviesList;

    private final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

    private final static String IMAGE_SIZE = "w185/";

    public void setMoviesList(Movie[] moviesList)
    {
        mMoviesList = moviesList;
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =  LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_item_adapter,parent,false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mMoviesList == null) return 0;
        else return mMoviesList.length;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imagePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);

            imagePoster = itemView.findViewById(R.id.iv_poster);
        }

        public void bind(int position)
        {
            Picasso.with(itemView.getContext()).load(BASE_IMAGE_URL+IMAGE_SIZE+mMoviesList[position].getMoviePoster()).into(imagePoster);
        }
    }
}
