package ex.g1.iem.Deep_Event;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import ex.g1.iem.R;

public class Info_Alert extends AppCompatActivity {

    String AlertID;
    TextView titleAlert_TextView, idAlert_TextView, contentAlert_TextView, time_TextView;
    FirebaseFirestore firestore;
    DatabaseReference DBRealtime;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_alert);
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        DBRealtime = FirebaseDatabase.getInstance().getReference();
        AlertID = getIntent().getStringExtra("alertID");
        titleAlert_TextView = findViewById(R.id.titleAlert_TextView);
        idAlert_TextView = findViewById(R.id.idAlert_TextView);
        contentAlert_TextView = findViewById(R.id.contentAlert_TextView);
        time_TextView = findViewById(R.id.time_TextView);
        //todo: load dữ liệu
        idAlert_TextView.setText(AlertID);


        //todo: back button
        findViewById(R.id.backButton).setOnClickListener(v -> {finish();});
        //todo: end

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.info_alert), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}