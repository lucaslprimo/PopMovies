package br.com.lucaslprimo.popmovies;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import br.com.lucaslprimo.popmovies.utilities.NetworkUtils;

/**
 * Created by Lucas Primo on 08-Dec-17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>{

    private Movie[] mMoviesList;
    private final OnClickListenerMovies mOnClickListenerMovies;

    public MoviesAdapter(OnClickListenerMovies onClickListenerMovies) {
        mOnClickListenerMovies = onClickListenerMovies;
    }

    public void setMoviesList(Movie[] moviesList)
    {
        mMoviesList = moviesList;
        notifyDataSetChanged();
    }

    public Movie getItem(int position)
    {
        return mMoviesList[position];
    }

    interface OnClickListenerMovies
    {
        void OnItemClick(Movie movieClicked, ImageView viewPoster);
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

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView imagePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);

            imagePoster = itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        public void bind(int position)
        {
            Movie movie = mMoviesList[position];

            Picasso.with(itemView.getContext()).load(NetworkUtils.BASE_IMAGE_URL+NetworkUtils.IMAGE_SIZE+ movie.getMoviePoster()).into(imagePoster);
            imagePoster.setContentDescription(movie.getOriginalTitle());
        }

        @Override
        public void onClick(View view) {
            mOnClickListenerMovies.OnItemClick(mMoviesList[getAdapterPosition()], imagePoster);
        }
    }
}
