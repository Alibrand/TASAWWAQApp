package com.ksacp2022t3.tasawwaq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022t3.tasawwaq.adapters.StuffGridAdapter;
import com.ksacp2022t3.tasawwaq.adapters.StuffListAdapter;
import com.ksacp2022t3.tasawwaq.models.Category;
import com.ksacp2022t3.tasawwaq.models.GlideApp;
import com.ksacp2022t3.tasawwaq.models.Stuff;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    TextView txt_title,txt_description;
    ImageView category_image,btn_back;
    RecyclerView recycler_products;

    FirebaseFirestore firestore;

    FirebaseStorage storage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        txt_title = findViewById(R.id.txt_title);
        txt_description = findViewById(R.id.txt_description);
        category_image = findViewById(R.id.category_image);
        recycler_products = findViewById(R.id.recycler_products);
        btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        String category_id=getIntent().getStringExtra("category_id");

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        firestore.collection("categories")
                .document(category_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Category category=documentSnapshot.toObject(Category.class);

                        txt_title.setText(category.getName());
                        txt_description.setText(category.getDescription());

                        StorageReference referencc=storage.getReference();
                        StorageReference image=referencc.child("categories_images/"+category.getImage_url());
                        GlideApp.with(CategoryActivity.this)
                                .load(image)
                                .fitCenter()
                                .into(category_image);

                        firestore.collection("stuffs")
                                .whereEqualTo("category_id",category_id)
                                .whereEqualTo("status","Available")
                                .orderBy("created_at", Query.Direction.DESCENDING)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        progressDialog.dismiss();
                                        List<Stuff> stuffList=queryDocumentSnapshots.toObjects(Stuff.class);
                                        StuffGridAdapter adapter=new StuffGridAdapter(stuffList,CategoryActivity.this);
                                        recycler_products.setAdapter(adapter);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                         Toast.makeText(CategoryActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                                         finish();
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         Toast.makeText(CategoryActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                         finish();
                    }
                });

    }

}