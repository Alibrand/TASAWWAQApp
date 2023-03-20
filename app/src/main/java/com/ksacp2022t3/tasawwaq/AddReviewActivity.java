package com.ksacp2022t3.tasawwaq;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.tasawwaq.models.Review;

import java.util.List;

public class AddReviewActivity extends AppCompatActivity {
    ImageView btn_back;
    EditText txt_comment;
    RatingBar rating_bar;
    AppCompatButton btn_send;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        btn_back = findViewById(R.id.btn_back);
        txt_comment = findViewById(R.id.txt_comment);
        rating_bar = findViewById(R.id.rating_bar);
        btn_send = findViewById(R.id.btn_send);


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Sending");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        String stuff_id=getIntent().getStringExtra("stuff_id");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String str_txt_comment =txt_comment.getText().toString().trim();




              progressDialog.show();

                Review review=new Review();
                review.setComment(str_txt_comment);
                review.setRating(rating_bar.getRating());
                SharedPreferences sharedPref = AddReviewActivity.this.getSharedPreferences("tasawwaq", Context.MODE_PRIVATE);
                String sender_name = sharedPref.getString("name", "");
                review.setName(sender_name);
                review.setId(firebaseAuth.getUid());

               DocumentReference doc= firestore.collection("stuffs")
                        .document(stuff_id)
                        .collection("reviews")
                        .document();
               review.setId(doc.getId());

               doc.set(review)
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               progressDialog.dismiss();

                                firestore.collection("stuffs")
                                       .document(stuff_id)
                                       .collection("reviews")
                                               .get()
                                                       .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                           @Override
                                                           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                               List<Review> reviewList=queryDocumentSnapshots.toObjects(Review.class);
                                                               if(reviewList.size()==0)
                                                                   return;

                                                              float sumRate=0;

                                                              for(Review r:reviewList)
                                                              {
                                                                  sumRate+=r.getRating();
                                                              }

                                                              float rate=sumRate/reviewList.size();

                                                               firestore.collection("stuffs")
                                                                       .document(stuff_id)
                                                                       .update("rate",rate,"reviews_count",reviewList.size());
                                                           }
                                                       });




                               makeText(AddReviewActivity.this,"You review has been sent successfully" , LENGTH_LONG).show();
                               finish();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                           progressDialog.dismiss();
                           makeText(AddReviewActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                           }
                       });

            }
        });

    }
}