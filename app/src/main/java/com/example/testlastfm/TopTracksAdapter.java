package com.example.testlastfm;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.testlastfm.MainActivity.TAG;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TopTracksAdapter extends RecyclerView.Adapter<TopTracksAdapter.TrackHolder> {
    private final Events events;
    private final List<HolderBody> bodyList;
    private final List<View> listView;

    TopTracksAdapter(Events events, List<HolderBody> bodyList) {
        this.events = events;
        this.bodyList = bodyList;
        listView = new ArrayList<>();
    }

    public static class TrackHolder extends RecyclerView.ViewHolder {
        ConstraintLayout item;
        ImageView ivImgAlbum;
        TextView tvNameTrack;

        public TrackHolder(View view) {
            super(view);

            item = view.findViewById(R.id.item);
            ivImgAlbum = view.findViewById(R.id.ivImgAlbum);
            tvNameTrack = view.findViewById(R.id.tvNameTrack);
        }
    }

    public static class HolderBody {
        private String urlAlbum;
        private String nameTrack;

        public String getImgAlbum() {
            return urlAlbum;
        }

        public void setImgAlbum(String imgAlbum) {
            this.urlAlbum = imgAlbum;
        }

        public String getNameTrack() {
            return nameTrack;
        }

        public void setNameTrack(String nameTrack) {
            this.nameTrack = nameTrack;
        }
    }

    @NonNull
    @Override
    public TrackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.add_track, parent, false);

        listView.add(view);

        return new TrackHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackHolder holder, int position) {
        //Получаю картинку
        new DownloadImageTask(holder.ivImgAlbum).execute(bodyList.get(position).urlAlbum);
        //Получаю название песни
        holder.tvNameTrack.setText(bodyList.get(position).nameTrack);

        Log.d(TAG, "position: " + position + ", url: " + bodyList.get(position).urlAlbum);
        Log.d(TAG, "position: " + position + ", title: " + bodyList.get(position).nameTrack);

        //устанавливаю слушатель по клику
        holder.item.setOnClickListener(view -> {
            for (int i = 0; i < bodyList.size(); i++)
                if (view == listView.get(i)) {
                    events.onItemClick(i);
                    break;
                }
        });
    }

    @Override
    public int getItemCount() {
        return bodyList.size();
    }

    public interface Events {

        void onItemClick(int position);
    }
}
