package com.ksacp2022t3.tasawwaq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {

    LinearLayoutCompat btn_logout;
    FirebaseAuth firebaseAuth;
    ImageView btn_inbox,btn_profile;
    TextView txt_new_messages;
    AppCompatButton btn_provider,btn_requester;

    int total_new_messages=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btn_inbox = findViewById(R.id.btn_inbox);
        btn_logout = findViewById(R.id.btn_logout);
        btn_provider = findViewById(R.id.btn_provider);
        btn_requester = findViewById(R.id.btn_requester);
        btn_profile = findViewById(R.id.btn_profile);
        txt_new_messages = findViewById(R.id.txt_new_messages);




        btn_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,InboxActivity. class);
                startActivity(intent);
            }
        });

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,ProfileActivity. class);
                startActivity(intent);
            }
        });



        btn_provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,ProviderActivity. class);
                startActivity(intent);
            }
        });

        btn_requester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,RequesterHomeActivity. class);
                startActivity(intent);
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();






        btn_logout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firebaseAuth.signOut();
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );

    }
    private  void  check_new_messages(){
        txt_new_messages.setVisibility(View.GONE);
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("inbox")
                .whereArrayContains("users_ids",firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        total_new_messages=0;
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()) {
                            doc.getReference()
                                    .collection("messages")
                                    .whereEqualTo("to",firebaseAuth.getUid())
                                    .whereEqualTo("status","unseen")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            int new_messages_count= queryDocumentSnapshots.getDocuments().size();
                                            total_new_messages+=new_messages_count;
                                            if(total_new_messages>0)
                                                txt_new_messages.setVisibility(View.VISIBLE);
                                            txt_new_messages.setText(String.valueOf(total_new_messages));
                                        }
                                    })
                            ;
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        check_new_messages();
    }

}