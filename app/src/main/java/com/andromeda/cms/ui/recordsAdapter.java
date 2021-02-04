package com.andromeda.cms.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andromeda.cms.R;

import java.util.ArrayList;

public class recordsAdapter extends RecyclerView.Adapter<recordsAdapter.MyViewHolder> {

    Context context;
    ArrayList<recordsModel> records_list;
    private int pos = RecyclerView.NO_POSITION;

    public recordsAdapter(Context context, ArrayList<recordsModel> records_list){
        this.context=context;
        this.records_list=records_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.records_tablelayout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        recordsModel records=records_list.get(position);
        records.generateVariables();
        holder.course.setText(records.getCourseID());
        holder.note.setText(records.getNotes());
        holder.routine.setText(records.getRoutineID());
        holder.building.setText(records.getBuilding());
        holder.room.setText(records.getRoom());
        holder.date.setText(records.getDate());
        holder.start.setText(records.getStartTime());
        holder.end.setText(records.getEndTime());

        holder.mainLayout.setSelected(pos==position);
    }

    @Override
    public int getItemCount() {
        return records_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView building, room, date, start, end, course, routine, note;
        LinearLayout mainLayout;
        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            building=itemView.findViewById(R.id.buildingTextView);
            room=itemView.findViewById(R.id.roomTextView);
            date=itemView.findViewById(R.id.dateTextView);
            start=itemView.findViewById(R.id.startTimeTextView);
            end=itemView.findViewById(R.id.endTimeTextView);
            course=itemView.findViewById(R.id.courseTextView);
            routine=itemView.findViewById(R.id.routineTextView);
            note=itemView.findViewById(R.id.noteTextView);
            mainLayout=itemView.findViewById(R.id.item_LinearLayout);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(pos!=getLayoutPosition())
            {
                notifyItemChanged(pos);
                pos = getLayoutPosition();
                notifyItemChanged(pos);
            }
            else if(pos==getLayoutPosition())
            {
                notifyItemChanged(pos);
                pos = RecyclerView.NO_POSITION;
            }
        }
    }



    public int getPos() { return pos; }
    public void setPos(int i) {pos=i;}
}
