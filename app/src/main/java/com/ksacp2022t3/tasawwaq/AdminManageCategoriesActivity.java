package com.ksacp2022t3.tasawwaq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.tasawwaq.adapters.AdminCategoryListAdapter;
import com.ksacp2022t3.tasawwaq.adapters.CategoryListAdapter;
import com.ksacp2022t3.tasawwaq.models.Category;

import java.util.ArrayList;
import java.util.List;

public class AdminManageCategoriesActivity extends AppCompatActivity {
    AppCompatButton btn_add_category;
    RecyclerView recycler_categories;
    ImageView btn_back;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_categories);
        btn_add_category = findViewById(R.id.btn_add_category);
        recycler_categories = findViewById(R.id.recycler_categories);
        btn_back = findViewById(R.id.btn_back);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    }
        );

        load_categories();

        btn_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminManageCategoriesActivity.this,AdminAddCategoryActivity. class);
                startActivity(intent);
            }
        });

    }

    private void load_categories() {
        progressDialog.show();
        firestore.collection("categories")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<Category> categoryList=new ArrayList<>();
                        for(DocumentSnapshot doc:queryDocumentSnapshots.getDocuments())
                        {
                            Category category=doc.toObject(Category.class);
                            categoryList.add(category);
                        }
                        AdminCategoryListAdapter adapter=new AdminCategoryListAdapter(categoryList, AdminManageCategoriesActivity.this);
                        recycler_categories.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         Toast.makeText(AdminManageCategoriesActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_categories();
    }
}