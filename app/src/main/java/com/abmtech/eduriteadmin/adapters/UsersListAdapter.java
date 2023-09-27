package com.abmtech.eduriteadmin.adapters;

import static com.abmtech.eduriteadmin.utils.Util.decodeImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.databinding.ItemCourseRecyclerBinding;
import com.abmtech.eduriteadmin.databinding.ItemUsersRecyclerBinding;
import com.abmtech.eduriteadmin.models.CourseModel;
import com.abmtech.eduriteadmin.models.UsersModel;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    Context context;
    List<UsersModel.Datum> models;

    public UsersListAdapter(Context context, List<UsersModel.Datum> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_users_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (models.get(position) != null) {
            UsersModel.Datum current = models.get(position);

            holder.binding.textHeading.setText(current.getName());
            holder.binding.textDesc.setText(current.getType());

            holder.binding.userImage.setImageBitmap(decodeImage(context, current.getProfileImg()));

        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUsersRecyclerBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemUsersRecyclerBinding.bind(itemView);
        }
    }
}
