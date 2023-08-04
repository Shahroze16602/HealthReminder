package eu.smartpatient.mytherapy;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import eu.smartpatient.mytherapy.database.DatabaseHelper;
import eu.smartpatient.mytherapy.models.MedicineModel;
import eu.smartpatient.mytherapy.services.AlarmForegroundService;

public class AddMedicineActivity extends AppCompatActivity {
    Spinner unitSpinner;
    String unit = "Tablet(s)";
    EditText edtName;
    Button btnTime, btnDose, btnBack, btnSave;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    int hours = 8, minutes = 0, noOfUnits = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        unitSpinner = findViewById(R.id.unit_spinner);
        edtName = findViewById(R.id.edt_name);
        btnTime = findViewById(R.id.btn_time);
        btnDose = findViewById(R.id.btn_dose);
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        if (minutes > 9)
            btnTime.setText(hours + ":" + minutes);
        else
            btnTime.setText(hours + ":0" + minutes);
        btnDose.setText(noOfUnits + " " + unit);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);
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
                    MedicineModel medicineModel = new MedicineModel(edtName.getText().toString().trim(), unit, hours, minutes, noOfUnits, "false");
                    medicineModel.setId(String.valueOf(databaseHelper.insertMedicine(medicineModel)));
                    Intent serviceIntent = new Intent(AddMedicineActivity.this, AlarmForegroundService.class);
                    ContextCompat.startForegroundService(AddMedicineActivity.this, serviceIntent);
                    Toast.makeText(AddMedicineActivity.this, "Reminder set successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(AddMedicineActivity.this, "Name is required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void openTimeDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                AddMedicineActivity.this.hours = hours;
                AddMedicineActivity.this.minutes = minutes;
                if (minutes > 9)
                    btnTime.setText(hours + ":" + minutes);
                else
                    btnTime.setText(hours + ":0" + minutes);
            }
        }, hours, minutes, true);
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
                        Toast.makeText(AddMedicineActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddMedicineActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddMedicineActivity.this, RemindersActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }
}