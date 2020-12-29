package ir.android.taskroom.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;

import ir.android.taskroom.R;
import ir.android.taskroom.ui.activity.setting.AboutAppActivity;
import ir.android.taskroom.ui.dialog.LanguageDialog;

public class LanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        LanguageDialog languageDialog = new LanguageDialog(LanguageActivity.this);
        languageDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        languageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        languageDialog.show();
    }
}