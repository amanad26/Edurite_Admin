package com.abmtech.eduriteadmin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.activities.MaterialDetailsActivtity;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ItemMaterialRecyclerBinding;
import com.abmtech.eduriteadmin.models.CourseDetailsModel;
import com.abmtech.eduriteadmin.models.MaterialListModel;
import com.abmtech.eduriteadmin.models.SignupModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseMaterialListAdapter extends RecyclerView.Adapter<CourseMaterialListAdapter.ViewHolder> {
    private final Context context;
    private final List<CourseDetailsModel.Data.Material> data;

    public CourseMaterialListAdapter(Context context, List<CourseDetailsModel.Data.Material> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMaterialRecyclerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data.get(position) != null) {
            CourseDetailsModel.Data.Material current = data.get(position);

            holder.binding.textHeading.setText(current.getMatName());
            holder.binding.textDesc.setText(current.getMatDescription());

            holder.binding.getRoot().setOnClickListener(view -> {
                if (holder.binding.textDesc.getMaxLines() != 2) {
                    holder.binding.textDesc.setMaxLines(2);
                    holder.binding.textViewMore.setText(R.string.view_more);
                } else {
                    holder.binding.textDesc.setMaxLines(1000);
                    holder.binding.textViewMore.setText(R.string.view_less);
                }
            });

            holder.binding.rating.setRating(Float.parseFloat(current.getAvgRating()));
            holder.binding.textRating.setText(current.getAvgRating());
            holder.binding.textTotalRating.setText("(" + current.getReviewCount() + ")");

            holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(context, MaterialDetailsActivtity.class)
                    .putExtra("mat_id", current.getMaterialId())));

        }


    }

    private void deleteCourse(String id) {

        ProgressDialog pd = new ProgressDialog(context);
        pd.show();
        RetrofitClient.getClient(context).deleteMaterial(id).enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(@NonNull Call<SignupModel> call, @NonNull Response<SignupModel> response) {
                pd.dismiss();
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().getResult().equalsIgnoreCase("true")) {

                        } else {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SignupModel> call, @NonNull Throwable t) {
                pd.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemMaterialRecyclerBinding binding;

        public ViewHolder(@NonNull ItemMaterialRecyclerBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
