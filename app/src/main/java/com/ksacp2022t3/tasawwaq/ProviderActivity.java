package com.ksacp2022t3.tasawwaq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.tasawwaq.adapters.StuffListAdapter;
import com.ksacp2022t3.tasawwaq.models.Stuff;
import com.ksacp2022t3.tasawwaq.models.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class ProviderActivity extends AppCompatActivity {

    ImageView btn_add_service,btn_add_stuff,btn_back,btn_requests,btn_report;
    TextView txt_name;
    RecyclerView recycler_services,recycler_stuffs;


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    UserAccount current_user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        btn_add_service = findViewById(R.id.btn_add_service);
        btn_add_stuff = findViewById(R.id.btn_add_stuff);
        recycler_services = findViewById(R.id.recycler_services);
        recycler_stuffs = findViewById(R.id.recycler_stuffs);
        txt_name = findViewById(R.id.txt_name);
        btn_back = findViewById(R.id.btn_back);
        btn_requests = findViewById(R.id.btn_requests);
        btn_report = findViewById(R.id.btn_report);



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProviderActivity.this,OrdersActivity. class);
                startActivity(intent);
            }
        });

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProviderActivity.this,ReportActivity. class);
                startActivity(intent);
            }
        });


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);



         progressDialog.show();
         firestore.collection("accounts")
                         .document(firebaseAuth.getUid())
                                 .get()
                                         .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                             @Override
                                             public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                 current_user=documentSnapshot.toObject(UserAccount.class);
                                                txt_name.setText(current_user.getName());


                                             }
                                         }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         progressDialog.dismiss();

                          Toast.makeText(ProviderActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                         finish();
                     }
                 });



        btn_add_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProviderActivity.this,AddActivity. class);
                intent.putExtra("main_category","Services");
                startActivity(intent);
            }
        });

        btn_add_stuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProviderActivity.this,AddActivity. class);
                intent.putExtra("main_category","Products");
                startActivity(intent);
            }
        });

    }

    void load_my_stuffs(){
        progressDialog.show();

        firestore.collection("stuffs")
                .whereEqualTo("owner_id",firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<Stuff> stuffList=queryDocumentSnapshots.toObjects(Stuff.class);

                        List<Stuff> services=new ArrayList<>();
                        List<Stuff> products=new ArrayList<>();

                        for (Stuff stuff:stuffList
                             ) {
                            if(stuff.getMain_category().equals("Products")&& stuff.getStatus().equals("Available"))
                                products.add(stuff);
                             else if(stuff.getMain_category().equals("Services"))
                                 services.add(stuff);
                        }


                        StuffListAdapter services_adapter=new StuffListAdapter(services,ProviderActivity.this);
                        StuffListAdapter products_adapter=new StuffListAdapter(products,ProviderActivity.this);

                        recycler_services.setAdapter(services_adapter);
                        recycler_stuffs.setAdapter(products_adapter);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                         Toast.makeText(ProviderActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                         progressDialog.dismiss();
                         finish();
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        load_my_stuffs();
    }
}