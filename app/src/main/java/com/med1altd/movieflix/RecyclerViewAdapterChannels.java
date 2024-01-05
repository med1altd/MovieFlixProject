package com.med1altd.movieflix;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.Serializable;
import java.util.ArrayList;

public class RecyclerViewAdapterChannels extends RecyclerView.Adapter<RecyclerViewAdapterChannels.MyViewHolder> {

    Context context;

    private static ArrayList<Channel>
            channelList = new ArrayList<>();
    private boolean
            isClicked = false,
            isWorking,
            MoreContentOption = false;

    private static final int
            VIEW_TYPE_UNLOCKED = 0,
            VIEW_TYPE_LOCKED = 1,
            VIEW_TYPE_LAST = 2;

    public RecyclerViewAdapterChannels(Context ct, ArrayList<Channel> ChannelList) {

        context = ct;

        channelList = ChannelList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        isWorking = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isWorking", false);

        MoreContentOption = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("MoreContentOption", false);

        switch (viewType) {

            case VIEW_TYPE_LOCKED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_channels_locked, parent, false);

                break;

            case VIEW_TYPE_UNLOCKED:
            case VIEW_TYPE_LAST:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_channels, parent, false);

                break;

            default:

                throw new IllegalArgumentException("Invalid view type: " + viewType);

        }

        Boolean isAndroidTV = new Help().isAndroidTV(context);

        if (isAndroidTV) {

            view.setFocusable(true);
            view.setFocusableInTouchMode(true);

        } else {

            view.setFocusable(false);
            view.setFocusableInTouchMode(false);

        }

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int pos = position;

        String
                ImageUrl;

        if (position == channelList.size()) {

            ImageUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("MoreImageUrl", "https://i.ibb.co/nRkWFQG/MORE.png");

        } else {

            ImageUrl = channelList.get(position).getImage();

        }

        new Help().showImage(ImageUrl, holder.imageMain, holder.progressBar, context);

        int viewType = getItemViewType(position);

        switch (viewType) {

            case VIEW_TYPE_UNLOCKED:

                new Help().setupAnimationOnClick(holder.itemView, 0.9f, new OnViewClickListener() {
                    @Override
                    public void onClick() {

                        if ((!isClicked) && isWorking) {

                            isClicked = true;

                            new Help().pushStringToFirebase(context, "TV", channelList.get(pos).Title);

                            Intent intent = new Intent(context, LiveStreamingActivity.class);

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            intent.putExtra("channel", (Serializable) channelList.get(pos));

                            context.startActivity(intent);

                            holder.progressBar.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    isClicked = false;

                                }

                            }, 500);

                        } else if (!isWorking) {

                            Toast.makeText(context, "Μη Διαθέσιμο!", Toast.LENGTH_SHORT).show();

                        }

                    }

                });

                break;

            case VIEW_TYPE_LOCKED:

                break;

            case VIEW_TYPE_LAST:

                if (MoreContentOption) {

                    new Help().setupAnimationOnClick(holder.itemView, 0.9f, new OnViewClickListener() {
                        @Override
                        public void onClick() {

                            if (!isClicked) {

                                isClicked = true;

                                if (context instanceof MainActivity) {

                                    MainActivity mainActivity = (MainActivity) context;

                                    new Help().showCustomDialogBox(mainActivity, "Hello!");

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

        return channelList.size() + 1;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageMain;

        RelativeLayout lockedLayout;

        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageMain = itemView.findViewById(R.id.imageMain);

            lockedLayout = itemView.findViewById(R.id.LockedLayout);

            progressBar = itemView.findViewById(R.id.progressBar);

        }

    }

    @Override
    public int getItemViewType(int position) {

        if (position < channelList.size()) {

            if (channelList.get(position).getUnlocked()) {

                return VIEW_TYPE_UNLOCKED;

            } else {

                return VIEW_TYPE_LOCKED;

            }

        } else {

            return VIEW_TYPE_LAST;

        }

    }

}