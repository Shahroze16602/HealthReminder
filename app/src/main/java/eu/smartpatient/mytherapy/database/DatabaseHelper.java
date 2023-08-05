package eu.smartpatient.mytherapy.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import eu.smartpatient.mytherapy.models.MedicineModel;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="MedicineReminderDB";
    public static final String MEDICINE_TABLE="medicine_tbl";
    private final String[] med_col=new String[]{"id","med_name","med_unit","time_hours","time_minutes","med_dose","skipped"};


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE medicine_tbl(id INTEGER PRIMARY KEY AUTOINCREMENT, med_name TEXT, med_unit TEXT, time_hours INTEGER, time_minutes INTEGER, med_dose INTEGER, skipped TEXT)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertMedicine(MedicineModel medicineModel) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("med_name", medicineModel.getName());
        contentValues.put("med_unit", medicineModel.getUnit());
        contentValues.put("time_hours", medicineModel.getTimeHours());
        contentValues.put("time_minutes", medicineModel.getTimeMinutes());
        contentValues.put("med_dose", medicineModel.getDose());
        contentValues.put("skipped", medicineModel.getIsSkipped());
        return sqLiteDatabase.insert(MEDICINE_TABLE, null, contentValues);
    }

    @SuppressLint("Range")
    public ArrayList<MedicineModel> getAllMedicines(){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor=sqLiteDatabase.query(MEDICINE_TABLE, med_col,null,null,null,null,"time_hours ASC, time_minutes ASC");
        ArrayList<MedicineModel> arrayList=new ArrayList<>();
        while (cursor.moveToNext()){
            MedicineModel medicineModel=new MedicineModel();
            medicineModel.setId(cursor.getString(cursor.getColumnIndex("id")));
            medicineModel.setName(cursor.getString(cursor.getColumnIndex("med_name")));
            medicineModel.setUnit(cursor.getString(cursor.getColumnIndex("med_unit")));
            medicineModel.setTimeHours(cursor.getInt(cursor.getColumnIndex("time_hours")));
            medicineModel.setTimeMinutes(cursor.getInt(cursor.getColumnIndex("time_minutes")));
            medicineModel.setDose(cursor.getInt(cursor.getColumnIndex("med_dose")));
            medicineModel.setIsSkipped(cursor.getString(cursor.getColumnIndex("skipped")));
            arrayList.add(medicineModel);
        }
        return arrayList;
    }
    public int updateMedicine(String id, MedicineModel medicineModel) {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("med_name", medicineModel.getName());
        contentValues.put("med_unit", medicineModel.getUnit());
        contentValues.put("time_hours", medicineModel.getTimeHours());
        contentValues.put("time_minutes", medicineModel.getTimeMinutes());
        contentValues.put("med_dose", medicineModel.getDose());
        contentValues.put("skipped", medicineModel.getIsSkipped());
        return sqLiteDatabase.update(MEDICINE_TABLE, contentValues, "id=?", new String[]{id});
    }
    public void deleteMedicine(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(MEDICINE_TABLE, "id=?", new String[]{id});
    }
    public void deleteAllMedicine(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(MEDICINE_TABLE, null, null);
    }
    public int skipMedicine(String id, String isSkipped){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("skipped", isSkipped);
        return sqLiteDatabase.update(MEDICINE_TABLE, contentValues, "id=?", new String[]{id});
    }
    @SuppressLint("Range")
    public String getIsSkipped(String id) {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor=sqLiteDatabase.query(MEDICINE_TABLE, med_col,"id=?", new String[]{id},null,null,null);
        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndex("skipped"));
    }
}
