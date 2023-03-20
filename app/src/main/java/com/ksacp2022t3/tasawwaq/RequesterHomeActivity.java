package com.ksacp2022t3.tasawwaq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.tasawwaq.adapters.CategoryListAdapter;
import com.ksacp2022t3.tasawwaq.adapters.MostRatedListAdapter;
import com.ksacp2022t3.tasawwaq.adapters.RecentlyListAdapter;
import com.ksacp2022t3.tasawwaq.models.Category;
import com.ksacp2022t3.tasawwaq.models.Stuff;

import java.util.List;

public class RequesterHomeActivity extends AppCompatActivity {

    RecyclerView recycler_recently,recycler_services,recycler_products,recycler_rated;
    ProgressBar progress_recently,progress_services,progress_products,progress_rated;

    FirebaseFirestore firestore;

    ImageView btn_back,btn_search,btn_my_orders,btn_profile;

    Runnable mRunnable;
    Handler mHandler;
    RecentlyListAdapter recently_adapter;
    MostRatedListAdapter  most_adapter;
    int current=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_home);
        recycler_recently = findViewById(R.id.recycler_recently);
        recycler_services = findViewById(R.id.recycler_services);
        recycler_products = findViewById(R.id.recycler_products);
        progress_recently = findViewById(R.id.progress_recently);
        progress_services = findViewById(R.id.progress_services);
        progress_products = findViewById(R.id.progress_products);
        recycler_rated = findViewById(R.id.recycler_rated);
        progress_rated = findViewById(R.id.progress_rated);
        btn_profile = findViewById(R.id.btn_profile);
        btn_back = findViewById(R.id.btn_back);
        btn_search = findViewById(R.id.btn_search);
        btn_my_orders = findViewById(R.id.btn_my_orders);
        


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequesterHomeActivity.this,SearchActivity. class);
                startActivity(intent);
            }
        });

        btn_my_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequesterHomeActivity.this,MyOrdersActivity. class);
                startActivity(intent);
            }
        });

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequesterHomeActivity.this,ProfileActivity. class);
                startActivity(intent);
            }
        });

        
        firestore=FirebaseFirestore.getInstance();

        mHandler=new Handler();
        mRunnable=new Runnable() {
            @Override
            public void run() {
                if(recently_adapter!=null  && recently_adapter.getItemCount()>0) {
                    recycler_recently.smoothScrollToPosition(current);
                    if (current == recently_adapter.getItemCount() - 1)
                        current = 0;
                    else
                        current++;
                }
                if(most_adapter!=null && most_adapter.getItemCount()>0) {
                    recycler_rated.smoothScrollToPosition(current);
                }

                 mHandler.postDelayed(mRunnable,4000);


            }
        };



        load_category_services();
        load_category_products();
        load_added_recently();
        load_most_rated();





    }

    void load_category_services(){
        firestore.collection("categories")
                .whereEqualTo("main_category","Services")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Category> categoryList=queryDocumentSnapshots.toObjects(Category.class);
                        CategoryListAdapter adapter=new CategoryListAdapter(categoryList,RequesterHomeActivity.this);
                        recycler_services.setAdapter(adapter);
                        progress_services.setVisibility(View.GONE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                         Toast.makeText(RequesterHomeActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        progress_services.setVisibility(View.GONE);
                    }
                });
    }
    void load_category_products(){
        firestore.collection("categories")
                .whereEqualTo("main_category","Products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Category> categoryList=queryDocumentSnapshots.toObjects(Category.class);
                        CategoryListAdapter adapter=new CategoryListAdapter(categoryList,RequesterHomeActivity.this);
                        recycler_products.setAdapter(adapter);
                        progress_products.setVisibility(View.GONE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RequesterHomeActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        progress_products.setVisibility(View.GONE);
                    }
                });
    }

    void load_added_recently(){
        firestore.collection("stuffs")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .whereEqualTo("status","Available")

                .limit(5)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Stuff> stuffList=queryDocumentSnapshots.toObjects(Stuff.class);
                        recently_adapter=new RecentlyListAdapter (stuffList,RequesterHomeActivity.this);
                        recycler_recently.setAdapter(recently_adapter);
                        progress_recently.setVisibility(View.GONE);

                        if(recently_adapter.getItemCount()>0)
                        {
                            mHandler.postDelayed(mRunnable,4000);
                        }




                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RequesterHomeActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        progress_recently.setVisibility(View.GONE);
                    }
                });
    }

    void load_most_rated(){
        firestore.collection("stuffs")
                .whereEqualTo("status","Available")
                .orderBy("reviews_count", Query.Direction.DESCENDING)
                .orderBy("rate", Query.Direction.DESCENDING)

                .limit(5)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<Stuff> stuffList=queryDocumentSnapshots.toObjects(Stuff.class);
                        most_adapter=new MostRatedListAdapter (stuffList,RequesterHomeActivity.this);
                        recycler_rated.setAdapter(most_adapter);
                        progress_rated.setVisibility(View.GONE);




                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RequesterHomeActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        progress_rated.setVisibility(View.GONE);
                    }
                });
    }
}