package com.e.toolplus;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Intent;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.e.toolplus.databinding.ActivityCreateProfileBinding;
import com.firebase.ui.auth.data.model.User;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateProfile extends AppCompatActivity {

    ActivityCreateProfileBinding binding;
    Uri imageUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateProfileBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PermissionChecker.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},11);
        }

        binding.userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(in,1);
            }
        });
        binding.btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.userName.getText().toString();
                String address = binding.userAddress.getText().toString();
                String email = binding.userEmail.getText().toString();
                String contactNumber = binding.userMobile.getText().toString();
                String token = "4324343434343432432434343434";

                if (binding.userName.toString().isEmpty()) {
                    binding.userName.setError("User name is required");
                    binding.userName.requestFocus();
                    return;
                }
                if (binding.userAddress.toString().isEmpty()) {
                    binding.userAddress.setError("Address  is required");
                    binding.userAddress.requestFocus();
                    return;
                }

                if (binding.userEmail.toString().isEmpty()) {
                    binding.userEmail.setError("Email  is required");
                    binding.userEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.userMobile.setError("Enter a valid email id");
                    binding.userMobile.requestFocus();
                    return;
                }

                if (binding.userMobile.toString().isEmpty()) {
                    binding.userMobile.setError("Mobile number  is required");
                    binding.userMobile.requestFocus();
                    return;
                }

                if(imageUri!=null){
                    File file =  FileUtils.getFile(CreateProfile.this,imageUri);
                    RequestBody requestFile =
                            RequestBody.create(
                                    MediaType.parse(getContentResolver().getType(imageUri)),
                                    file
                            );

                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                    RequestBody userName = RequestBody.create(
                            MultipartBody.FORM, name);

                    RequestBody userAddress = RequestBody.create(MultipartBody.FORM,address);

                    RequestBody useremail = RequestBody.create(MultipartBody.FORM,email);

                    RequestBody userContact = RequestBody.create(MultipartBody.FORM,contactNumber);

                    RequestBody userToken = RequestBody.create(MultipartBody.FORM,token);

                    UserService.UserApi userApi = UserService.getUserApiInstance();
                    Call<User> call = userApi.saveProfile(body,userName,userAddress,useremail,userContact,userToken);

                    call.enqueue(new Callback<User>() {

                        /*If everything will be successful this method will be called*/

                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if(response.code() == 200) {
                                User user = response.body();
                                Toast.makeText(CreateProfile.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        }

                        /*If In case of any failure this  method will be called*/


                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(CreateProfile.this, "Failed : "+t, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                    Toast.makeText(CreateProfile.this, "Please select your profile picture", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            imageUri = data.getData();
            binding.userProfile.setImageURI(imageUri);
        }
    }
}