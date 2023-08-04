package eu.smartpatient.mytherapy.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import eu.smartpatient.mytherapy.fragments.MedicineFragment;
import eu.smartpatient.mytherapy.fragments.TodayFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    public List<Fragment> fragments;

    public FragmentAdapter(FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
