package com.ksacp2022t3.tasawwaq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    AppCompatButton btn_categories,btn_accounts;
    LinearLayoutCompat btn_logout;


    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        btn_categories = findViewById(R.id.btn_categories);
        btn_accounts = findViewById(R.id.btn_accounts);
        btn_logout = findViewById(R.id.btn_logout);

        firebaseAuth=FirebaseAuth.getInstance();



        btn_logout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firebaseAuth.signOut();
                        Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );



        btn_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminManageCategoriesActivity. class);
                startActivity(intent);
            }
        });

        btn_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminManageAccountsActivity. class);
                startActivity(intent);
            }
        });
    }
}