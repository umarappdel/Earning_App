package com.umarappdel.earningapk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

public class YoutubeAdapter extends FirebaseRecyclerAdapter<Youtube, YoutubeAdapter.YoutubeViewfinder> {


    public YoutubeAdapter(
            @NonNull FirebaseRecyclerOptions<Youtube> options)
    {
        super(options);
    }

    @Override
    protected void
    onBindViewHolder(@NonNull final YoutubeViewfinder holder, int position, @NonNull final Youtube model) {

        final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                Log.d("onThumbnailError", "onThumbnailError: Fail");
            }

            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                youTubeThumbnailView.setVisibility(View.VISIBLE);
                holder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
            }
        };

        holder.youTubeThumbnailView.initialize(DeveloperKey.DEVELOPER_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                youTubeThumbnailLoader.setVideo(model.getVideoid());
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);

            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

                Log.d("onInitializationFailure", "onInitializationFailure: Fail");

            }
        });

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = v.getContext();

                Intent intent = new Intent(context,WatchActivity.class);
                intent.putExtra("Video_ID",model.getVideoid());
                context.startActivity(intent);

            }
        });

    }

    @NonNull
    @Override
    public YoutubeViewfinder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list, parent, false);
        return new YoutubeViewfinder(view);
    }

    public static class YoutubeViewfinder extends ViewHolder {

        RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        ImageView playButton;
        YouTubeThumbnailView youTubeThumbnailView;

        public YoutubeViewfinder(@NonNull View itemView) {
            super(itemView);

            relativeLayoutOverYouTubeThumbnailView = itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail);
            playButton = (ImageView) itemView.findViewById(R.id.btnYoutube_player);

        }

    }

}
