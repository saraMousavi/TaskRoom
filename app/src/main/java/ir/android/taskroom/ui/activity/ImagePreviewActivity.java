package ir.android.taskroom.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;

import ir.android.taskroom.R;

public class ImagePreviewActivity extends AppCompatActivity {
    ImageView image ;
    ImageView imgBackForm;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        image = findViewById(R.id.imagePrev);
        imgBackForm = findViewById(R.id.imgBackForm);
        image.setImageBitmap(BitmapFactory.decodeFile(getIntent().getStringExtra("imagePath")));
        imgBackForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}