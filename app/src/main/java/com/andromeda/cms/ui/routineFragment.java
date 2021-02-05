package com.andromeda.cms.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.andromeda.cms.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class routineFragment extends Fragment {

    private final FirebaseAuth fAuth=FirebaseAuth.getInstance();
    private final FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    private String userID= Objects.requireNonNull(fAuth.getCurrentUser()).getEmail(), userRoutineID;
    private routineModel routine;
    private TextView dayOfWeek;
    private ArrayList<recordsModel> records_list;
    private Calendar selected=Calendar.getInstance();
    TextView[] CourseSlot = new TextView[8];

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_routine, container, false);
        CalendarView calendarView=root.findViewById(R.id.routineCalendarView);
        dayOfWeek=root.findViewById(R.id.dayOfWeekTextView);
        for(int i = 0; i < 8; i++) {
            CourseSlot[i] = new TextView(getContext());
        }
        CourseSlot[0]=root.findViewById(R.id.courseShowTextView1);
        CourseSlot[1]=root.findViewById(R.id.courseShowTextView2);
        CourseSlot[2]=root.findViewById(R.id.courseShowTextView3);
        CourseSlot[3]=root.findViewById(R.id.courseShowTextView4);
        CourseSlot[4]=root.findViewById(R.id.courseShowTextView5);
        CourseSlot[5]=root.findViewById(R.id.courseShowTextView6);
        CourseSlot[6]=root.findViewById(R.id.courseShowTextView7);
        CourseSlot[7]=root.findViewById(R.id.courseShowTextView8);
        dayOfWeek.setText(getDayOfWeek(selected.get(Calendar.DAY_OF_WEEK)));

        fStore.collection("User").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userRoutineID=task.getResult().getString("Batch")+"-"+task.getResult().getString("Section");
                loadRoutine(root);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selected.set(year,month,dayOfMonth);
                dayOfWeek.setText(getDayOfWeek(selected.get(Calendar.DAY_OF_WEEK)));
                setRoutine();
                setRoutineRecord();
            }
        });

        return root;
    }

    public void loadRoutine(View v){
        fStore.collection("Routine").document(userRoutineID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, String> Monday= (Map<String, String>) task.getResult().get("Monday"),
                        Tuesday= (Map<String, String>) task.getResult().get("Tuesday"),
                        Wednesday= (Map<String, String>) task.getResult().get("Wednesday"),
                        Thursday= (Map<String, String>) task.getResult().get("Thursday"),
                        Friday= (Map<String, String>) task.getResult().get("Friday");
                routine=new routineModel(Friday,Monday,Thursday,Tuesday,Wednesday);
                setRoutine();
            }
        });
        records_list=new ArrayList<>();
        fStore.collection("Records").whereEqualTo("RoutineID",userRoutineID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            recordsModel records = documentSnapshot.toObject(recordsModel.class);
                            records.setDocumentId(documentSnapshot.getId());
                            records.generateVariables();
                            records.generateDate();
                            records_list.add(records);
                        }
                        //Toast.makeText(getActivity(), "Success! "+queryDocumentSnapshots.size(), Toast.LENGTH_SHORT).show();
                        setRoutineRecord();
                    }
                });

    }

    public void setRoutine() {
        Map<String, String> day=new HashMap<>();
        if(dayOfWeek.getText().toString().equals("Monday"))
            day=routine.getMon();
        else if(dayOfWeek.getText().toString().equals("Tuesday"))
            day=routine.getTue();
        else if(dayOfWeek.getText().toString().equals("Wednesday"))
            day=routine.getWed();
        else if(dayOfWeek.getText().toString().equals("Thursday"))
            day=routine.getThu();
        else if(dayOfWeek.getText().toString().equals("Friday"))
            day=routine.getFri();
        int i=0;
        for (Map.Entry<String, String> mapElement : day.entrySet()) {
            CourseSlot[i].setText((String)mapElement.getValue());
            i++;
        }

    }

    public void setRoutineRecord(){
        for(recordsModel r : records_list){
            if(r.getCurrentDate().get(Calendar.YEAR)==selected.get(Calendar.YEAR) &&
                    r.getCurrentDate().get(Calendar.MONTH)==selected.get(Calendar.MONTH) &&
                    r.getCurrentDate().get(Calendar.DAY_OF_MONTH)==selected.get(Calendar.DAY_OF_MONTH)){
                int i=Integer.parseInt(r.getTime())-1;
                CourseSlot[i].setText(r.getCourseID());
            }
        }
    }

    public String getDayOfWeek(int i){
        switch (i){
            case 1:
                return("Sunday");
            case 2:
                return("Monday");
            case 3:
                return("Tuesday");
            case 4:
                return("Wednesday");
            case 5:
                return("Thursday");
            case 6:
                return("Friday");
            case 7:
                return("Saturday");
            default:
                return("");
        }
    }
}