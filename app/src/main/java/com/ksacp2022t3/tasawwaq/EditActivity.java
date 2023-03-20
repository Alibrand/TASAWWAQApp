package com.ksacp2022t3.tasawwaq;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

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
import com.ksacp2022t3.tasawwaq.adapters.ImageListAdapter;
import com.ksacp2022t3.tasawwaq.models.Category;
import com.ksacp2022t3.tasawwaq.models.GlideApp;
import com.ksacp2022t3.tasawwaq.models.Stuff;
import com.ksacp2022t3.tasawwaq.models.UserAccount;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EditActivity extends AppCompatActivity {
    String main_category, stuff_id;
    AppCompatButton btn_save, btn_delete;
    ImageView btn_take_pic, btn_upload_pic, btn_back, stuff_image;
    Spinner sp_category;
    Switch switch_need_request;

    EditText txt_title, txt_description, txt_price;


    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;


    ProgressDialog progressDialog;

    UserAccount current_user;


    Uri imageUri;
    List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        btn_save = findViewById(R.id.btn_save);
        btn_take_pic = findViewById(R.id.btn_take_pic);
        btn_upload_pic = findViewById(R.id.btn_upload_pic);
        sp_category = findViewById(R.id.sp_category);
        stuff_image = findViewById(R.id.stuff_image);
        switch_need_request = findViewById(R.id.switch_need_request);



        txt_title = findViewById(R.id.txt_title);
        txt_description = findViewById(R.id.txt_description);
        btn_back = findViewById(R.id.btn_back);
        txt_price = findViewById(R.id.txt_price);

        btn_delete = findViewById(R.id.btn_delete);


        main_category = getIntent().getStringExtra("main_category");
        stuff_id = getIntent().getStringExtra("stuff_id");


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
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
                        current_user = documentSnapshot.toObject(UserAccount.class);

                        firestore.collection("categories")
                                .whereEqualTo("main_category", main_category)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        categories = queryDocumentSnapshots.toObjects(Category.class);
                                        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(EditActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories);
                                        sp_category.setAdapter(adapter);

                                        load_stuff();


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        makeText(EditActivity.this, "Error :" + e.getMessage(), LENGTH_LONG).show();
                                        finish();
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(EditActivity.this, "Error :" + e.getMessage(), LENGTH_LONG).show();
                        finish();
                    }
                });


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.setTitle("Delete Item");
                firestore.collection("stuffs")
                        .document(stuff_id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Category selected = (Category) sp_category.getSelectedItem();
                                firestore.collection("categories")
                                        .document(selected.getId())
                                        .update("items_count", FieldValue.increment(-1))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();

                                            }
                                        });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(EditActivity.this, "Error :" + e.getMessage(), LENGTH_LONG).show();
                                progressDialog.dismiss();

                            }
                        });
            }
        });


        btn_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(EditActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                    if (ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED)
                        startActivityForResult(takePicture, 100);
                } else {
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
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(intent, "Select Pictures"), 110);

            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_title = txt_title.getText().toString();
                String str_txt_description = txt_description.getText().toString();
                Category selected_category = (Category) sp_category.getSelectedItem();

                if (str_txt_title.isEmpty()) {
                    txt_title.setError("Required Field");
                    return;
                }
                if (str_txt_description.isEmpty()) {
                    txt_description.setError("Required Field");
                    return;
                }
                if (txt_price.getText().toString().isEmpty()) {
                    txt_price.setError("Required Field");
                    return;
                }


                progressDialog.show();



                if (imageUri == null) {
                    progressDialog.setTitle("Updating Data");
                    double price = Double.parseDouble(txt_price.getText().toString());
                    firestore.collection("stuffs")
                            .document(stuff_id)
                            .update(
                                    "title", str_txt_title,
                                    "category_id", selected_category.getId(),
                                    "category_name", selected_category.getName(),
                                    "description", str_txt_description,
                                    "price", price,
                                    "request_needed",switch_need_request.isChecked()

                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    progressDialog.dismiss();
                                    makeText(EditActivity.this, "Data has been saved successfully", LENGTH_LONG).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    makeText(EditActivity.this, "Error :" + e.getMessage(), LENGTH_LONG).show();
                                }
                            });
                } else {

                    progressDialog.setTitle("Uploading Image");
                    //define unique image name usinf UUID generator
                    String imageName = UUID.randomUUID().toString() + ".jpg";

                    // Defining the child of storageReference
                    StorageReference storageReference = storage.getReference();
                    StorageReference ref = storageReference.child("stuff_images/" + imageName);

                    ref.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    progressDialog.setTitle("Updating Data");

                                    double price = Double.parseDouble(txt_price.getText().toString());

                                    firestore.collection("stuffs")
                                            .document(stuff_id)
                                            .update(
                                                    "title", str_txt_title,
                                                    "category_id", selected_category.getId(),
                                                    "category_name", selected_category.getName(),
                                                    "description", str_txt_description,
                                                    "price", price,
                                                    "image", imageName
                                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    progressDialog.dismiss();
                                                    makeText(EditActivity.this, "Data has been saved successfully", LENGTH_LONG).show();
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    makeText(EditActivity.this, "Error :" + e.getMessage(), LENGTH_LONG).show();
                                                }
                                            });


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    progressDialog.dismiss();
                                    makeText(EditActivity.this, "Error :" + e.getMessage(), LENGTH_LONG).show();

                                }
                            });

                }


            }
        });


    }

    void load_stuff() {
        progressDialog.show();
        firestore.collection("stuffs")
                .document(stuff_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Stuff stuff = documentSnapshot.toObject(Stuff.class);
                        txt_description.setText(stuff.getDescription());
                        txt_title.setText(stuff.getTitle());
                        txt_price.setText(String.valueOf(stuff.getPrice()));
                        for (int i = 0; i < categories.size(); i++)
                            if (stuff.getCategory_id().equals(categories.get(i).getId())) {
                                sp_category.setSelection(i);
                                break;
                            }
                        StorageReference ref = storage.getReference();
                        StorageReference image = ref.child("stuff_images/" + stuff.getImage());

                        GlideApp.with(EditActivity.this)
                                .load(image)
                                .into(stuff_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        finish();
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

                GlideApp.with(EditActivity.this)
                        .load(imageUri)
                        .into(stuff_image);

            } else if (requestCode == 100) {

                Bitmap image = (Bitmap) data.getExtras().get("data");
                //prepare image to upload
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);


                imageUri = Uri.parse(path);
                GlideApp.with(EditActivity.this)
                        .load(imageUri)
                        .into(stuff_image);

            }

        }
    }
}