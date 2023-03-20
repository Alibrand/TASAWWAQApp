package com.ksacp2022t3.tasawwaq.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.ksacp2022t3.tasawwaq.models.Category;
import com.ksacp2022t3.tasawwaq.models.GlideApp;

import java.util.List;

public class AdminCategoryListAdapter extends RecyclerView.Adapter<AdminCategoryItem> {
    List<Category> categoryList;
    Context context;
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    public AdminCategoryListAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
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
    public AdminCategoryItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_category,parent,false);

        return new AdminCategoryItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCategoryItem holder, int position) {
        Category category=categoryList.get(position);
        holder.txt_name.setText(category.getName());
        holder.txt_count.setText(String.valueOf(category.getItems_count()) );

        holder.txt_cat.setText(category.getMain_category());

        StorageReference ref=storage.getReference();
        StorageReference image_ref=ref.child("categories_images/"+category.getImage_url());


        GlideApp.with(context)
                        .load(image_ref)
                .circleCrop()
                .into(holder.category_image);

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdminEditCategoryActivity. class);
                intent.putExtra("cat_id",category.getId());
                context.startActivity(intent);
            }
        });


        if(category.getItems_count()>0)
            holder.btn_delete.setVisibility(View.GONE);
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                firestore.collection("categories")
                        .document(category.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                categoryList.remove(category);
                                AdminCategoryListAdapter.this.notifyDataSetChanged();
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
        return categoryList.size();
    }
}

class AdminCategoryItem extends RecyclerView.ViewHolder{
    TextView txt_name,txt_count,txt_cat;
    AppCompatButton btn_edit,btn_delete;
    ImageView category_image;
    public AdminCategoryItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_count=itemView.findViewById(R.id.txt_count);
        btn_edit=itemView.findViewById(R.id.btn_edit);
        btn_delete=itemView.findViewById(R.id.btn_delete);
        category_image=itemView.findViewById(R.id.category_image);
        txt_cat=itemView.findViewById(R.id.txt_cat);
    }
}
