package eu.smartpatient.mytherapy.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import eu.smartpatient.mytherapy.R;
import eu.smartpatient.mytherapy.adapters.TodayAdapter;
import eu.smartpatient.mytherapy.database.DatabaseHelper;
import eu.smartpatient.mytherapy.models.MedicineModel;

import java.util.ArrayList;

public class TodayFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<MedicineModel> arrayList;
    @SuppressLint("StaticFieldLeak")
    TodayAdapter adapter;
    DatabaseHelper database;
    TextView tvEmptyHeading, tvEmptyText;


    public TodayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_today);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        database = new DatabaseHelper(getContext());
        arrayList = database.getAllMedicines();
        tvEmptyHeading = view.findViewById(R.id.tv_empty_heading);
        tvEmptyText = view.findViewById(R.id.tv_empty_text);
        onResume();
        adapter = new TodayAdapter(arrayList, getContext(), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void notifyDeletion(int position) {
        adapter.arrayList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (arrayList.size() == 0) {
            tvEmptyHeading.setVisibility(View.VISIBLE);
            tvEmptyText.setVisibility(View.VISIBLE);
        }
    }
}