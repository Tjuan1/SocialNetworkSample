package com.example.instagramclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UsersPosts extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_posts);


        mProgressBar = findViewById(R.id.progressBarUsersPosts);
        mLinearLayout = findViewById(R.id.linearLayout);

        Intent receivedIntentObject = getIntent();
        String receivedUserName = receivedIntentObject.getStringExtra("username");

//        create a query for the class Photo in our db
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Photo");
        parseQuery.whereEqualTo("username", receivedUserName);
        parseQuery.orderByDescending("createdAt");

        mProgressBar.setVisibility(View.VISIBLE);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject post : objects) {
                            TextView imageDesc = new TextView(UsersPosts.this);
                            if (post.get("image_des") != null) {
                            imageDesc.setText(post.get("image_des") + "");
                            } else {
                                imageDesc.setText("No description has been provided");
                            }
                            ParseFile postPicture = (ParseFile) post.get("picture");
                            postPicture.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if( data != null && e == null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        ImageView postImageView = new ImageView(UsersPosts.this);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                       params.setMargins(16,16,16,16);
                                       postImageView.setLayoutParams(params);
                                       postImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                       postImageView.setImageBitmap(bitmap);

                                        LinearLayout.LayoutParams desc_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                        desc_params.setMargins(16,16,16,16);
                                        imageDesc.setLayoutParams(desc_params);
                                        imageDesc.setGravity(Gravity.CENTER);
                                        imageDesc.setTextColor(Color.BLACK);
                                        imageDesc.setTextSize(30f);

                                        mLinearLayout.addView(postImageView);
                                        mLinearLayout.addView(imageDesc);

                                    }
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                }

                            });
                        }
                    } else {
                        Toast.makeText(UsersPosts.this, "This user doesn´t have any posts", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UsersPosts.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}