package edu.bluejack22_1.GMoneysoLVer.activity.transaction.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import edu.bluejack22_1.GMoneysoLVer.activity.transaction.TransactionPageFragment;

public class TransactionPagerAdapter extends FragmentStateAdapter {

    public TransactionPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
//                TransactionPageFragment fragment1 = new TransactionPageFragment(11);
//                Calendar c1 = Calendar.getInstance();
//                c1.add(Calendar.MONTH, -11);
//                fragment1.getDataYear(c1);
//                return fragment1;
                return new TransactionPageFragment(11);
            case 2:
                return new TransactionPageFragment(10);
            case 3:
                return new TransactionPageFragment(9);
            case 4:
                return new TransactionPageFragment(8);
            case 5:
                return new TransactionPageFragment(7);
            case 6:
                return new TransactionPageFragment(6);
            case 7:
                return new TransactionPageFragment(5);
            case 8:
                return new TransactionPageFragment(4);
            case 9:
                return new TransactionPageFragment(3);
            case 10:
                return new TransactionPageFragment(2);
            case 11:
//                TransactionPageFragment fragment2 = new TransactionPageFragment(1);
//                Calendar c2 = Calendar.getInstance();
//                c2.add(Calendar.MONTH, -1);
//                fragment2.getDataYear(c2);
//                return fragment2;
                return new TransactionPageFragment(1);
            case 12:
                return new TransactionPageFragment(0);
        }
        return new TransactionPageFragment(12);
    }

    @Override
    public int getItemCount() {
        return 13;
    }
}
