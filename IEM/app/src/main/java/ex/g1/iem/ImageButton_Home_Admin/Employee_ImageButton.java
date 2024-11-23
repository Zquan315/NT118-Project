package ex.g1.iem.ImageButton_Home_Admin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import Adapter.EmployeeAdapter;
import java.util.ArrayList;
import java.util.List;

import Class.Employee;

import ex.g1.iem.R;

public class Employee_ImageButton extends AppCompatActivity {

    FirebaseFirestore firestore;
    DatabaseReference DBRealtime;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_image_button);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_employeeList);
        List<Employee> employeeList = new ArrayList<>();
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        DBRealtime = FirebaseDatabase.getInstance().getReference();
        // todo: load danh sách nhân viên


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EmployeeAdapter employeeAdapter = new EmployeeAdapter(employeeList);
        recyclerView.setAdapter(employeeAdapter);

        // Xử lí sự kiện khi nhấn nút back
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.employee_imagebutton), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}