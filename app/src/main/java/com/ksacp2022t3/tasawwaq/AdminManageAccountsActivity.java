package com.ksacp2022t3.tasawwaq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.tasawwaq.adapters.AccountsListAdapter;
import com.ksacp2022t3.tasawwaq.adapters.CategoryListAdapter;
import com.ksacp2022t3.tasawwaq.models.Category;
import com.ksacp2022t3.tasawwaq.models.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class AdminManageAccountsActivity extends AppCompatActivity {
    RecyclerView recycler_categories;
    ImageView btn_back;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_accounts);
        recycler_categories = findViewById(R.id.recycler_accounts);
        btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    }
        );

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        load_accounts();


    }

    private void load_accounts() {
        progressDialog.show();
        firestore.collection("accounts")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<UserAccount> userAccountList=new ArrayList<>();
                        for(DocumentSnapshot doc:queryDocumentSnapshots.getDocuments())
                        {
                            UserAccount userAccount=doc.toObject(UserAccount.class);
                            userAccountList.add(userAccount);
                        }
                        AccountsListAdapter adapter=new AccountsListAdapter(userAccountList, AdminManageAccountsActivity.this);
                        recycler_categories.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AdminManageAccountsActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_accounts();
    }
}