package com.ksacp2022t3.tasawwaq;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022t3.tasawwaq.models.Category;
import com.ksacp2022t3.tasawwaq.models.GlideApp;

import java.util.UUID;

public class AdminAddCategoryActivity extends AppCompatActivity {
    ImageView btn_back,btn_image;
    EditText txt_name,txt_description;
    RadioGroup group_main_category;
    AppCompatButton btn_save;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    FirebaseStorage storage;
    Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_category);
        btn_back = findViewById(R.id.btn_back);
        btn_image = findViewById(R.id.btn_image);
        txt_name = findViewById(R.id.txt_name);
        txt_description = findViewById(R.id.txt_description);
        group_main_category = findViewById(R.id.group_main_category);
        btn_save = findViewById(R.id.btn_save);

        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Saving");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 110);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_name =txt_name.getText().toString();
                String str_txt_description =txt_description.getText().toString();
                RadioButton selected_cat=findViewById(group_main_category.getCheckedRadioButtonId());
                String main_cat=selected_cat.getText().toString();

                if(str_txt_name.isEmpty())
                {
                     txt_name.setError("Required Field");
                     return;
                }
                if(str_txt_description.isEmpty())
                {
                     txt_description.setError("Required Field");
                     return;
                }
                if(image==null)
                {
                    makeText(AdminAddCategoryActivity.this,"You should upload an image" , LENGTH_LONG).show();
                return;
                }

                progressDialog.show();
                //define unique image name usinf UUID generator
                String imageName = UUID.randomUUID().toString() + ".jpg";

                // Defining the child of storageReference
                StorageReference storageReference = storage.getReference();
                StorageReference ref = storageReference.child("categories_images/" + imageName);


                // adding listeners on upload
                // or failure of image

                ref.putFile(image)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                DocumentReference new_document= firestore.collection("categories")
                                        .document();
                                Category category=new Category();
                                category.setName(str_txt_name);
                                category.setMain_category(main_cat);
                                category.setDescription(str_txt_description);
                                category.setId(new_document.getId());
                                category.setImage_url(imageName);

                                new_document.set(category)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                makeText(AdminAddCategoryActivity.this,"Category Created successfully" , LENGTH_LONG).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                makeText(AdminAddCategoryActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(AdminAddCategoryActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                            }
                        });







            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 110) {
                // Get the url of the image from data
                image = data.getData();
                if (null != image) {
                    // update the preview image in the layout
                    //selected_image = (Bitmap)data.getExtras().get("data");
                    GlideApp.with(this)
                            .load(image)

                            .into(btn_image);
                }
            }

        }
    }
}