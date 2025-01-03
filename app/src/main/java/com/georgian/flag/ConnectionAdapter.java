package com.georgian.flag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ViewHolder> {

    private List<MyDataModel> dataList;
    private Context context;

    public ConnectionAdapter(List<MyDataModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    public void setData(List<MyDataModel> newData) {
        this.dataList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyDataModel data = dataList.get(position);

        // Bind data to views
        holder.nameTextView.setText(data.getName());
        holder.studentIdTextView.setText("Student ID: " + data.getId());

        // Load image using Picasso (replace with your image loading library)
        //Picasso.get().load(data.getImageUrl()).into(holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;
        TextView studentIdTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profile_image);
            nameTextView = itemView.findViewById(R.id.name_text);
            studentIdTextView = itemView.findViewById(R.id.student_id_text);
        }
    }
}
