package com.abmtech.eduriteadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.apis.BaseUrls;
import com.abmtech.eduriteadmin.databinding.ItemNotificationRecyclerBinding;
import com.abmtech.eduriteadmin.models.NotificationModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    Context context ;
    List<NotificationModel.Datum> models;

    public NotificationListAdapter(Context context, List<NotificationModel.Datum> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemNotificationRecyclerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.getRoot().setOnClickListener(view -> {
            if (holder.binding.textDesc.getMaxLines() != 2) {
                holder.binding.textDesc.setMaxLines(2);
                holder.binding.textViewMore.setText(R.string.view_more);
            } else {
                holder.binding.textDesc.setMaxLines(1000);
                holder.binding.textViewMore.setText(R.string.view_less);
            }
        });


        NotificationModel.Datum data = models.get(position);
        holder.binding.textDesc.setText(data.getDescription());
        holder.binding.textHeading.setText(data.getTitle());
        holder.binding.textDate.setText(data.getTime());


        if (!data.getImage().equalsIgnoreCase("")) {
            Picasso.get().load(BaseUrls.IMAGE_URL + data.getImage())
                    .into(holder.binding.image);
        }



    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemNotificationRecyclerBinding binding;

        public ViewHolder(@NonNull ItemNotificationRecyclerBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}