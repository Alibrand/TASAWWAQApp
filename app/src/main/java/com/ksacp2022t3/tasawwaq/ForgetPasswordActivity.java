package com.ksacp2022t3.tasawwaq;

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

public class ForgetPasswordActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText txt_email;
    AppCompatButton btn_send;
    ProgressDialog progressDialog;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        txt_email = findViewById(R.id.txt_email);
        btn_send = findViewById(R.id.btn_send);
        firebaseAuth=FirebaseAuth.getInstance();
        btn_back = findViewById(R.id.btn_back);


        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Sending");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_email =txt_email.getText().toString();

                if(str_txt_email.isEmpty())
                {
                     txt_email.setError("Required Field");
                     return;
                }

                progressDialog.show();
                firebaseAuth.sendPasswordResetEmail(str_txt_email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(ForgetPasswordActivity.this,"A reset link was sent to your inbox..check and follow the instructions" , Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ForgetPasswordActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                            }
                        });



            }
        });
    }
}