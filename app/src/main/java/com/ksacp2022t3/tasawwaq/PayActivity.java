package com.ksacp2022t3.tasawwaq;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.tasawwaq.models.Order;

import java.util.List;

public class PayActivity extends AppCompatActivity {
    EditText txt_card_number,txt_expiry,txt_cvv;
    TextView txt_price;
    AppCompatButton btn_pay;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    ImageView btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        txt_card_number = findViewById(R.id.txt_card_number);
        txt_expiry = findViewById(R.id.txt_expiry);
        txt_cvv = findViewById(R.id.txt_cvv);
        btn_pay = findViewById(R.id.btn_pay);
        txt_price = findViewById(R.id.txt_price);
        btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        String order_id=getIntent().getStringExtra("order_id");
        Double price=getIntent().getDoubleExtra("price",0);
        String stuff_id=getIntent().getStringExtra("stuff_id");
        String main_category=getIntent().getStringExtra("main_category");
        txt_price.setText("Amount to pay :"+ price.intValue()+" S.R");




        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_card_number =txt_card_number.getText().toString();
                String str_txt_expiry =txt_expiry.getText().toString();
                String str_txt_cvv =txt_cvv.getText().toString();

                if(str_txt_card_number.isEmpty())
                {
                     txt_card_number.setError("Required Field");
                     return;
                }

                if(str_txt_expiry.isEmpty())
                {
                     txt_expiry.setError("Required Field");
                     return;
                }

                if(str_txt_cvv.isEmpty())
                {
                     txt_cvv.setError("Required Field");
                     return;
                }


                progressDialog.show();
                firestore.collection("orders")
                        .document(order_id)
                        .update("status","Paid")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                if(main_category.equals("Services"))
                                {
                                    finish();
                                    makeText(PayActivity.this,"Your order has been completed" , LENGTH_LONG).show();
                                    return;
                                }

                                //only products should be set to sold

                                firestore.collection("stuffs")
                                        .document(stuff_id)
                                        .update("status","Sold",
                                        "sold_date", FieldValue.serverTimestamp())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                firestore.collection("orders")
                                                        .whereEqualTo("stuff_id",stuff_id)
                                                        .whereNotEqualTo("status","Paid")
                                                        .get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                List<Order> orders=queryDocumentSnapshots.toObjects(Order.class);
                                                                for(Order o:orders)
                                                                {
                                                                    firestore.collection("orders")
                                                                            .document(o.getId())
                                                                            .delete();
                                                                }

                                                                progressDialog.dismiss();
                                                                finish();
                                                                makeText(PayActivity.this,"Your order has been completed" , LENGTH_LONG).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDialog.dismiss();
                                                                makeText(PayActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                makeText(PayActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                            }
                                        });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();

                                makeText(PayActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });

            }
        });


    }
}