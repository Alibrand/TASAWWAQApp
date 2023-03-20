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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.tasawwaq.models.UserAccount;

public class ProfileActivity extends AppCompatActivity {
    EditText txt_name,txt_phone;
    AppCompatButton btn_update;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    ImageView btn_back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        txt_name = findViewById(R.id.txt_name);
        txt_phone = findViewById(R.id.txt_phone);
        btn_update = findViewById(R.id.btn_update);
        btn_back = findViewById(R.id.btn_back);


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                        progressDialog.dismiss();
                        UserAccount account=documentSnapshot.toObject(UserAccount.class);
                        txt_name.setText(account.getName());
                        txt_phone.setText(account.getPhone());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                   progressDialog.dismiss();
                   makeText(ProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                   finish();
                    }
                });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_txt_name =txt_name.getText().toString();
                String str_txt_phone =txt_phone.getText().toString();

                if(str_txt_name.isEmpty())
                {
                    txt_name.setError("Required Field");
                    return;
                }

                if(str_txt_phone.isEmpty())
                {
                    txt_phone.setError("Required Field");
                    return;
                }

                progressDialog.show();
                progressDialog.setTitle("Updating");
                firestore.collection("accounts")
                        .document(firebaseAuth.getUid())
                        .update("name",str_txt_name
                        ,"phone",str_txt_phone)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                makeText(ProfileActivity.this,"Info Updated Successfully" , LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(ProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}