package com.ksacp2022t3.tasawwaq.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022t3.tasawwaq.EditActivity;
import com.ksacp2022t3.tasawwaq.R;
import com.ksacp2022t3.tasawwaq.ViewStuffActivity;
import com.ksacp2022t3.tasawwaq.models.GlideApp;
import com.ksacp2022t3.tasawwaq.models.Stuff;

import java.util.List;

public class MostRatedListAdapter extends  RecyclerView.Adapter<MostRatedItem> {
    List<Stuff> stuffList;
    Context context;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;

    public MostRatedListAdapter(List<Stuff> stuffList, Context context) {
        firebaseAuth=FirebaseAuth.getInstance();
        this.stuffList = stuffList;
        this.context = context;
        storage=FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public MostRatedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_rated,parent,false);
        return new MostRatedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MostRatedItem holder, int position) {
        Stuff stuff=stuffList.get(position);

        holder.txt_name.setText(stuff.getTitle());

        StorageReference ref=storage.getReference();
        StorageReference image=ref.child("stuff_images/"+stuff.getImage());
        GlideApp.with(context)
                .load(image)
                .centerCrop()
                .into(holder.image);

        holder.txt_owner_name.setText(stuff.getOwner_name());

        holder.rating.setRating((float) stuff.getRate());

        holder.txt_reviews_count.setText("("+stuff.getReviews_count()+")");

        holder.item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getUid().equals(stuff.getOwner_id()))
                {
                    Intent intent = new Intent(context, EditActivity. class);
                    intent.putExtra("stuff_id",stuff.getId());
                    intent.putExtra("main_category",stuff.getMain_category());
                    context.startActivity(intent);
                }
                else{
                    Intent intent = new Intent(context, ViewStuffActivity. class);
                    intent.putExtra("stuff_id",stuff.getId());
                    context.startActivity(intent);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return stuffList.size();
    }
}

class MostRatedItem extends RecyclerView.ViewHolder
{
TextView txt_name,txt_owner_name,txt_reviews_count;
ImageView image;
RatingBar rating;
LinearLayoutCompat item_card;
    public MostRatedItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        image=itemView.findViewById(R.id.image);
        txt_owner_name=itemView.findViewById(R.id.txt_owner_name);
        rating=itemView.findViewById(R.id.rating);
        item_card=itemView.findViewById(R.id.item_card);
        txt_reviews_count=itemView.findViewById(R.id.txt_reviews_count);
    }
}
