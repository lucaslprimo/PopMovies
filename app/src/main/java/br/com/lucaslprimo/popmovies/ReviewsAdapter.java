package br.com.lucaslprimo.popmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

/**
 * Created by Lucas Primo on 30-Dec-17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>
{
    private Review[] mArrayReview;

    public void setArrayReview(Review[] arrayReview)
    {
        mArrayReview = arrayReview;

        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_review,parent,false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mArrayReview == null) return 0;
        return mArrayReview.length;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder
    {
       final TextView mAuthor;
       final TextView mContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthor = itemView.findViewById(R.id.author);
            mContent = itemView.findViewById(R.id.content);

        }

        public void bind(int position)
        {
            mAuthor.setText(mArrayReview[position].getAuthor());
            mContent.setText(mArrayReview[position].getContent());
        }
    }
}
