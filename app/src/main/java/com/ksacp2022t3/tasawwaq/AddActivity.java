package com.ksacp2022t3.tasawwaq;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022t3.tasawwaq.models.Category;
import com.ksacp2022t3.tasawwaq.models.GlideApp;
import com.ksacp2022t3.tasawwaq.models.Stuff;
import com.ksacp2022t3.tasawwaq.models.UserAccount;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {

    String main_category;
    AppCompatButton btn_save;
    ImageView btn_take_pic,btn_upload_pic,btn_back,stuff_image;
    Spinner sp_category;
    EditText txt_title,txt_description,txt_price;
    Switch switch_need_request;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;

    ProgressDialog progressDialog;

    UserAccount current_user;

    Uri imageUri;
    List<Category> categories=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        btn_save = findViewById(R.id.btn_save);
        btn_take_pic = findViewById(R.id.btn_take_pic);
        btn_upload_pic = findViewById(R.id.btn_upload_pic);
        sp_category = findViewById(R.id.sp_category);
        txt_title = findViewById(R.id.txt_title);
        txt_description = findViewById(R.id.txt_description);
        btn_back = findViewById(R.id.btn_back);
        txt_price = findViewById(R.id.txt_price);
        stuff_image = findViewById(R.id.stuff_image);
        switch_need_request = findViewById(R.id.switch_need_request);






        main_category =getIntent().getStringExtra("main_category");


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();


        firestore.collection("accounts")
                .document(firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        current_user=documentSnapshot.toObject(UserAccount.class);

                        firestore.collection("categories")
                                .whereEqualTo("main_category", main_category)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        progressDialog.dismiss();
                                        categories=queryDocumentSnapshots.toObjects(Category.class);
                                        ArrayAdapter<Category> adapter=new ArrayAdapter<Category>(AddActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,categories);
                                        sp_category.setAdapter(adapter);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        makeText(AddActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                        finish();
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                          makeText(AddActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                          finish();
                    }
                });



        btn_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.requestPermissions(AddActivity.this, new String[] {Manifest.permission.CAMERA}, 1);
                    if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED)
                        startActivityForResult(takePicture, 100);
                }
                else{
                    startActivityForResult(takePicture, 100);

                }
            }
        });

        btn_upload_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create an instance of the
                // intent of the type image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(intent, "Select Pictures"), 110);

            }
        });



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_title =txt_title.getText().toString();
                String str_txt_description =txt_description.getText().toString();
                Category selected_category= (Category) sp_category.getSelectedItem();

                if(str_txt_title.isEmpty())
                {
                     txt_title.setError("Required Field");
                     return;
                }
                if(str_txt_description.isEmpty())
                {
                     txt_description.setError("Required Field");
                     return;
                }
                if(txt_price.getText().toString().isEmpty())
                {
                    txt_price.setError("Required Field");
                    return;
                }
                if(imageUri==null)
                {
                    makeText(AddActivity.this,"You should upload  one image" , LENGTH_LONG).show();
                    return;
                }



                progressDialog.show();
                progressDialog.setTitle("Uploading Image");



                    //define unique image name usinf UUID generator
                    String imageName = UUID.randomUUID().toString()+".jpg";

                    // Defining the child of storageReference
                    StorageReference storageReference =storage.getReference();
                    StorageReference ref = storageReference.child("stuff_images/"+imageName);

                    ref.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        progressDialog.setTitle("Uploading Data");
                                       DocumentReference newStuff= firestore.collection("stuffs")
                                                .document();

                                        Stuff stuff=new Stuff();
                                        stuff.setTitle(str_txt_title);
                                        stuff.setDescription(str_txt_description);
                                        stuff.setCategory_id(selected_category.getId());
                                        stuff.setCategory_name(selected_category.getName());
                                        stuff.setImage(imageName);
                                        stuff.setId(newStuff.getId());
                                        stuff.setOwner_id(current_user.getId());
                                        stuff.setOwner_name(current_user.getName());
                                        stuff.setMain_category(main_category);
                                        stuff.setRequest_needed(switch_need_request.isChecked());
                                        double price=Double.parseDouble(txt_price.getText().toString());
                                        stuff.setPrice(price);

                                        newStuff.set(stuff)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        //increase category counter
                                                        firestore.collection("categories")
                                                                        .document(selected_category.getId())
                                                                                .update("items_count", FieldValue.increment(1));
                                                        progressDialog.dismiss();
                                                        makeText(AddActivity.this,"Data has been saved successfully" , LENGTH_LONG).show();
                                                   finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                         makeText(AddActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                                    }
                                                });



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    progressDialog.dismiss();
                                    makeText(AddActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

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

                  imageUri = data.getData();

                GlideApp.with(AddActivity.this)
                        .load(imageUri)
                        .into(stuff_image);

            } else if(requestCode==100) {

                Bitmap image= (Bitmap) data.getExtras().get("data");
                //prepare image to upload
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);


                imageUri=Uri.parse(path);

                GlideApp.with(AddActivity.this)
                        .load(imageUri)
                        .into(stuff_image);

            }

        }
}

}