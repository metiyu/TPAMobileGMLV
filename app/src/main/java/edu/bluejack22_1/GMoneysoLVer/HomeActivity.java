package edu.bluejack22_1.GMoneysoLVer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import edu.bluejack22_1.GMoneysoLVer.activity.bill.BillsFragment;
import edu.bluejack22_1.GMoneysoLVer.activity.budget.BudgetFragment;
import edu.bluejack22_1.GMoneysoLVer.activity.category.CategoriesFragment;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.AddTransactionActivity;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.TransactionFragment;
import edu.bluejack22_1.GMoneysoLVer.activity.wallet.WalletsFragment;
import edu.bluejack22_1.GMoneysoLVer.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String fragmentToGo = extras.getString("fragmentToGo");
            if(fragmentToGo != null){
                if(fragmentToGo.equals("category")){
                    replaceFragment(new CategoriesFragment());
                }
                else if(fragmentToGo.equals("profile")){
                    replaceFragment(new ProfileFragment());
                }
                else if(fragmentToGo.equals("home")){
                    replaceFragment(new HomeFragment());
                }
                else if(fragmentToGo.equals("plan")){
                    replaceFragment(new PlanningFragment());
                }
                else if(fragmentToGo.equals("wallet")){
                    replaceFragment(new WalletsFragment());
                }
                else if(fragmentToGo.equals("bill")){
                    replaceFragment(new BillsFragment());
                }
                else if(fragmentToGo.equals("budget")){
                    replaceFragment(new BudgetFragment());
                }
            }
        }
        else{
            replaceFragment(new HomeFragment());
        }
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if(currentFragment instanceof HomeFragment){
            actionBar.setDisplayHomeAsUpEnabled(false);
        }


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.homee:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.transactionn:
                    replaceFragment(new TransactionFragment());
                    break;
                case R.id.profilee:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.plann:
                    replaceFragment(new PlanningFragment());
                    break;
                case R.id.add_transaction:
                    Intent intent = new Intent(HomeActivity.this, AddTransactionActivity.class);
                    startActivity(intent);
                    break;
            }


            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        Intent intent  = new Intent(HomeActivity.this, HomeActivity.class);
        if(currentFragment instanceof CategoriesFragment){
            intent.putExtra("fragmentToGo","profile");
            startActivity(intent);
        }
        else if(currentFragment instanceof ProfileFragment){
            intent.putExtra("fragmentToGo","home");
            startActivity(intent);
        }
        else if(currentFragment instanceof PlanningFragment){
            intent.putExtra("fragmentToGo","home");
            startActivity(intent);
        }
        else if(currentFragment instanceof BillsFragment){
            intent.putExtra("fragmentToGo","plan");
            startActivity(intent);
        }
        else if(currentFragment instanceof BudgetFragment){
            intent.putExtra("fragmentToGo","plan");
            startActivity(intent);
        }
        else if(currentFragment instanceof ValidationBeforeUpdateFragment){
            intent.putExtra("fragmentToGo","profile");
            startActivity(intent);
        }
        else if(currentFragment instanceof EditProfileFragment){
            intent.putExtra("fragmentToGo","profile");
            startActivity(intent);
        }
        else if(currentFragment instanceof WalletsFragment){
            intent.putExtra("fragmentToGo","home");
            startActivity(intent);
        }
        else if(currentFragment instanceof NotificationFragment){
            intent.putExtra("fragmentToGo","profile");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}