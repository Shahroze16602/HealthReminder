package eu.smartpatient.mytherapy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import eu.smartpatient.mytherapy.database.DatabaseHelper;
import eu.smartpatient.mytherapy.models.MedicineModel;
import eu.smartpatient.mytherapy.services.AlarmForegroundService;

public class UpdateMedicineActivity extends AppCompatActivity {
    Spinner unitSpinner;
    String name, unit, isSkipped;
    EditText edtName;
    Button btnTime, btnDose, btnBack, btnSave;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    int id, hours, minutes, noOfUnits;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_medicine);
        Intent intent = getIntent();
        id = Integer.parseInt(intent.getStringExtra("id"));
        name = intent.getStringExtra("name");
        unit = intent.getStringExtra("unit");
        hours = Integer.parseInt(intent.getStringExtra("time_hours"));
        minutes = Integer.parseInt(intent.getStringExtra("time_minutes"));
        noOfUnits = Integer.parseInt(intent.getStringExtra("dose"));
        isSkipped = intent.getStringExtra("isSkipped");
        unitSpinner = findViewById(R.id.unit_spinner);
        edtName = findViewById(R.id.edt_name);
        edtName.setText(name);
        btnTime = findViewById(R.id.btn_time);
        btnDose = findViewById(R.id.btn_dose);
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        String time = (minutes > 9) ? hours + ":" + minutes : hours + ":0" + minutes;
        time = convertTo12HourFormat(time);
        btnTime.setText(time);
        btnDose.setText(noOfUnits + " " + unit);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);
        unitSpinner.setSelection(adapter.getPosition(unit));
        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                unit = adapterView.getItemAtPosition(i).toString();
                btnDose.setText(noOfUnits + " " + unit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeDialog();
            }
        });
        btnDose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDoseDialog();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtName.getText().toString().trim().isEmpty()) {
                    MedicineModel medicineModel = new MedicineModel(edtName.getText().toString().trim(), unit, hours, minutes, noOfUnits, isSkipped);
                    medicineModel.setId(String.valueOf(id));
                    databaseHelper.updateMedicine(String.valueOf(id), medicineModel);
                    Intent serviceIntent = new Intent(UpdateMedicineActivity.this, AlarmForegroundService.class);
                    ContextCompat.startForegroundService(UpdateMedicineActivity.this, serviceIntent);
                    onBackPressed();
                } else {
                    Toast.makeText(UpdateMedicineActivity.this, "Name is required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void openTimeDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                UpdateMedicineActivity.this.hours = hours;
                UpdateMedicineActivity.this.minutes = minutes;
                String time = (minutes > 9) ? hours + ":" + minutes : hours + ":0" + minutes;
                time = convertTo12HourFormat(time);
                btnTime.setText(time);
            }
        }, hours, minutes, false);
        dialog.show();
    }

    public void openDoseDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dose_dialog_layout);
        Button btnCancel, btnOk;
        EditText edtDose;
        edtDose = dialog.findViewById(R.id.edt_dose);
        edtDose.setText(String.valueOf(noOfUnits));
        edtDose.setHint(unit);
        btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (!edtDose.getText().toString().isEmpty()) {
                    noOfUnits = Integer.parseInt(edtDose.getText().toString());
                    if (noOfUnits > 0) {
                        btnDose.setText(noOfUnits + " " + unit);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(UpdateMedicineActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdateMedicineActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UpdateMedicineActivity.this, RemindersActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
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