package edu.bluejack22_1.GMoneysoLVer.activity.transaction.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import edu.bluejack22_1.GMoneysoLVer.activity.transaction.CategoryExpenseFragment;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.CategoryIncomeFragment;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    public CategoryPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new CategoryIncomeFragment();
        }
        return new CategoryExpenseFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
