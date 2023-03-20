package com.ksacp2022t3.tasawwaq.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;
import com.ksacp2022t3.tasawwaq.EditActivity;
import com.ksacp2022t3.tasawwaq.R;
import com.ksacp2022t3.tasawwaq.ViewStuffActivity;
import com.ksacp2022t3.tasawwaq.models.GlideApp;
import com.ksacp2022t3.tasawwaq.models.Review;
import com.ksacp2022t3.tasawwaq.models.Stuff;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewItem> {

    List<Review> reviewList;
    Context context;



    public ReviewListAdapter(List<Review> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;

    }

    @NonNull
    @Override
    public ReviewItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review,parent,false);
        return new ReviewItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewItem holder, int position) {
            Review review=reviewList.get(position);

            holder.txt_name.setText(review.getName());
            if(review.getComment().isEmpty())
            {
                holder.txt_comment.setVisibility(View.GONE);
            }
            holder.txt_comment.setText(review.getComment());

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        holder.txt_date.setText(simpleDateFormat.format(review.getCreated_at()));
        holder.rating_bar.setRating(review.getRating());

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}
class ReviewItem extends RecyclerView.ViewHolder
{

        TextView txt_name,txt_comment,txt_date;
        RatingBar rating_bar;

    public ReviewItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_comment=itemView.findViewById(R.id.txt_comment);
        txt_date=itemView.findViewById(R.id.txt_date);
        rating_bar=itemView.findViewById(R.id.rating_bar);
    }
}
