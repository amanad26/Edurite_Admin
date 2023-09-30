package com.abmtech.eduriteadmin.adapters;

import static com.abmtech.eduriteadmin.utils.Util.calculateDateDifference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.eduriteadmin.databinding.ItemReviewRecyclerBinding;
import com.abmtech.eduriteadmin.models.CourseDetailsModel;
import com.abmtech.eduriteadmin.models.MaterialDetailsModel;

import java.util.List;

public class MaterialReviewListAdapter extends RecyclerView.Adapter<MaterialReviewListAdapter.ViewHolder> {
    private final Context context;
    private final List<MaterialDetailsModel.Data.Review> data;

    public MaterialReviewListAdapter(Context context, List<MaterialDetailsModel.Data.Review> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemReviewRecyclerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data.get(position) != null) {
            MaterialDetailsModel.Data.Review current = data.get(position);

            holder.binding.textHeading.setText(current.getUsername());
            holder.binding.textDesc.setText(current.getReview());
            holder.binding.textRating.setText(current.getRating());
            holder.binding.rating.setRating(Float.parseFloat(current.getRating()));
            holder.binding.textDate.setText(calculateDateDifference(current.getDate()));

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemReviewRecyclerBinding binding;

        public ViewHolder(@NonNull ItemReviewRecyclerBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
