package com.ksacp2022t3.tasawwaq.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.ksacp2022t3.tasawwaq.ChatActivity;
import com.ksacp2022t3.tasawwaq.R;
import com.ksacp2022t3.tasawwaq.ViewStuffActivity;
import com.ksacp2022t3.tasawwaq.models.Order;

import java.text.SimpleDateFormat;
import java.util.List;

public class SoldStuffListAdapter extends RecyclerView.Adapter<SoldStuffItem> {
    List<Order> orderList;
    Context context;


    public SoldStuffListAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;


    }

    @NonNull
    @Override
    public SoldStuffItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sold_stuff,parent,false);

        return new SoldStuffItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoldStuffItem holder, int position) {
        Order order= orderList.get(position);
        holder.txt_name.setText(order.getRequester_name());
        holder.txt_stuff_name.setText(order.getStuff_name());
        holder.txt_price.setText((int)order.getPrice()+" S.R");

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.txt_date.setText(sdf.format(order.getCreated_at()));



        holder.item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewStuffActivity. class);
                intent.putExtra("stuff_id",order.getStuff_id());
                context.startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}

class SoldStuffItem extends RecyclerView.ViewHolder{
    TextView txt_name,txt_date,txt_stuff_name,txt_price;
    LinearLayoutCompat item_card;
    public SoldStuffItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_date=itemView.findViewById(R.id.txt_date);
        txt_stuff_name=itemView.findViewById(R.id.txt_stuff_name);
        txt_price=itemView.findViewById(R.id.txt_price);
        item_card=itemView.findViewById(R.id.item_card);


    }
}
