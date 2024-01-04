package com.med1altd.movieflix;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerViewAdapterMovies extends RecyclerView.Adapter<RecyclerViewAdapterMovies.MyViewHolder> {

    Context context;

    ArrayList<Movie>
            Movies = new ArrayList<>();

    private String Type;

    private boolean isClicked = false;

    private boolean isWorking,
            MoreContentOption = false;

    private static final int VIEW_TYPE_UNLOCKED = 0;
    private static final int VIEW_TYPE_LOCKED = 1;
    private static final int VIEW_TYPE_LAST = 2;

    public RecyclerViewAdapterMovies(Context ct,
                                     ArrayList<Movie> movies,
                                     String Type) {

        context = ct;

        Movies = movies;

        this.Type = Type;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        isWorking = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isWorking", false);

        MoreContentOption = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("MoreContentOption", false);

        switch (viewType) {

            case VIEW_TYPE_LOCKED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_movies_locked, parent, false);

                break;

            case VIEW_TYPE_UNLOCKED:
            case VIEW_TYPE_LAST:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_movies, parent, false);

                break;

            default:

                    throw new IllegalArgumentException("Invalid view type: " + viewType);

        }

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int posMovies = position;

        String
                ImageUrl;

        if (Movies.get(position) == null) {

            ImageUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("MoreUrl", "https://i.ibb.co/DtDR2cp/More.jpg");

        } else {

            ImageUrl = Movies.get(position).getImage();

        }

        new Help().showImage(ImageUrl, holder.imageMain, holder.progressBar, context);

        int viewType = getItemViewType(position);

        switch (viewType) {

            case VIEW_TYPE_UNLOCKED:

                // Bind data for unlocked item

                // ((UnlockedViewHolder) holder).bindData(data.get(position));

                holder.titleText.setText(Movies.get(posMovies).getTitle());

                holder.titleText.setSelected(true);

                holder.lockedLayout.setVisibility(View.GONE);

                new Help().setupAnimationOnClick(holder.itemView, 0.9f, new OnViewClickListener() {
                    @Override
                    public void onClick() {

                        if ((!isClicked) && isWorking) {

                            isClicked = true;

                            Intent intent = new Intent(context, MovieInfoActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("JSON_URL", Movies.get(posMovies).getJSON());
                            intent.putExtra("Position", Movies.get(posMovies).getPosition());
                            //intent.putExtra("Title", TitleList.get(posMovies));
                            //intent.putExtra("TitleList", TitleListIndex);
                            intent.putExtra("Type", Type);
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

                holder.titleText.setText(Movies.get(posMovies).getTitle());

                break;

            case VIEW_TYPE_LAST:

                if (MoreContentOption) {

                    new Help().setupAnimationOnClick(holder.itemView, 0.9f, new OnViewClickListener() {
                        @Override
                        public void onClick() {

                            if (!isClicked) {

                                isClicked = true;

                                if (context instanceof MoviesActivity) {

                                    MoviesActivity moviesActivity = (MoviesActivity) context;

                                    new Help().showCustomDialogBox(moviesActivity, "Hello!");

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

        return Movies.size();

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

        if (Movies.get(position) != null) {

            if (Movies.get(position).getUnlocked()) {

                return VIEW_TYPE_UNLOCKED;

            } else {

                return VIEW_TYPE_LOCKED;

            }

        } else {

            return VIEW_TYPE_LAST;

        }

    }

}