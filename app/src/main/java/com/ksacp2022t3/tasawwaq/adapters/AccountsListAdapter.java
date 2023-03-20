package com.ksacp2022t3.tasawwaq.adapters;

import android.accounts.Account;
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
import com.google.firebase.storage.StorageReference;
import com.ksacp2022t3.tasawwaq.AdminEditCategoryActivity;
import com.ksacp2022t3.tasawwaq.R;
import com.ksacp2022t3.tasawwaq.models.GlideApp;
import com.ksacp2022t3.tasawwaq.models.UserAccount;

import java.text.SimpleDateFormat;
import java.util.List;

public class AccountsListAdapter extends RecyclerView.Adapter<AccountItem> {
    List<UserAccount> userAccountList;
    Context context;
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    public AccountsListAdapter(List<UserAccount> userAccountList, Context context) {
        this.userAccountList = userAccountList;
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
    public AccountItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account,parent,false);

        return new AccountItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountItem holder, int position) {
        UserAccount account= userAccountList.get(position);
        holder.txt_name.setText(account.getName());
        holder.txt_status.setText(account.getStatus());

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.txt_date.setText(sdf.format(account.getCreated_at()));





        holder.btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+account.getPhone()))
                        ;
                context.startActivity(intent);
            }
        });

        if(account.getStatus().equals("Active")) {
            holder.txt_status.setTextColor(Color.GREEN);
            holder.btn_approve.setVisibility(View.GONE);
            holder.btn_reject.setVisibility(View.VISIBLE);
        }
        else {
            holder.txt_status.setTextColor(Color.RED);
            holder.btn_reject.setVisibility(View.GONE);
            holder.btn_approve.setVisibility(View.VISIBLE);
        }

        holder.btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                firestore.collection("accounts")
                        .document(account.getId())
                        .update("status","Active")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                account.setStatus("Active");
                                AccountsListAdapter.this.notifyDataSetChanged();
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
                firestore.collection("accounts")
                        .document(account.getId())
                        .update("status","Rejected")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                account.setStatus("Rejected");
                                AccountsListAdapter.this.notifyDataSetChanged();
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
        return userAccountList.size();
    }
}

class AccountItem extends RecyclerView.ViewHolder{
    TextView txt_name,txt_date,txt_status;
    AppCompatButton  btn_approve,btn_reject,btn_call;
    public AccountItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_date=itemView.findViewById(R.id.txt_date);
        btn_approve=itemView.findViewById(R.id.btn_approve);
        btn_reject=itemView.findViewById(R.id.btn_reject);
        txt_status=itemView.findViewById(R.id.txt_status);
        btn_call=itemView.findViewById(R.id.btn_call);
    }
}
