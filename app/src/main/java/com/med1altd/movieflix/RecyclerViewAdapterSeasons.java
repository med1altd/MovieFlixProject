package com.med1altd.movieflix;

import android.content.Context;
import android.content.Intent;
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

public class RecyclerViewAdapterSeasons extends RecyclerView.Adapter<RecyclerViewAdapterSeasons.MyViewHolder> {

    Context context;

    ArrayList<Season> Seasons;

    private static Integer PositionSeries;

    private static String JSON_URL;

    private static String SeriesName;

    private boolean isClicked = false;

    private static final int VIEW_TYPE_UNLOCKED = 0;
    private static final int VIEW_TYPE_LOCKED = 1;

    public RecyclerViewAdapterSeasons(Context ct, ArrayList<Season> seasons, String json_url, Integer positionSeries, String seriesName) {

        context = ct;

        Seasons = seasons;

        this.JSON_URL = json_url;

        this.PositionSeries = positionSeries;

        this.SeriesName = seriesName;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {

            case VIEW_TYPE_UNLOCKED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_seasons, parent, false);

                break;

            case VIEW_TYPE_LOCKED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_seasons_locked, parent, false);

                break;

            default:

                throw new IllegalArgumentException("Invalid view type: " + viewType);

        }

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int pos = position;

        new Help().showImage(Seasons.get(position).getImage(), holder.imageMain, holder.progressBar, context);

        holder.titleText.setText(Seasons.get(pos).getTitle());

        holder.titleText.setSelected(true);

        if (holder.year != null) {

            holder.year.setText(Seasons.get(pos).getYear());

        }

        int viewType = getItemViewType(position);

        switch (viewType) {

            case VIEW_TYPE_UNLOCKED:

                holder.lockedLayout.setVisibility(View.GONE);

                new Help().setupAnimationOnClick(holder.itemView, 0.9f, new OnViewClickListener() {
                    @Override
                    public void onClick() {

                        if (!isClicked) {

                            isClicked = true;

                            Intent intent = new Intent(context, EpisodesActivity.class);

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            intent.putExtra("JSON_URL", JSON_URL);

                            intent.putExtra("PositionSeries", PositionSeries);

                            intent.putExtra("PositionSeasons", pos);

                            intent.putExtra("Title", SeriesName + ": " + Seasons.get(pos).getTitle());

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

                break;

            // Add more cases for additional view types

        }

    }

    @Override
    public int getItemCount() {

        return Seasons.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, year;

        ImageView imageMain;

        RelativeLayout lockedLayout;

        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.TitleTxt);

            year = itemView.findViewById(R.id.textYear);

            imageMain = itemView.findViewById(R.id.imageMain);

            lockedLayout = itemView.findViewById(R.id.LockedLayout);

            progressBar = itemView.findViewById(R.id.progressBar);

        }

    }

    @Override
    public int getItemViewType(int position) {

        if (Seasons.get(position).getUnlocked()) {

            return VIEW_TYPE_UNLOCKED;

        } else {

            return VIEW_TYPE_LOCKED;

        }

    }

}