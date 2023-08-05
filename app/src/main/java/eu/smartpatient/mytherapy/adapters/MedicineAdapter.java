package eu.smartpatient.mytherapy.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eu.smartpatient.mytherapy.R;
import eu.smartpatient.mytherapy.database.DatabaseHelper;
import eu.smartpatient.mytherapy.models.MedicineModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    public ArrayList<MedicineModel> arrayList;
    Context context;
    AdapterView.OnItemClickListener onItemClickListener;
    DatabaseHelper database;

    public MedicineAdapter(ArrayList<MedicineModel> arrayList, Context context, AdapterView.OnItemClickListener onItemClickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        MedicineModel currentItem = arrayList.get(position);
        database = new DatabaseHelper(context);
        holder.tvName.setText(currentItem.getName().substring(0, 1).toUpperCase(Locale.ROOT)+currentItem.getName().substring(1).toLowerCase(Locale.ROOT));
        int hours = currentItem.getTimeHours();
        int minutes = currentItem.getTimeMinutes();
        String time = (minutes > 9) ? hours + ":" + minutes : hours + ":0" + minutes;
        time = convertTo12HourFormat(time);
        holder.tvTime.setText(time);
        holder.tvDose.setText(currentItem.getDose()+" "+currentItem.getUnit());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvDose;
        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvDose = itemView.findViewById(R.id.tv_dose);
        }
    }
    private String convertTo12HourFormat(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.US);

            Date date = inputFormat.parse(time);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
