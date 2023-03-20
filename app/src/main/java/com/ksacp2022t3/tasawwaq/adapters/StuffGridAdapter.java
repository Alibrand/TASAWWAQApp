package com.ksacp2022t3.tasawwaq.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

public class StuffGridAdapter extends RecyclerView.Adapter<StuffGridItem> {

    List<Stuff> stuffList;
    Context context;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;


    public StuffGridAdapter(List<Stuff> stuffList, Context context) {
        this.stuffList = stuffList;
        this.context = context;
        storage=FirebaseStorage.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public StuffGridItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_stuff,parent,false);
        return new StuffGridItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StuffGridItem holder, int position) {
            Stuff stuff=stuffList.get(position);

            holder.txt_description.setText(stuff.getTitle());
            holder.txt_price.setText("Price:"+(int)stuff.getPrice()+" S.R");

        StorageReference reference=storage.getReference();
        StorageReference image=reference.child("stuff_images/"+stuff.getImage());

        GlideApp.with(context)
                .load(image)
                .centerCrop()
                .into(holder.stuff_image);

        holder.stuff_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stuff.getStatus().equals("Available")) {
                    if (firebaseAuth.getUid().equals(stuff.getOwner_id())) {

                        Intent intent = new Intent(context, EditActivity.class);
                        intent.putExtra("stuff_id", stuff.getId());
                        intent.putExtra("main_category", stuff.getMain_category());
                        context.startActivity(intent);
                        return;
                    }
                    else{
                        Intent intent = new Intent(context, ViewStuffActivity. class);
                        intent.putExtra("stuff_id",stuff.getId());
                        context.startActivity(intent);
                    }
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
class StuffGridItem extends RecyclerView.ViewHolder
{
        ImageView stuff_image;
        TextView txt_price,txt_description;
        CardView stuff_card;
    public StuffGridItem(@NonNull View itemView) {
        super(itemView);
        stuff_card=itemView.findViewById(R.id.stuff_card);
        txt_price=itemView.findViewById(R.id.txt_price);
        txt_description=itemView.findViewById(R.id.txt_description);
        stuff_image=itemView.findViewById(R.id.stuff_image);
    }
}
