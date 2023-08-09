package eu.smartpatient.mytherapy.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import eu.smartpatient.mytherapy.R;
import eu.smartpatient.mytherapy.UpdateMedicineActivity;
import eu.smartpatient.mytherapy.adapters.FragmentAdapter;
import eu.smartpatient.mytherapy.adapters.MedicineAdapter;
import eu.smartpatient.mytherapy.database.DatabaseHelper;
import eu.smartpatient.mytherapy.models.MedicineModel;
import eu.smartpatient.mytherapy.services.AlarmForegroundService;
import eu.smartpatient.mytherapy.swipe.SwipeHelper;

public class MedicineFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<MedicineModel> arrayList;
    @SuppressLint("StaticFieldLeak")
    static MedicineAdapter adapter;
    DatabaseHelper database;
    TextView tvEmptyHeading, tvEmptyText;

    public MedicineFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medicine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_medicines);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        database = new DatabaseHelper(getContext());
        arrayList = database.getAllMedicines();
        if (arrayList.size() == 0) {
            tvEmptyHeading = view.findViewById(R.id.tv_empty_heading);
            tvEmptyHeading.setVisibility(View.VISIBLE);
            tvEmptyText = view.findViewById(R.id.tv_empty_text);
            tvEmptyText.setVisibility(View.VISIBLE);
        }
        adapter = new MedicineAdapter(arrayList, getContext(), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MedicineModel medicineModel = arrayList.get(position);
                Intent intent = new Intent(requireContext(), UpdateMedicineActivity.class);
                intent.putExtra("id", medicineModel.getId());
                intent.putExtra("name", medicineModel.getName());
                intent.putExtra("unit", medicineModel.getUnit());
                intent.putExtra("time_hours", String.valueOf(medicineModel.getTimeHours()));
                intent.putExtra("time_minutes", String.valueOf(medicineModel.getTimeMinutes()));
                intent.putExtra("dose", String.valueOf(medicineModel.getDose()));
                intent.putExtra("isSkipped", medicineModel.getIsSkipped());
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                if (getActivity() != null && !getActivity().isFinishing()) {
                    getActivity().finish();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        SwipeHelper swipeHelper = new SwipeHelper(requireContext(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new UnderlayButton(R.drawable.baseline_delete_outline_24, R.color.red, new UnderlayButtonClickListener() {
                    @Override
                    public void onClick(int pos) {
                        int position = viewHolder.getAdapterPosition();
                        MedicineModel currentItem = arrayList.get(position);
                        arrayList.remove(position);
                        if (arrayList.size() == 0) {
                            tvEmptyHeading = view.findViewById(R.id.tv_empty_heading);
                            tvEmptyHeading.setVisibility(View.VISIBLE);
                            tvEmptyText = view.findViewById(R.id.tv_empty_text);
                            tvEmptyText.setVisibility(View.VISIBLE);
                        }
                        database.deleteMedicine(currentItem.getId());
                        TodayFragment todayFragment = (TodayFragment) getParentFragmentManager().getFragments().get(1);
                        if (todayFragment != null)
                            todayFragment.notifyDeletion(position);
                        else
                            Toast.makeText(getContext(), "NULL", Toast.LENGTH_SHORT).show();
                        Intent serviceIntent = new Intent(getContext(), AlarmForegroundService.class);
                        ContextCompat.startForegroundService(getContext(), serviceIntent);
                        adapter.notifyItemRemoved(position);
                    }
                }));
            }
        };
    }
}