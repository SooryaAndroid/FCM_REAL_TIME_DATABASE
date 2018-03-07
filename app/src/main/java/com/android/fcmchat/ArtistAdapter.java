package com.android.fcmchat;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by soorya on 5/3/18.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private List<Artist> itemList;
    MainActivity activity;

    public ArtistAdapter(List<Artist> itemList, MainActivity activity) {
        this.itemList = itemList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ArtistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_artist_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistAdapter.ViewHolder holder, final int position) {
        Artist artist = itemList.get(position);
        holder.artistName.setText(artist.getArtistName());
        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                activity.viewItemClick(position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView artistName;
        private ConstraintLayout constraintLayout;

        public ViewHolder(final View itemView) {
            super(itemView);
            artistName = (TextView) itemView.findViewById(R.id.txt_artist_name);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraint);
        }
    }
}
