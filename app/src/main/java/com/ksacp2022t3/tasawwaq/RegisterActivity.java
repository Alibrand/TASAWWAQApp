package com.ksacp2022t3.tasawwaq;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.tasawwaq.models.UserAccount;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    ImageView btn_back;
    EditText txt_email,txt_name,txt_phone,txt_password,txt_confirm_password;
    AppCompatButton btn_register;
    TextView txt_have_account;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txt_email = findViewById(R.id.txt_email);
        txt_name = findViewById(R.id.txt_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_password = findViewById(R.id.txt_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);
        btn_back = findViewById(R.id.btn_back);
        btn_register = findViewById(R.id.btn_register);
        txt_have_account = findViewById(R.id.txt_have_account);


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Creating account");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txt_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity. class);
                startActivity(intent);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_email =txt_email.getText().toString();
                String str_txt_name =txt_name.getText().toString();
                String str_txt_phone =txt_phone.getText().toString();
                String str_txt_password =txt_password.getText().toString();
                String str_txt_confirm_password =txt_confirm_password.getText().toString();

                if(str_txt_name.isEmpty())
                {
                     txt_name.setError("Required Field");
                     return;
                }
                if(str_txt_email.isEmpty())
                {
                     txt_email.setError("Required Field");
                     return;
                }
                if(!isValidEmail(str_txt_email))
                {

                    txt_email.setError("Email should be like example@stu.kau.edu.sa");
                    return;

                }
                if(str_txt_phone.isEmpty())
                {
                     txt_phone.setError("Required Field");
                     return;
                }
                if(!isValidPhone(str_txt_phone) )
                {
                    txt_phone.setError("Invalid phone format.It should be like 05X XXX XXXX");
                    return;
                }

                if(str_txt_password.isEmpty())
                {
                     txt_password.setError("Required Field");
                     return;
                }

                if(!isValidPassword(str_txt_password))
                {

                    txt_password.setError("Password should be at least 8 characters and contains a mix of letters and numbers");
                    return;

                }
                if(str_txt_confirm_password.isEmpty())
                {
                     txt_confirm_password.setError("Required Field");
                     return;
                }
                if(!str_txt_confirm_password.equals(str_txt_password))
                {
                    txt_confirm_password.setError("Passwords don't match");
                    return;
                }

                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(
                        str_txt_email,str_txt_password
                ).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        UserAccount userAccount=new UserAccount();
                        userAccount.setName(str_txt_name);
                        userAccount.setEmail(str_txt_email);
                        userAccount.setPhone(str_txt_phone);

                        DocumentReference new_doc=firestore.collection("accounts")
                                .document(authResult.getUser().getUid());
                        userAccount.setId(new_doc.getId());



                        new_doc.set(userAccount)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();

                                        SharedPreferences sharedPref = RegisterActivity.this.getSharedPreferences("tasawwaq", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor=sharedPref.edit();
                                        editor.putString("name",str_txt_name);
                                        editor.apply();

                                        makeText(RegisterActivity.this,"User Created successfully" , LENGTH_LONG).show();
                                        Intent intent = new Intent(RegisterActivity.this,HomeActivity. class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("EXIT", true);

                                        startActivity(intent);
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        makeText(RegisterActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         makeText(RegisterActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                    }
                });








            }
        });

    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@stu.kau.edu.sa";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }


    public static boolean isValidPhone(final String phone) {

        Pattern pattern;
        Matcher matcher;
        final String SA_PHONE_PATTERN = "^(5|05)(5|0|3|6|4|9|1|8|7)([0-9]{7})$";
        pattern = Pattern.compile(SA_PHONE_PATTERN);
        matcher = pattern.matcher(phone);

        return matcher.matches();

    }
}