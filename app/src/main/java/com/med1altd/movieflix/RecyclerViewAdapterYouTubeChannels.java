package com.med1altd.movieflix;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerViewAdapterYouTubeChannels extends RecyclerView.Adapter<RecyclerViewAdapterYouTubeChannels.MyViewHolder> {

    Context context;

    static ArrayList<YouTubeChannel>
            YouTubeChannels = new ArrayList<>();

    private boolean
            isClicked = false,
            isWorking = false,
            MoreContentOption = false;
    private static final int
            VIEW_TYPE_UNLOCKED = 0,
            VIEW_TYPE_LOCKED = 1,
            VIEW_TYPE_LAST = 2;

    public RecyclerViewAdapterYouTubeChannels(Context ct, ArrayList<YouTubeChannel> youTubeChannels) {

        context = ct;

        YouTubeChannels = youTubeChannels;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        isWorking = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isWorking", false);

        MoreContentOption = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("MoreContentOption", false);

        switch (viewType) {

            case VIEW_TYPE_LOCKED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_youtube_channels_locked, parent, false);

                break;

            case VIEW_TYPE_UNLOCKED:
            case VIEW_TYPE_LAST:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_youtube_channels, parent, false);

                break;

            default:

                throw new IllegalArgumentException("Invalid view type: " + viewType);

        }

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int pos = position;

        String
                ImageUrl;

        if (YouTubeChannels.get(position) == null) {

            ImageUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("MoreUrl", "https://i.ibb.co/DtDR2cp/More.jpg");

        } else {

            ImageUrl = YouTubeChannels.get(position).getImage();

        }

        new Help().showImage(ImageUrl, holder.imageMain, holder.progressBar, context);

        int viewType = getItemViewType(position);

        switch (viewType) {

            case VIEW_TYPE_UNLOCKED:

                // Bind data for unlocked item

                // ((UnlockedViewHolder) holder).bindData(data.get(position));

                holder.title.setText(YouTubeChannels.get(pos).getTitle());

                holder.lockedLayout.setVisibility(View.GONE);

                new Help().setupAnimationOnClick(holder.itemView, 0.9f, new OnViewClickListener() {
                    @Override
                    public void onClick() {

                        if (!isClicked) {

                            isClicked = true;

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(YouTubeChannels.get(pos).getUrl()));
                            context.startActivity(intent);

                            holder.progressBar.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isClicked = false;
                                }
                            }, 500);

                        }

                    }

                });

                break;

            case VIEW_TYPE_LOCKED:

                holder.title.setText(YouTubeChannels.get(pos).getTitle());

                break;

            case VIEW_TYPE_LAST:

                if (MoreContentOption) {

                    new Help().setupAnimationOnClick(holder.itemView, 0.9f, new OnViewClickListener() {
                        @Override
                        public void onClick() {

                            if (!isClicked) {

                                isClicked = true;

                                if (context instanceof YouTubeChannelsActivity) {

                                    YouTubeChannelsActivity youTubeChannelsActivity = (YouTubeChannelsActivity) context;

                                    new Help().showCustomDialogBox(youTubeChannelsActivity, "Hello!");

                                    // Now you can use 'activity' to access methods or variables in your activity

                                    // For example, you can start a new activity or perform other actions

                                }

                                holder.progressBar.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        isClicked = false;

                                    }

                                }, 500);

                            }

                        }

                    });

                } else {

                    holder.itemView.setVisibility(View.GONE);

                }

                break;

            // Add more cases for additional view types

        }

    }

    @Override
    public int getItemCount() {

        return YouTubeChannels.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        ImageView imageMain;

        RelativeLayout lockedLayout;

        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textTitle);

            imageMain = itemView.findViewById(R.id.imageMain);

            lockedLayout = itemView.findViewById(R.id.LockedLayout);

            progressBar = itemView.findViewById(R.id.progressBar);

        }

    }

    @Override
    public int getItemViewType(int position) {

        if (YouTubeChannels.get(position) != null) {

            if (YouTubeChannels.get(position).getUnlocked()) {

                return VIEW_TYPE_UNLOCKED;

            } else {

                return VIEW_TYPE_LOCKED;

            }

        } else {

            return VIEW_TYPE_LAST;

        }

    }

}