package com.ksacp2022t3.tasawwaq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022t3.tasawwaq.adapters.ReviewListAdapter;
import com.ksacp2022t3.tasawwaq.models.GlideApp;
import com.ksacp2022t3.tasawwaq.models.Order;
import com.ksacp2022t3.tasawwaq.models.Review;
import com.ksacp2022t3.tasawwaq.models.Stuff;

import java.util.List;

public class ViewStuffActivity extends AppCompatActivity {
    TextView txt_title,txt_description,txt_owner,txt_price
            ,txt_order_status,txt_sold;
    ImageView stuff_image,btn_back,btn_chat,btn_rate,btn_order;

    RecyclerView recycler_reviews;
    LinearLayoutCompat layout_reviews,layout_order_status;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;


    ProgressDialog progressDialog;
    String stuff_id;

    Stuff stuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stuff);
        txt_title = findViewById(R.id.txt_title);
        txt_description = findViewById(R.id.txt_description);
        txt_owner = findViewById(R.id.txt_owner);
        stuff_image = findViewById(R.id.stuff_image);
        btn_back = findViewById(R.id.btn_back);
        btn_chat = findViewById(R.id.btn_chat);
        txt_price = findViewById(R.id.txt_price);
        recycler_reviews = findViewById(R.id.recycler_reviews);
        layout_reviews = findViewById(R.id.layout_reviews);
        btn_rate = findViewById(R.id.btn_rate);
        txt_order_status = findViewById(R.id.txt_order_status);
        layout_order_status = findViewById(R.id.layout_order_status);
        btn_order = findViewById(R.id.btn_order);
        txt_sold = findViewById(R.id.txt_sold);









        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();


         stuff_id=getIntent().getStringExtra("stuff_id");

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);


                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });




            load_stuff_info();






    }

    private void load_stuff_info() {
        progressDialog.show();

        firestore.collection("stuffs")
                .document(stuff_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        stuff=documentSnapshot.toObject(Stuff.class);

                        StorageReference ref=storage.getReference();
                        StorageReference image=ref.child("stuff_images/"+stuff.getImage());

                        GlideApp.with(ViewStuffActivity.this)
                                .load(image)
                                .fitCenter()
                                .into(stuff_image);

                        txt_title.setText(stuff.getTitle());
                        txt_description.setText(stuff.getDescription());
                        txt_owner.setText(stuff.getOwner_name());
                        txt_owner.setPaintFlags(txt_owner.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                        txt_price.setText((int)stuff.getPrice() +" S.R");

                        if(stuff.getStatus().equals("Sold"))
                        {
                            btn_order.setVisibility(View.GONE);
                            layout_order_status.setVisibility(View.GONE);
                            txt_price.setVisibility(View.GONE);
                            txt_sold.setVisibility(View.VISIBLE);
                        }
                        else{
                            btn_order.setVisibility(View.VISIBLE);
                            check_order();
                        }

                        btn_chat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ViewStuffActivity.this,ChatActivity. class);
                                intent.putExtra("receiver_id",stuff.getOwner_id());
                                intent.putExtra("receiver_name",stuff.getOwner_name());
                                startActivity(intent);
                            }
                        });

                        load_reviews();



                        txt_owner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                        btn_rate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ViewStuffActivity.this,AddReviewActivity. class);
                                intent.putExtra("stuff_id",stuff_id);
                                startActivity(intent);
                            }
                        });



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ViewStuffActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        finish();

                    }
                });

    }

    void load_reviews(){

        firestore.collection("stuffs")
                .document(stuff_id)
                .collection("reviews")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(queryDocumentSnapshots.size()==0) {
                            layout_reviews.setVisibility(View.GONE);
                        }
                        else {
                            layout_reviews.setVisibility(View.VISIBLE);
                            List<Review> reviewList=queryDocumentSnapshots.toObjects(Review.class);
                            ReviewListAdapter adapter=new ReviewListAdapter(reviewList,ViewStuffActivity.this);
                            recycler_reviews.setAdapter(adapter);

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ViewStuffActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });
    }

    void check_order(){
        String order_id=stuff_id+"_"+firebaseAuth.getUid();
        progressDialog.setTitle("Loading");
        progressDialog.show();
        firestore.collection("orders")
                .document(order_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        if(documentSnapshot.exists())
                        {
                            Order order=documentSnapshot.toObject(Order.class);

                            btn_order.setImageResource(R.drawable.ic_baseline_remove_shopping_cart_24);
                            layout_order_status.setVisibility(View.VISIBLE);
                            txt_order_status.setText(order.getStatus());


                            if(order.getStatus().equals("Pending")||order.getStatus().equals("Accepted"))
                            {
                                btn_order.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        progressDialog.show();
                                        progressDialog.setTitle("Canceling Order");
                                        firestore.collection("orders")
                                                .document(order.getId())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressDialog.dismiss();
                                                        check_order();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ViewStuffActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                });

                            }
                            else
                            {
                                btn_order.setVisibility(View.GONE);
                            }

                            if(order.getStatus().equals("Accepted"))
                            {
                                txt_order_status.setText("Ready to Pay..tap here");
                                txt_order_status.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ViewStuffActivity.this,PayActivity. class);
                                        intent.putExtra("order_id",order_id);
                                        intent.putExtra("price",stuff.getPrice());
                                        intent.putExtra("stuff_id",stuff.getId());
                                        intent.putExtra("main_category",stuff.getMain_category());
                                        startActivity(intent);

                                    }
                                });

                            }



                        }
                        else
                        {
                            layout_order_status.setVisibility(View.GONE);
                            btn_order.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
                            btn_order.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Order order=new Order();
                                    order.setStuff_id(stuff_id);
                                    order.setStuff_name(stuff.getTitle());
                                    order.setId(order_id);
                                    order.setPrice(stuff.getPrice());
                                    order.setRequester_uid(firebaseAuth.getUid());
                                    if(stuff.isRequest_needed()) {
                                        order.setStatus("Pending");
                                    } else {
                                        order.setStatus("Accepted");
                                    }
                                    order.setOwner_id(stuff.getOwner_id());
                                    SharedPreferences sharedPref = ViewStuffActivity.this.getSharedPreferences("tasawwaq", Context.MODE_PRIVATE);
                                    String sender_name = sharedPref.getString("name", "");
                                    order.setRequester_name(sender_name);

                                    progressDialog.show();
                                    progressDialog.setTitle("Sending Order");

                                   firestore.collection("orders")
                                           .document(order.getId())
                                           .set(order)
                                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void unused) {
                                                   progressDialog.dismiss();
                                                   check_order();

                                               }
                                           }).addOnFailureListener(new OnFailureListener() {
                                               @Override
                                               public void onFailure(@NonNull Exception e) {
                                                   progressDialog.dismiss();
                                                   Toast.makeText(ViewStuffActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                                               }
                                           });
                                }
                            });
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_stuff_info();
    }
}