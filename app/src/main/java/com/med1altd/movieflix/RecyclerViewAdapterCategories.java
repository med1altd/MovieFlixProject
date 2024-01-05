package com.med1altd.movieflix;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;

public class RecyclerViewAdapterCategories extends RecyclerView.Adapter<RecyclerViewAdapterCategories.MyViewHolder> {

    Context context;

    ArrayList<Category>
            Categories = new ArrayList<>();

    private static String Type;

    private boolean isClicked = false;

    private static final int VIEW_TYPE_UNLOCKED = 0;
    private static final int VIEW_TYPE_LOCKED = 1;

    public RecyclerViewAdapterCategories(Context ct, ArrayList<Category> categories, String Type) {

        context = ct;

        Categories = categories;

        this.Type = Type;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {

            case VIEW_TYPE_UNLOCKED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_categories, parent, false);

                break;

            case VIEW_TYPE_LOCKED:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_categories_locked, parent, false);

                break;

            default:

                throw new IllegalArgumentException("Invalid view type: " + viewType);

        }

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int pos = position;

        new Help().showImage(Categories.get(position).getImage(), holder.imageMain, holder.progressBar, context);

        holder.title.setText(Categories.get(pos).getTitle());

        int viewType = getItemViewType(position);

        switch (viewType) {

            case VIEW_TYPE_UNLOCKED:

                // Bind data for unlocked item

                // ((UnlockedViewHolder) holder).bindData(data.get(position));

                new Help().setupAnimationOnClick(holder.itemView, 0.9f, new OnViewClickListener() {
                    @Override
                    public void onClick() {

                        if (!isClicked) {

                            isClicked = true;

                            //Intent intent = new Intent(context, LiveStreamingActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //intent.putExtra("Title", TitleList.get(pos));
                            //intent.putExtra("Video", VideoList.get(pos));
                            //context.startActivity(intent);

                            if ("Series".equals(Type)) {

                                Intent intent = new Intent(context, SeriesActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("Category", Categories.get(pos).getCategory());
                                intent.putExtra("JSON", Categories.get(pos).getJSON_URL());
                                intent.putExtra("Type", Type);
                                context.startActivity(intent);

                            } else if ("Movies".equals(Type)) {

                                Intent intent = new Intent(context, MoviesActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("Category", Categories.get(pos).getCategory());
                                intent.putExtra("JSON", Categories.get(pos).getJSON_URL());
                                intent.putExtra("Type", Type);
                                context.startActivity(intent);

                            } else {

                                if ( pos == 0 ) {

                                    Intent intent = new Intent(context, MoviesActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("Category", 3);
                                    intent.putExtra("JSON", Categories.get(pos).getJSON_URL());
                                    intent.putExtra("Type", Type);
                                    context.startActivity(intent);

                                } else if ( pos == 1 ) {

                                    Intent intent = new Intent(context, SeriesActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("Category", 4);
                                    intent.putExtra("JSON", Categories.get(pos).getJSON_URL());
                                    intent.putExtra("Type", Type);
                                    context.startActivity(intent);

                                } else {

                                    Intent intent = new Intent(context, YouTubeChannelsActivity.class);
                                    intent.putExtra("Category", Categories.get(pos).getCategory());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("JSON", Categories.get(pos).getJSON_URL());
                                    context.startActivity(intent);

                                }

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

                break;

            case VIEW_TYPE_LOCKED:

                break;

            // Add more cases for additional view types

        }

    }

    @Override
    public int getItemCount() {

        return Categories.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        ShapeableImageView imageMain;

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

        if (Categories.get(position).getUnlocked()) {

            return VIEW_TYPE_UNLOCKED;

        } else {

            return VIEW_TYPE_LOCKED;

        }

    }

}