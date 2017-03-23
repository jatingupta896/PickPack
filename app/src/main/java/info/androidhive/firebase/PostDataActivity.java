package info.androidhive.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

/**
 * Created by new on 28-Jan-17.
 */

public class PostDataActivity extends AppCompatActivity implements View.OnClickListener /*  implementing click listener */ {
    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;
    final String resName = null;
    //Buttons
    private Button buttonChoose;
    private Button submitButton;
    private EditText title, description;

    //ImageView
    private ImageView imageView;

    //a Uri object to store file path
    private Uri filePath;

    //  private ImageView selectImage;
    private static final int GALLERY_REQUEST = 1;
    private StorageReference storageReference;
    private DatabaseReference databaseReference, databaseUser;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseUser = FirebaseDatabase.getInstance().getReference().child("User").child(currentUser.getUid());
        //getting views from layout
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        submitButton = (Button) findViewById(R.id.submitButton);
        title = (EditText) findViewById(R.id.editText1);
        description = (EditText) findViewById(R.id.editText2);

        imageView = (ImageView) findViewById(R.id.imageSelect);

        //attaching listener
        buttonChoose.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                final String resName = getResources().getResourceEntryName(R.id.imageSelect);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        //if the clicked button is choose
        if (view == buttonChoose) {
            showFileChooser();
        }
        //if the clicked button is upload
        else if (view == submitButton) {
            uploadFile();

        }
    }

    private void uploadFile() {
        //if there is a file to upload

        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(PostDataActivity.this, R.style.MyTheme);
            progressDialog.setTitle("Uploading");
            final String title_val = title.getText().toString().trim();
            final String desc_val = description.getText().toString().trim();

            if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val)) {
                progressDialog.show();
                StorageReference riversRef = storageReference.child("Blog_Image").child(filePath.getLastPathSegment());
                riversRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //if the upload is successfull
                                //hiding the progress dialog
                                final DatabaseReference newPost = databaseReference.push();
                                final Uri downloadUri = taskSnapshot.getDownloadUrl();

                                databaseUser.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        newPost.child("title").setValue(title_val);
                                        newPost.child("desc").setValue(desc_val);
                                        newPost.child("image").setValue(downloadUri.toString());
                                        newPost.child("uid").setValue(currentUser.getUid());
                                  /*  Map<String, String> time=ServerValue.TIMESTAMP;
                                    long time1=Long.parseLong(time);
                                    time1=time1*1000L;
                                    Date date=new Date(time1);
                                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MM yyyy, HH:mm");
                                    String date1=simpleDateFormat.format(date);
                                   // DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                     // this will create a new unique key
                                   // value = new HashMap<>();*/
                                        //Calendar calendar=Calendar.getInstance();
                                        //SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.ENGLISH);
                                        //String date=sdf.format(calendar.getTime());

                                        newPost.child("time").setValue(System.currentTimeMillis());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                progressDialog.dismiss();

                                //and displaying a success toast
                                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(PostDataActivity.this, Web.class));
                                finish();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                //and displaying error message
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                //displaying percentage in progress dialog
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Title and Description are empty..Please Fill It", Toast.LENGTH_LONG).show();
            }
        }
        //if there is not any file
        else {
            //you can display an error toast
            Toast.makeText(getApplicationContext(), "Choose a Photo", Toast.LENGTH_LONG).show();
        }


    }


}
