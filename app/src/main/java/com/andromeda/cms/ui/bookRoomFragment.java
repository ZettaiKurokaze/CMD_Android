package com.andromeda.cms.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.andromeda.cms.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class bookRoomFragment extends Fragment implements DatePickerDialog.OnDateSetListener , AdapterView.OnItemSelectedListener{
    int userType=0, capacity=0;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, userRoutineID;
    private TextView dateText;
    private CheckBox ACCheckbox, ProjectorCheckbox;
    private Spinner BuildingSpinner, BoardsSpinner, TimeSpinner,RoomSpinner, RoutineSpinner;
    private EditText CapacityEdit, CourseEdit, NotesEdit;
    private recordsModel records=new recordsModel();
    ArrayList<String> Rooms;
    ArrayAdapter<String> roomAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bookroom, container, false);
        Button SearchButton, BookButton;
        RoomSpinner = root.findViewById(R.id.roomSpinner);
        Rooms=new ArrayList<>();
        roomAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Rooms);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        userID= Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();
        DocumentReference dRef=fStore.collection("User").document(userID);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    userType= Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getLong("Type")).intValue();
                    if(userType==2)
                        userRoutineID=task.getResult().getString("Batch")+"-"+task.getResult().getString("Section");
                loadRoutineID(root);
            }
        });
        dateText=root.findViewById(R.id.datePickTextView);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                if(!Rooms.isEmpty()) {
                    Rooms.clear();
                    roomAdapter.notifyDataSetChanged();
                }
            }
        });
        loadBoards(root);
        loadTimeSlots(root);
        loadBuildings(root);

        SearchButton=root.findViewById(R.id.searchRoomButton);
        BookButton=root.findViewById(R.id.bookRoomButton);
        ProjectorCheckbox=root.findViewById(R.id.projectorCheckBox);
        ACCheckbox=root.findViewById(R.id.acCheckBox);
        CapacityEdit=root.findViewById(R.id.capacityEditTextNumber);
        CourseEdit=root.findViewById(R.id.courseIDEditText);
        NotesEdit=root.findViewById(R.id.notesEditText);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CapacityEdit.getText().toString().equals(""))
                    CapacityEdit.setError("Capacity cannot be empty!");
                else if(dateText.getText().toString().equals("click to set date")) {
                    dateText.setError("");
                    Toast.makeText(getActivity(),"No date set!" , Toast.LENGTH_SHORT).show();
                }
                else {
                    dateText.setError(null);
                    capacity = Integer.parseInt(CapacityEdit.getText().toString());
                    records.setBuilding(Integer.toString(BuildingSpinner.getSelectedItemPosition()+1));
                    records.setDate(dateText.getText().toString());
                    records.setTime(Integer.toString(TimeSpinner.getSelectedItemPosition()+1));
                    records.setStartTime();
                    loadRooms(root);
                }
            }
        });

        BookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RoomSpinner.getSelectedItem()==null) {
                    BookButton.setError("");
                    Toast.makeText(getActivity(),"No room selected!" , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Calendar currentDate=Calendar.getInstance();
                    BookButton.setError(null);
                    records.setUserEmail(userID);
                    records.setCourseID(CourseEdit.getText().toString());
                    records.setNotes(NotesEdit.getText().toString());
                    records.setRoom(RoomSpinner.getSelectedItem().toString());
                    records.setRoutineID(RoutineSpinner.getSelectedItem().toString());
                    records.generateDocId();
                    records.generateDate();
                    if(currentDate.after(records.getCurrentDate()))
                        Toast.makeText(getActivity(), "Cannot create record for the past!" , Toast.LENGTH_SHORT).show();
                    else {
                        fStore.collection("Record").document(records.getDocumentId())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult().exists())
                                    Toast.makeText(getActivity(), "Room Booked!", Toast.LENGTH_SHORT).show();
                                else
                                    saveRecord();
                            }
                        });
                    }
                }
            }
        });

        BuildingSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!Rooms.isEmpty()) {
                    Rooms.clear();
                    roomAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        BoardsSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!Rooms.isEmpty()) {
                    Rooms.clear();
                    roomAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        TimeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!Rooms.isEmpty()) {
                    Rooms.clear();
                    roomAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        ACCheckbox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!Rooms.isEmpty()) {
                    Rooms.clear();
                    roomAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        ProjectorCheckbox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!Rooms.isEmpty()) {
                    Rooms.clear();
                    roomAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        CapacityEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!Rooms.isEmpty()) {
                    Rooms.clear();
                    roomAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        return root;
    }

    public void loadBuildings(View v){
        ArrayList<String> Buildings= new ArrayList<>();
        BuildingSpinner = v.findViewById(R.id.buildingNoSpinner);
        ArrayAdapter<String> buildAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Buildings);
        CollectionReference buildRef=fStore.collection("Building");
        buildRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Buildings.add(documentSnapshot.getString("Name"));
                        }
                        buildAdapter.notifyDataSetChanged();
                    }
                });
        buildAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BuildingSpinner.setAdapter(buildAdapter);
        BuildingSpinner.setOnItemSelectedListener(this);
    }

    public void loadRoutineID(View v){
        ArrayList<String>IDs= new ArrayList<>();
        RoutineSpinner = v.findViewById(R.id.routineSpinner);
        ArrayAdapter<String> routineAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, IDs);
        if(userType==2){
            IDs.add(userRoutineID);
            routineAdapter.notifyDataSetChanged();
        }
        else {
            CollectionReference routineRef = fStore.collection("Routine");
            routineRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                IDs.add(documentSnapshot.getId());
                            }
                            routineAdapter.notifyDataSetChanged();
                        }
                    });
        }
        routineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RoutineSpinner.setAdapter(routineAdapter);
        RoutineSpinner.setOnItemSelectedListener(this);
    }

    public void loadTimeSlots(View v){
        TimeSpinner = v.findViewById(R.id.timeSpinner);
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.time_slots, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TimeSpinner.setAdapter(timeAdapter);
        TimeSpinner.setOnItemSelectedListener(this);
    }

    public void loadBoards(View v){
        BoardsSpinner = v.findViewById(R.id.boardsSpinner);
        ArrayAdapter<CharSequence> boardsAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.boards, android.R.layout.simple_spinner_item);
        boardsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BoardsSpinner.setAdapter(boardsAdapter);
        BoardsSpinner.setOnItemSelectedListener(this);
    }

    public void loadRooms(View v){
        CollectionReference roomRef=fStore.collection("Room");
        roomRef.whereGreaterThanOrEqualTo("Capacity",capacity)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String docID=documentSnapshot.getId(), buildingNo=Integer.toString(BuildingSpinner.getSelectedItemPosition()+1);
                            int boards=Integer.parseInt(documentSnapshot.get("Board").toString());
                            if(ACCheckbox.isChecked() && !documentSnapshot.getBoolean("AC"))
                                continue;
                            else if(ProjectorCheckbox.isChecked() && !documentSnapshot.getBoolean("Projector"))
                                continue;
                            else if(!buildingNo.equals(docID.substring(0,1)))
                                continue;
                            else if(BoardsSpinner.getSelectedItemPosition()==0 && boards>4)
                                continue;
                            else if(BoardsSpinner.getSelectedItemPosition()==1 && (boards>7 || boards<4))
                                continue;
                            else if(BoardsSpinner.getSelectedItemPosition()==1 && boards<7)
                                continue;
                            Rooms.add(docID.substring(2,5));
                        }
                        Collections.sort(Rooms,String::compareTo);
                        roomAdapter.notifyDataSetChanged();
                    }
                });
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RoomSpinner.setAdapter(roomAdapter);
        RoomSpinner.setOnItemSelectedListener(this);
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void saveRecord() {
        Map<String, String> rec = new HashMap<>();
        rec.put("CourseID",records.getCourseID());
        rec.put("Notes",records.getNotes());
        rec.put("RoutineID",records.getRoutineID());
        rec.put("UserEmail",userID);
        fStore.collection("Record").document(records.getDocumentId()).set(rec)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Record saved", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date="";
        month++;
        if(dayOfMonth<10)
            date+="0";
        date+=dayOfMonth+"-";
        if(month<10)
            date+="0";
        date+=month
                +"-"+year;
        dateText.setText(date);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}