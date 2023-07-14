package vn.tika.fitchat.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.tika.fitchat.EditProfileActivity;
import vn.tika.fitchat.Model.User;
import vn.tika.fitchat.R;

public class ProfileFragment extends Fragment {
    View viewProfile;

    CircleImageView civAvatar;
    TextView txtName, txtNickName, txtStudentCode, txtClassName;
    TextView txtAddress, txtSpecialized, txtEmail;
    ImageButton imbEditProfile;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUrl;
    private StorageTask upLoadTask;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewProfile = inflater.inflate(R.layout.fragment_profile, container, false);
        addControls();
        addEvents();
        return viewProfile;
    }

    private void addControls() {
        civAvatar = viewProfile.findViewById(R.id.civAvatarProfile);
        txtName = viewProfile.findViewById(R.id.txtName);
        txtNickName = viewProfile.findViewById(R.id.txtNickName);
        txtStudentCode = viewProfile.findViewById(R.id.txtStudentCode);
        txtClassName = viewProfile.findViewById(R.id.txtClassName);
        txtAddress = viewProfile.findViewById(R.id.txtAddress);
        txtSpecialized = viewProfile.findViewById(R.id.txtSpecialized);
        imbEditProfile = viewProfile.findViewById(R.id.imbEditProfile);
        txtEmail = viewProfile.findViewById(R.id.txtEmail);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
    }
    private void addEvents() {
        showInfor();// hien thông tin cua tài khoan
        setAvatarImage();
        // chuyen sang màn hình edit profile
        startEditProfileActivity();
    }

    private void startEditProfileActivity(){
        imbEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });
    }
    private void showInfor(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String email = firebaseUser.getEmail();
                String studentCode = email.substring(0,email.indexOf("@"));
                txtName.setText(user.getUserName());
                txtNickName.setText("@"+user.getNickName());
                txtAddress.setText("Address: "+user.getAddress());
                txtClassName.setText("Classname: "+user.getClassName());
                txtStudentCode.setText("Student code: "+studentCode);
                txtSpecialized.setText("Specialized: "+user.getSpecialized());
                txtEmail.setText("Email: "+email);
                if(user.getAvatar().equals("default")){
                    civAvatar.setImageResource(R.drawable.img_avatar_default);
                }else{
                    Glide.with(getContext()).load(user.getAvatar()).into(civAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setAvatarImage(){
        civAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUrl != null){
            final  StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUrl));

            upLoadTask = fileReference.putFile(imageUrl);
            upLoadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("avatar", ""+mUri);
                        databaseReference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUrl = data.getData();

            if (upLoadTask != null && upLoadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}
