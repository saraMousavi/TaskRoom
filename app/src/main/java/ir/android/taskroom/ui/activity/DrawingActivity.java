package ir.android.taskroom.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Attachments;
import ir.android.taskroom.utils.DrawingView;

public class DrawingActivity extends AppCompatActivity {
    private ImageButton currPaint;
    private DrawingView drawingView;
    private float smallBrush, mediumBrush, largeBrush;
    private ImageButton drawSave, drawBrush, drawErase, drawNew;
    private SeekBar brushSize;
    private int brushSizeVal = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        onClickEvents();
    }

    private void onClickEvents() {
        drawSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().toString() + "/TaskRoom";
                File directory = new File(path);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File file = new File(path, "draw" + new Date().getTime() + ".png");
                OutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                drawingView.setDrawingCacheEnabled(true);
                Bitmap bitmap = drawingView.getDrawingCache();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra("drawPath", file.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        drawBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(brushSize.getVisibility() == View.VISIBLE){
                    brushSize.setVisibility(View.INVISIBLE);
                } else {
                    brushSize.setVisibility(View.VISIBLE);
                }

                drawingView.setErase(false);
            }
        });
        drawErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setErase(true);
                drawingView.setPaintColor(R.color.white);
                drawingView.setStrokeWidth(brushSizeVal);
            }
        });
        drawNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.startNew();
            }
        });
        brushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushSizeVal = progress;
                drawingView.setStrokeWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void init() {
        setContentView(R.layout.activity_drawing);
        LinearLayout colorPalette = findViewById(R.id.colorPalette);
        LinearLayout firstRow = (LinearLayout) colorPalette.getChildAt(0);
        currPaint = (ImageButton) firstRow.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        drawingView = findViewById(R.id.drawingView);
        drawSave = findViewById(R.id.drawSave);
        drawBrush = findViewById(R.id.drawBrush);
        drawErase = findViewById(R.id.drawErase);
        drawNew = findViewById(R.id.drawNew);
        brushSize = findViewById(R.id.brushSize);
        smallBrush = getResources().getInteger(R.integer.small_brush);
        mediumBrush = getResources().getInteger(R.integer.mediumBrush);
        largeBrush = getResources().getInteger(R.integer.largeBrush);
    }

    public void paintClicked(View view) {
        if (view != currPaint) {
            drawingView.setErase(false);
            ImageButton imgView = (ImageButton) view;
            String color = imgView.getTag().toString();
            drawingView.setColor(color);

            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) view;
        }

    }
}