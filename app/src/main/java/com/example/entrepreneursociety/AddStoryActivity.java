package com.example.entrepreneursociety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class AddStoryActivity extends AppCompatActivity {

    private Uri eImageUri;
    String Url = "";
    private StorageTask storageTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        storageReference = FirebaseStorage.getInstance().getReference("story");

        CropImage.activity().setAspectRatio(9,16).start(AddStoryActivity.this);

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void publishStory(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting!");
        progressDialog.show();

        if (eImageUri != null){
            StorageReference imageRef = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(eImageUri));

            storageTask = imageRef.putFile(eImageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        Url = downloadUri.toString();

                        String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(myid);

                        String storyId = reference.push().getKey();
                        long timeEnd = System.currentTimeMillis() + 86400000; // 24 hours

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl",Url);
                        hashMap.put("timestart", ServerValue.TIMESTAMP);
                        hashMap.put("timeend",timeEnd);
                        hashMap.put("storyid",storyId);
                        hashMap.put("userid",myid);

                        reference.child(storyId).setValue(hashMap);
                        progressDialog.dismiss();

                        finish();

                    }else {
                        Toast.makeText(AddStoryActivity.this,"Please Try Again!",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddStoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No Image Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            eImageUri = result.getUri();
            publishStory();
        } else {
            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddStoryActivity.this,ProfileActivity.class));
            finish();
        }
    }
}