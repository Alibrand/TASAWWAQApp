package com.ksacp2022t3.tasawwaq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.tasawwaq.adapters.StuffGridAdapter;
import com.ksacp2022t3.tasawwaq.models.Stuff;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class SearchActivity extends AppCompatActivity {
    ImageView btn_back,btn_search;
    RecyclerView recycler_products;
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    EditText txt_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        btn_back = findViewById(R.id.btn_back);
        btn_search = findViewById(R.id.btn_search);
        recycler_products = findViewById(R.id.recycler_products);
        txt_search = findViewById(R.id.txt_search);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Searching");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        firestore=FirebaseFirestore.getInstance();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_search =txt_search.getText().toString().trim();

                progressDialog.show();

                firestore.collection("stuffs")
                        .whereEqualTo("status","Available")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                progressDialog.dismiss();

                                List<Stuff> stuffList=queryDocumentSnapshots.toObjects(Stuff.class);
                                Log.d("tasss",stuffList.size()+"l");
                                stuffList.removeIf(new Predicate<Stuff>() {
                                    @Override
                                    public boolean test(Stuff stuff) {
                                        return !stuff.getTitle().toLowerCase(Locale.ROOT).contains(str_txt_search.toLowerCase(Locale.ROOT))
                                                &&!stuff.getDescription().toLowerCase(Locale.ROOT).contains(str_txt_search.toLowerCase(Locale.ROOT))
                                                && !stuff.getCategory_name().toLowerCase(Locale.ROOT).contains(str_txt_search.toLowerCase(Locale.ROOT));
                                    }
                                });

                                Log.d("tasss",stuffList.size()+"l");
                                StuffGridAdapter adapter=new StuffGridAdapter(stuffList,SearchActivity.this);
                                recycler_products.setAdapter(adapter);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(SearchActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                            }
                        });


            }
        });


    }
}