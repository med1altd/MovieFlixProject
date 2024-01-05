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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerViewAdapterEpisodes extends RecyclerView.Adapter<RecyclerViewAdapterEpisodes.MyViewHolder> {

    Context context;

    ArrayList<Episode> Episodes = new ArrayList<>();

    Integer PositionSeries, PositionSeasons;

    private static String JSON_URL;

    private boolean isClicked = false;

    private boolean isWorking;

    private static final int VIEW_TYPE_UNLOCKED = 0;
    private static final int VIEW_TYPE_LOCKED = 1;

    public RecyclerViewAdapterEpisodes(Context ct, ArrayList<Episode> episodes, String JSON_URL, Integer PositionSeries, Integer PositionSeasons) {
        context = ct;
        Episodes = episodes;
        this.JSON_URL = JSON_URL;
        this.PositionSeries = PositionSeries;
        this.PositionSeasons = PositionSeasons;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        isWorking = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isWorking", false);

        switch (viewType) {

            case VIEW_TYPE_UNLOCKED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_episodes, parent, false);

                break;

            case VIEW_TYPE_LOCKED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_episodes_locked, parent, false);

                break;

            default:

                throw new IllegalArgumentException("Invalid view type: " + viewType);

        }

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int pos = position;

        new Help().showImage(Episodes.get(position).getImage(), holder.imageMain, holder.progressBar, context);

        holder.titleText.setText(Episodes.get(pos).getTitle());

        holder.titleText.setSelected(true);

        int viewType = getItemViewType(position);

        switch (viewType) {

            case VIEW_TYPE_UNLOCKED:

                // Bind data for unlocked item

                // ((UnlockedViewHolder) holder).bindData(data.get(position));

                holder.lockedLayout.setVisibility(View.GONE);

                new Help().setupAnimationOnClick(holder.itemView, 0.9f, new OnViewClickListener() {
                    @Override
                    public void onClick() {

                        holder.lockedLayout.setVisibility(View.GONE);

                        if ((!isClicked) && isWorking) {

                            isClicked = true;

                            Intent intent = new Intent(context, SeriesInfoActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("JSON_URL", JSON_URL);
                            intent.putExtra("posSeries", PositionSeries);
                            intent.putExtra("posSeasons", PositionSeasons);
                            intent.putExtra("posEpisodes", pos);
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

            // Add more cases for additional view types

        }

    }

    @Override
    public int getItemCount() {

        return Episodes.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;

        ImageView imageMain;

        RelativeLayout lockedLayout;

        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.TitleTxt);

            imageMain = itemView.findViewById(R.id.imageMain);

            lockedLayout = itemView.findViewById(R.id.LockedLayout);

            progressBar = itemView.findViewById(R.id.progressBar);

        }

    }

    @Override
    public int getItemViewType(int position) {

        if (Episodes.get(position).getUnlocked()) {

            return VIEW_TYPE_UNLOCKED;

        } else {

            return VIEW_TYPE_LOCKED;

        }

    }

}