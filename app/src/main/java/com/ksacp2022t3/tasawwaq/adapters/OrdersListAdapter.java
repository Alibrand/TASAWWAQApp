package com.ksacp2022t3.tasawwaq.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.ksacp2022t3.tasawwaq.ChatActivity;
import com.ksacp2022t3.tasawwaq.R;
import com.ksacp2022t3.tasawwaq.ViewStuffActivity;
import com.ksacp2022t3.tasawwaq.models.Order;
import com.ksacp2022t3.tasawwaq.models.UserAccount;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrdersListAdapter extends RecyclerView.Adapter<OrderItem> {
    List<Order> orderList;
    Context context;
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    public OrdersListAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
        progressDialog=new ProgressDialog(context);
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();

    }

    @NonNull
    @Override
    public OrderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order,parent,false);

        return new OrderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItem holder, int position) {
        Order order= orderList.get(position);
        holder.txt_name.setText(order.getRequester_name());
        holder.txt_stuff.setText(order.getStuff_name());

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.txt_date.setText(sdf.format(order.getCreated_at()));



        holder.btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity. class);
                intent.putExtra("receiver_id",order.getRequester_uid());
                intent.putExtra("receiver_name",order.getRequester_name() );
                context.startActivity(intent);
            }
        });



        if(order.getStatus().equals("Accepted")) {
            holder.btn_accept.setVisibility(View.GONE);
            holder.btn_reject.setVisibility(View.VISIBLE);
        }
        else if(order.getStatus().equals("Rejected")) {
            holder.btn_reject.setVisibility(View.GONE);
            holder.btn_accept.setVisibility(View.VISIBLE);
        }
        else  if(order.getStatus().equals("Paid"))
        {
            holder.btn_reject.setVisibility(View.GONE);
            holder.btn_accept.setVisibility(View.GONE);
        }

        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                firestore.collection("orders")
                        .document(order.getId())
                        .update("status","Accepted")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                order.setStatus("Accepted");
                                OrdersListAdapter.this.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                 Toast.makeText(context,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        holder.btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                firestore.collection("orders")
                        .document(order.getId())
                        .update("status","Rejected")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                order.setStatus("Rejected");
                                OrdersListAdapter.this.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}

class OrderItem extends RecyclerView.ViewHolder{
    TextView txt_name,txt_date,txt_stuff;
    AppCompatButton btn_chat,btn_accept,btn_reject  ;
    public OrderItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_date=itemView.findViewById(R.id.txt_date);
        btn_chat=itemView.findViewById(R.id.btn_chat);
        btn_accept=itemView.findViewById(R.id.btn_accept);
        btn_reject=itemView.findViewById(R.id.btn_reject);
        txt_stuff=itemView.findViewById(R.id.txt_stuff);

    }
}
