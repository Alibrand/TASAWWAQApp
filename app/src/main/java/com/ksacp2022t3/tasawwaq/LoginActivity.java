package com.ksacp2022t3.tasawwaq;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.tasawwaq.models.UserAccount;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    ImageView btn_back;
    EditText txt_email,txt_password;
    TextView txt_forget_password,txt_register;
    AppCompatButton btn_login;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        btn_back = findViewById(R.id.btn_back);
        btn_login = findViewById(R.id.btn_login);
        txt_forget_password = findViewById(R.id.txt_forget_password);
        txt_register = findViewById(R.id.txt_register);



        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txt_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity. class);
                startActivity(intent);
            }
        });

        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this,RegisterActivity. class);
                startActivity(intent);
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_email =txt_email.getText().toString();
                String str_txt_password =txt_password.getText().toString();


                if(str_txt_email.isEmpty())
                {
                    txt_email.setError("Required Field");
                    return;
                }

                if(str_txt_password.isEmpty())
                {
                    txt_password.setError("Required Field");
                    return;
                }


                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(
                        str_txt_email,str_txt_password
                ).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {


                        if(authResult.getUser().getEmail().equals("admin@tasawwaq.com"))
                        {
                            progressDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this,AdminHomeActivity. class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("EXIT", true);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            firestore.collection("accounts")
                                    .document(authResult.getUser().getUid())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            progressDialog.dismiss();
                                            UserAccount account=documentSnapshot.toObject(UserAccount.class);
                                            if(account.getStatus().equals("Active"))
                                            {
                                                SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("tasawwaq", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPref.edit();
                                                editor.putString("name",account.getName());
                                                editor.apply();
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("EXIT", true);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {

                                                firebaseAuth.signOut();
                                                Intent intent = new Intent(LoginActivity.this,SorryActivity.
                                                class);
                                                startActivity(intent);
                                                makeText(LoginActivity.this,"Sorry..Your account has been rejected by Admin" , LENGTH_LONG).show();
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                             makeText(LoginActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                        }
                                    });

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(LoginActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                    }
                });








            }
        });


    }
}