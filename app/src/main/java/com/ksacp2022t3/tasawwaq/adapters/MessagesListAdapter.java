package com.ksacp2022t3.tasawwaq.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ksacp2022t3.tasawwaq.R;
import com.ksacp2022t3.tasawwaq.models.Message;


import java.util.List;

public class MessagesListAdapter extends RecyclerView.Adapter<MessageItem> {
    List<Message> messageList;
    Context context;
    FirebaseAuth firebaseAuth;

    public MessagesListAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MessageItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message,parent,false);
        return new MessageItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageItem holder, int position) {
        Message message= messageList.get(position);
        String uid= firebaseAuth.getUid();
        //if the current user is the sender
        if(message.getFrom().equals(uid))
        {

                holder.receiver_message.setVisibility(View.GONE);
                holder.sender_message.setText(message.getText());

        }
        else{

                holder.receiver_message.setText(message.getText());
                holder.sender_message.setVisibility(View.GONE);

        }




    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}

class MessageItem extends RecyclerView.ViewHolder{
    TextView sender_message,receiver_message;

    public MessageItem(@NonNull View itemView) {
        super(itemView);
        sender_message=itemView.findViewById(R.id.sender_message);
        receiver_message=itemView.findViewById(R.id.receiver_message);
    }
}
