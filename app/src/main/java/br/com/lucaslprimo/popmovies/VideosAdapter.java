package br.com.lucaslprimo.popmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Lucas Primo on 30-Dec-17.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder>
{
    private Video[] mArrayVideo;

    public void setArrayVideo(Video[] arrayVideo)
    {
        mArrayVideo = arrayVideo;

        notifyDataSetChanged();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_video,parent,false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mArrayVideo== null) return 0;
        return mArrayVideo.length;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder
    {
        final TextView mVideoName;
        String mYoutubeUrl = "";
        final ImageButton mBtnPlay;
        final ImageButton mBtnShare;

        public VideoViewHolder(View itemView) {
            super(itemView);
            mVideoName = itemView.findViewById(R.id.video_name);
            mBtnPlay = itemView.findViewById(R.id.btn_play);
            mBtnShare= itemView.findViewById(R.id.btn_share);

            mBtnPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!mYoutubeUrl.isEmpty())
                        {
                            Context context = view.getContext();
                            Uri uri = Uri.parse(mYoutubeUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW,uri);

                            context.startActivity(intent);
                        }
                    }
                }
            );

            mBtnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!mYoutubeUrl.isEmpty())
                        {
                            Context context = view.getContext();

                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_video_text) + " " + mYoutubeUrl);
                            sendIntent.setType("text/plain");
                            context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_with)));
                        }
                    }
                }
            );
        }

        public void bind(int position)
        {
            mVideoName.setText(mArrayVideo[position].getName());
            mYoutubeUrl = mArrayVideo[position].getYoutubeUrl();
        }
    }
}
