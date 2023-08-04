package eu.smartpatient.mytherapy;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import eu.smartpatient.mytherapy.adapters.FragmentAdapter;
import eu.smartpatient.mytherapy.fragments.MedicineFragment;
import eu.smartpatient.mytherapy.fragments.TodayFragment;
import eu.smartpatient.mytherapy.services.AlarmForegroundService;

public class RemindersActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    Button fab;
    ImageView imgAccount;
    ViewPager2 pager;
    FragmentAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.viewpager);
        imgAccount = findViewById(R.id.opt_account);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.baseline_more_vert_24));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RemindersActivity.this, AddMedicineActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
        imgAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RemindersActivity.this, UserAccountActivity.class);
                startActivity(intent);
            }
        });
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MedicineFragment());
        fragments.add(new TodayFragment());
        adapter = new FragmentAdapter(this, fragments);
        pager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0){
                    tab.setText("Medicines");
                }
                else {
                    tab.setText("Today");
                }
            }
        }).attach();
        pager.setCurrentItem(1);
        pager.setCurrentItem(0);
        Intent serviceIntent = new Intent(this, AlarmForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (user == null) {
            editor.putString("user_name", "guest");
            editor.apply();
        }
    }
}