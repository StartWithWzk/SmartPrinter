package com.qg.smartprinter.localorder.status.localstatus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.qg.smartprinter.R;
import com.qg.smartprinter.ui.BaseActivity;

import static com.qg.common.Preconditions.checkNotNull;

public class LocalStatusActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, LocalStatusActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_status);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = checkNotNull(getSupportActionBar());
        ab.setDefaultDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        StatusPagerAdapter statusPagerAdapter = new StatusPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(statusPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    private class StatusPagerAdapter extends FragmentPagerAdapter {

        private StatusPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return OrderStatusFragment.newInstanceWithPresenter(LocalStatusActivity.this);
                case 1:
                    return PrinterStatusFragment.newInstanceWithPresenter();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.order_status);
                case 1:
                    return getString(R.string.printer_status);
            }
            return null;
        }
    }
}
