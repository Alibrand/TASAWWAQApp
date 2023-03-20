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
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022t3.tasawwaq.AdminEditCategoryActivity;
import com.ksacp2022t3.tasawwaq.CategoryActivity;
import com.ksacp2022t3.tasawwaq.R;
import com.ksacp2022t3.tasawwaq.models.Category;
import com.ksacp2022t3.tasawwaq.models.GlideApp;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryItem> {
    List<Category> categoryList;
    Context context;

    FirebaseStorage storage;

    public CategoryListAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;

        storage=FirebaseStorage.getInstance();

    }

    @NonNull
    @Override
    public CategoryItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);

        return new  CategoryItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryItem holder, int position) {
        Category category=categoryList.get(position);
        holder.txt_name.setText(category.getName());

        StorageReference ref=storage.getReference();
        StorageReference image_ref=ref.child("categories_images/"+category.getImage_url());


        GlideApp.with(context)
                        .load(image_ref)
                .circleCrop()
                .into(holder.category_image);


        holder.item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CategoryActivity. class);
                intent.putExtra("category_id",category.getId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}

class  CategoryItem extends RecyclerView.ViewHolder{
    TextView txt_name;
    LinearLayoutCompat item_card;
    ImageView category_image;
    public  CategoryItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        item_card=itemView.findViewById(R.id.item_card);
          category_image=itemView.findViewById(R.id.category_image);

    }
}
