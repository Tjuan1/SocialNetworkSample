package com.example.instagramclone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class SocialMediaActivity extends AppCompatActivity {


    private TabLayout mTabLayout;
    private ViewPager2 mViewPager2;
    private String[] tabText = { "Profile", "Users", "Pictures"};
    private Toolbar mToolbar;
    Bitmap receivedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        mTabLayout= findViewById(R.id.tabs);
        mViewPager2 = findViewById(R.id.view_pager);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.my_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.postImageItem) {
                    if(Build.VERSION.SDK_INT >= 23 &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        mPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    } else {
                        captureImage();
                    }
                } else if (item.getItemId() == R.id.logoutUserItem) {
                    ParseUser.getCurrentUser().logOut();
                    finish();
                    Intent intent = new Intent(SocialMediaActivity.this,SignUp.class);
                    startActivity(intent);
                }
                    return false;

            }

        });

        TabAdapter adapter = new TabAdapter(this);
        mViewPager2.setAdapter(adapter);

        new TabLayoutMediator(mTabLayout, mViewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabText[position]);
            }
        }).attach();

    }

    private void captureImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        mActivityResultLauncher.launch(intent);

    }


    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if(result) {
                        captureImage();
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
                            Uri capturedImage = result.getData().getData();
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(getContentResolver(), capturedImage);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream);
                            byte[] bytes = byteArrayOutputStream.toByteArray();

                            ParseFile parseFile = new ParseFile("img.png", bytes);
                            ParseObject parseObject = new ParseObject("Photo");
                            parseObject.put("picture", parseFile);
                            parseObject.put("username", ParseUser.getCurrentUser().getUsername());

                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        Toast.makeText(SocialMediaActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SocialMediaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

}
