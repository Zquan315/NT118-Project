package ex.g1.iem.Deep_Event;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import Class.ProjectManage;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ex.g1.iem.R;

public class Create_Project extends AppCompatActivity {

    String usernameEmp;
    EditText idEditText, nameEditText, underTakeEditText,
    deadlineEditText, descriptionEditText;
    Button createButton, chooseDeadlineButton;
    ImageButton genID;
    FirebaseFirestore firestore;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_project);
        usernameEmp = getIntent().getStringExtra("username");
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        //todo: khoi tao cac doi tuong
        idEditText = findViewById(R.id.ID_Project_EditText);
        nameEditText = findViewById(R.id.name_project_EditText);
        underTakeEditText = findViewById(R.id.underTake_project_EditText);
        deadlineEditText = findViewById(R.id.deadline_EditText);
        descriptionEditText = findViewById(R.id.description_EditText);
        createButton = findViewById(R.id.Create_Project_Button);
        genID = findViewById(R.id.Generate_ID_PJ_Button);
        chooseDeadlineButton = findViewById(R.id.chooseDeadline_Button);

        //todo: load Dam nhan
        firestore.collection("Employee").document(usernameEmp).get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String underTake = task.getResult().getString("depart");
                                underTakeEditText.setText(underTake);
                            }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi", Toast.LENGTH_SHORT).show();
                });

        //todo: tao id
        genID.setOnClickListener(v -> {
            Random random = new Random();
            int randomNumber = 1000 + random.nextInt(9000);
            String id = "PJ" + String.valueOf(randomNumber);
            idEditText.setText(id);
        });

        //todo: lay dealine
        chooseDeadlineButton.setOnClickListener(v -> {
            // Lấy ngày hiện tại
            LocalDate currentDate = LocalDate.now();
            int currentYear = currentDate.getYear();
            int currentMonth = currentDate.getMonthValue() - 1;
            int currentDay = currentDate.getDayOfMonth();

            // Tạo DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        // Định dạng ngày được chọn và đặt vào EditText
                        @SuppressLint("DefaultLocale")
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        deadlineEditText.setText(selectedDate);
                    },
                    currentYear,
                    currentMonth,
                    currentDay
            );

            // Hiển thị DatePickerDialog
            datePickerDialog.show();
        });


        //todo: them du an
        createButton.setOnClickListener(v -> {
            String ID = idEditText.getText().toString();
            String name = nameEditText.getText().toString();
            String underTake = underTakeEditText.getText().toString();
            String deadline = deadlineEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            //todo: kiem tra dieu kien
            if(ID.isEmpty() || name.isEmpty() || deadline.isEmpty() || description.isEmpty())
            {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!isDate(deadline))
            {
                Toast.makeText(this, "Ngày không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            firestore.collection("Project").document(ID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                idEditText.setError("ID đã tồn tại");
                            }
                            else {
                                ProjectManage project = new ProjectManage(ID, name, underTake, description,deadline);
                                firestore.collection("Project").document(ID)
                                        .set(project)
                                        .addOnSuccessListener(aVoid ->{
                                                Toast.makeText(this, "Thêm dự án thành công",
                                                        Toast.LENGTH_SHORT).show();
                                                firestore.collection("Department").document(underTake)
                                                                .update("amount_pj", FieldValue.increment(1));
                                                idEditText.setText("");
                                                nameEditText.setText("");
                                                deadlineEditText.setText("");
                                                descriptionEditText.setText("");
                                            }
                                        )
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Thêm dự án thất bại", Toast.LENGTH_SHORT).show()
                                        );
                            }
                        }
                    });
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.create_project), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    boolean isDate(String deadline) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate inputDate = LocalDate.parse(deadline, formatter);
            return inputDate.isAfter(LocalDate.now());
        }
        catch (DateTimeException e) {
            return false;
        }
    }

}