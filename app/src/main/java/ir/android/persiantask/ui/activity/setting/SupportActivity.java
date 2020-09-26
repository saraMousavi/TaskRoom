package ir.android.persiantask.ui.activity.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Test;

public class SupportActivity extends AppCompatActivity {

    private DatabaseReference fireBaseDataBase;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:09103009458"));
        startActivity(intent);
//        init();
    }

    //@TODO add private chat to admin
    private void init() {
        setContentView(R.layout.support_activity);
        fireBaseDataBase = FirebaseDatabase.getInstance().getReference("Message");
        key = fireBaseDataBase.push().getKey();
        TextView chatList = findViewById(R.id.chatList);
        fireBaseDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.setText(snapshot.child(key).getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                chatList.setText("canceled");
            }
        });
    }

    public void sendMessage(View view){
        EditText enterMessage = findViewById(R.id.enterMessage);
        Test test = new Test(key, "test");
        fireBaseDataBase.child(key).setValue(test);
        enterMessage.setText("");
    }
}