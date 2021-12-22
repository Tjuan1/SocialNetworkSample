package com.example.instagramclone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;


public class SharedPictureTab extends Fragment {

    private ImageView imageShare;
    private EditText imageShareDescription;
    private Button mButton;
    private ProgressBar mProgressBar;

    Bitmap receivedImageBitmap;

    public SharedPictureTab() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shared_picture_tab, container, false);
        imageShare = view.findViewById(R.id.imgShare);
        imageShareDescription = view.findViewById(R.id.edtImageDescription);
        mButton = view.findViewById(R.id.btnImageShare);
        mProgressBar = view.findViewById(R.id.progressBar);

        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23 &&
                        ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                    mPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    getChosenImage();
                }
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(receivedImageBitmap != null) {
                    if(imageShareDescription.getText().toString() != null) {
//                        we convert the image to an array of bytes so that it can be uploaded
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        receivedImageBitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        ParseFile parseFile = new ParseFile("img.png", bytes);
                        ParseObject parseObject = new ParseObject("Photo");
                        parseObject.put("picture", parseFile);
                        parseObject.put("image_des", imageShareDescription.getText().toString());
                        parseObject.put("username", ParseUser.getCurrentUser().getUsername());
                        mProgressBar.setVisibility(View.VISIBLE);

                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.i("log-", "Saving Image");
                                if (e == null) {
                                    Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Please, provide some description", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please, select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void getChosenImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.putExtra("imageUri", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        mActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if(result) {
                        getChosenImage();
                    } else {
                        Log.e("TAG", "onActivityResult: PERMISSION DENIED");
                    }
                }
            });

    ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            Uri selectedImage = result.getData().getData();
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null);
//                            move to the first obect
                            cursor.moveToFirst();
//                            the array has only one value. We select it
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                            this gets the path of our image. THen we don´t need the cursor anymore
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();
//                            we decode and convert the image and convert it to a bitmap
                            receivedImageBitmap = BitmapFactory.decodeFile(picturePath);

                            imageShare.setImageBitmap(receivedImageBitmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


}