package com.ksacp2022t3.tasawwaq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    AppCompatButton btn_register,btn_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);

        firebaseAuth=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null)
        {
            Intent intent;
            if(firebaseAuth.getCurrentUser().getEmail().equals("admin@tasawwaq.com"))
            {
                intent = new Intent(MainActivity.this,AdminHomeActivity. class);
            }
            else {
                intent = new Intent(MainActivity.this, HomeActivity.class);
            }
             startActivity(intent);
            finish();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity. class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity. class);
                startActivity(intent);
            }
        });


    }
}