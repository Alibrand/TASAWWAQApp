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

import org.checkerframework.framework.qual.DefaultQualifier;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyOrdersListAdapter extends RecyclerView.Adapter<MyOrderItem> {
    List<Order> orderList;
    Context context;


    public MyOrdersListAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;


    }

    @NonNull
    @Override
    public MyOrderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order,parent,false);

        return new MyOrderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderItem holder, int position) {
        Order order= orderList.get(position);
        holder.txt_status.setText(order.getStatus());
        holder.txt_stuff.setText(order.getStuff_name());

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

class MyOrderItem extends RecyclerView.ViewHolder{
    TextView txt_status,txt_date,txt_stuff;
    LinearLayoutCompat item_card;

    public MyOrderItem(@NonNull View itemView) {
        super(itemView);
        txt_status=itemView.findViewById(R.id.txt_status);
        txt_date=itemView.findViewById(R.id.txt_date);
        item_card=itemView.findViewById(R.id.item_card);

        txt_stuff=itemView.findViewById(R.id.txt_stuff);

    }
}
