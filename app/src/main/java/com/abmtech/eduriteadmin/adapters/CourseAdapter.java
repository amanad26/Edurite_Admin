package com.abmtech.eduriteadmin.adapters;

import static com.abmtech.eduriteadmin.utils.Util.decodeImage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.activities.CourseDetailActivity;
import com.abmtech.eduriteadmin.apis.BaseUrls;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ItemCourseRecyclerBinding;
import com.abmtech.eduriteadmin.models.CourseModel;
import com.abmtech.eduriteadmin.models.SignupModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    Context context;
    List<CourseModel.Datum> models;

    public CourseAdapter(Context context, List<CourseModel.Datum> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_course_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (models.get(position) != null) {
            CourseModel.Datum current = models.get(position);

            holder.binding.textHeading.setText(current.getTitle());
            holder.binding.textDesc.setText(current.getDescription());
            holder.binding.textLevel.setText(current.getLevel());

//            holder.binding.imageThumbnail.setImageBitmap(decodeImage(context, current.getImage()));

            if (!current.getImage().equalsIgnoreCase("")) {
                Picasso.get().load(BaseUrls.IMAGE_URL + current.getImage())
                        .into(holder.binding.imageThumbnail);
            }

            holder.binding.rating.setRating(Float.parseFloat(current.getAvgRating()));
            holder.binding.textRating.setText(current.getAvgRating());
            holder.binding.textTotalRating.setText("(" + current.getReviewCount() + ")");

            holder.itemView.setOnLongClickListener(view -> {

                new AlertDialog.Builder(context)
                        .setTitle("Delete Course")
                        .setMessage("Are you sure you want to delete this course")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            String id = current.getCourseId();
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(0, models.size() - 1);
                            models.remove(position);
                            deleteCourse(id);
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();

                return true;
            });


            holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(context, CourseDetailActivity.class)
                    .putExtra("course_id", current.getCourseId())));
        }
    }

    /*private void showDialog(String id, int pos) {

        new KAlertDialog(context, KAlertDialog.SUCCESS_TYPE)
                .setTitleText("Warning!")
                .setContentText("Do you want to delete this course?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.dismissWithAnimation();
                        deleteCourse(id);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(0, models.size() - 1);
                        models.remove(pos);
                    }
                })
                .setCancelText("No")
                .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.dismissWithAnimation();
                    }
                })
                .show();

    }
*/
    private void deleteCourse(String id) {

        ProgressDialog pd = new ProgressDialog(context);
        pd.show();
        RetrofitClient.getClient(context).deleteCourse(id).enqueue(new Callback<SignupModel>() {
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
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCourseRecyclerBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCourseRecyclerBinding.bind(itemView);
        }
    }
}
