package com.georgian.flag;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<MyDataModel> items;

    public MyAdapter(List<MyDataModel> items) {
        this.items = items;
    }

    public MyDataModel getItem(int position) {
        if (position >= 0 && position < items.size()) {
            return items.get(position);
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyDataModel item = items.get(position);
        holder.name.setText(item.getName());
        holder.birthdate.setText(item.getBirthdate());
        holder.campus.setText(item.getCampus());
        holder.bio.setText(item.getBio());
        Glide.with(holder.itemView.getContext())
                .load(R.drawable.myprofilepic)
                .into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView birthdate;
        TextView campus;
        TextView bio;
        ImageView profileImage;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            birthdate = itemView.findViewById(R.id.birthdate);
            campus = itemView.findViewById(R.id.campus);
            bio = itemView.findViewById(R.id.bio);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }


}
