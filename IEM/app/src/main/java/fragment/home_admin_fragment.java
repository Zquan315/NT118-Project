package fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import Class.Alert;
import Adapter.AlertAdapter;
import ex.g1.iem.Deep_Event.Create_Alert;
import ex.g1.iem.ImageButton_Home_Admin.Depart_ImageButton;
import ex.g1.iem.ImageButton_Home_Admin.Employee_ImageButton;
import ex.g1.iem.ImageButton_Home_Admin.Finance_ImageButton;
import ex.g1.iem.ImageButton_Home_Admin.Project_ImageButton;
import ex.g1.iem.ImageButton_Home_Admin.Resource_ImageButton;
import ex.g1.iem.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home_admin_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home_admin_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    String usernameAdmin;
    RecyclerView recyclerView;
    AlertAdapter alertAdapter;
    ArrayList<Alert> alertList;
    FirebaseFirestore firestore;
    DatabaseReference DBRealtime;
    Button clear_All_Button;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public home_admin_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_admin_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static home_admin_fragment newInstance(String param1, String param2) {
        home_admin_fragment fragment = new home_admin_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home_admin_fragment, container, false);
        assert getArguments() != null;
        usernameAdmin = getArguments().getString("username"); // username = admin
        FirebaseApp.initializeApp(this.requireActivity());
        firestore = FirebaseFirestore.getInstance();
        DBRealtime = FirebaseDatabase.getInstance().getReference();
        clear_All_Button = view.findViewById(R.id.clear_all_Button);

        //Xử lí sự kiện khi nhấn các ImageButton
        ImageButton departImageButton = view.findViewById(R.id.departImageButton);
        ImageButton employeeImageButton = view.findViewById(R.id.employeeImageButton);
        ImageButton planImageButton = view.findViewById(R.id.planImageButton);
        ImageButton resImageButton = view.findViewById(R.id.resImageButton);
        ImageButton financeImageButton = view.findViewById(R.id.financeImageButton);

        departImageButton.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), Depart_ImageButton.class);
            intent.putExtra("username", usernameAdmin);
            startActivity(intent);
        });
        employeeImageButton.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), Employee_ImageButton.class);
            intent.putExtra("username", usernameAdmin);
            startActivity(intent);
        });
        planImageButton.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), Project_ImageButton.class);
            intent.putExtra("username", usernameAdmin);
            startActivity(intent);
        });
        resImageButton.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), Resource_ImageButton.class);
            intent.putExtra("username", usernameAdmin);
            startActivity(intent);
        });
        financeImageButton.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), Finance_ImageButton.class);
            intent.putExtra("username", usernameAdmin);
            startActivity(intent);
        });

        //Xử lí sự kiện khi nhấn nút thêm thông báo
        ImageButton addAlertImageButton = view.findViewById(R.id.addAlertImageButton);
        addAlertImageButton.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), Create_Alert.class);
            startActivity(intent);
        });

        //todo:  Hiển thị các thông báo
        alertList = new ArrayList<>();

        // Truy xuất dữ liệu từ Firestore
        firestore.collection("Alert")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String id = document.getId(); // Lấy ID của document
                        if(id.equals("thongbao"))
                            continue;
                        String title = document.getString("title"); // Lấy trường title

                        if (title != null) { // Đảm bảo title không null
                            alertList.add(new Alert(id, title)); // Thêm vào danh sách
                        }
                    }
                    // Cập nhật giao diện sau khi lấy dữ liệu thành công
                    alertAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                });
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alertAdapter = new AlertAdapter(alertList);
        recyclerView.setAdapter(alertAdapter);

        //todo: Xóa tất cả thông báo
        clear_All_Button.setOnClickListener(v -> {
            try {
                if(alertList.isEmpty())
                    return;
                firestore.collection("Alert")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            List<String> idsToRemove = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String documentId = document.getId();
                                if (!documentId.equals("thongbao")) {
                                    idsToRemove.add(documentId);
                                    firestore.collection("Alert").document(documentId).delete();
                                }
                            }
                            alertList.removeIf(alert -> idsToRemove.contains(alert.getId()));
                            alertAdapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Xóa tất cả thông báo thành công", Toast.LENGTH_SHORT).show();
                        });
            } catch (Exception e) {
                Toast.makeText(getContext(), "Lỗi khi xóa thông báo", Toast.LENGTH_SHORT).show();
            }
        });


        //todo: end
        return view;
    }


}

