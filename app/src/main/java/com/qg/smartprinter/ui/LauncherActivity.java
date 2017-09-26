package com.qg.smartprinter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qg.smartprinter.Injection;
import com.qg.smartprinter.R;
import com.qg.smartprinter.localorder.DevicesManager;
import com.qg.smartprinter.localorder.OrderManager;
import com.qg.smartprinter.localorder.bluetooth.BTTestActivity;
import com.qg.smartprinter.localorder.selectdevice.SelectDeviceActivity;
import com.qg.smartprinter.localorder.status.localstatus.LocalStatusActivity;
import com.qg.smartprinter.localorder.wifi.WifiTestActivity;

public class LauncherActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ListView launchers = (ListView) findViewById(R.id.launchers);
        final ArrayAdapter<StarterItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        launchers.setAdapter(adapter);
        launchers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.getItem(position).starter.start();
            }
        });
        adapter.add(new StarterItem(getString(R.string.title_activity_login), new StarterItem.Starter() {
            @Override
            public void start() {
                LoginActivity.start(LauncherActivity.this);
            }
        }));
        adapter.add(new StarterItem(getString(R.string.title_activity_server_setting), new StarterItem.Starter() {
            @Override
            public void start() {
                ServerSettingActivity.start(LauncherActivity.this);
            }
        }));
        adapter.add(new StarterItem(getString(R.string.register), new StarterItem.Starter() {
            @Override
            public void start() {
                RegisterActivity.start(LauncherActivity.this);
            }
        }));
        adapter.add(new StarterItem(getString(R.string.title_activity_business_info), new StarterItem.Starter() {
            @Override
            public void start() {
                BusinessInfoActivity.start(LauncherActivity.this);
            }
        }));
        adapter.add(new StarterItem(getString(R.string.blue_test), new StarterItem.Starter() {
            @Override
            public void start() {
                BTTestActivity.start(LauncherActivity.this);
            }
        }));

        adapter.add(new StarterItem(getString(R.string.wifi_test), new StarterItem.Starter() {
            @Override
            public void start() {
                WifiTestActivity.start(LauncherActivity.this);
            }
        }));
        adapter.add(new StarterItem(getString(R.string.direct_location_order), new StarterItem.Starter() {
            @Override
            public void start() {
                BusinessInfoActivity.start(LauncherActivity.this);
            }
        }));

        adapter.add(new StarterItem(getString(R.string.title_activity_local_status), new StarterItem.Starter() {
            @Override
            public void start() {
                LocalStatusActivity.start(LauncherActivity.this);
            }
        }));

        adapter.add(new StarterItem("清空数据库", new StarterItem.Starter() {

            @Override
            public void start() {
                Injection.provideOrdersRepository(getApplicationContext())
                        .deleteDatabase(getApplicationContext(), Injection.provideBaseSchedulerProvider());
                OrderManager.getInstance().resetOrderNumber();
                DevicesManager.getInstance().clear();
            }
        }));
        adapter.add(new StarterItem("设备管理", new StarterItem.Starter() {
            @Override
            public void start() {
                SelectDeviceActivity.start(LauncherActivity.this, 0);
            }
        }));
    }

    static class StarterItem {
        private String text;
        private Starter starter;

        StarterItem(String text, Starter starter) {
            this.text = text;
            this.starter = starter;
        }

        @Override
        public String toString() {
            return text;
        }

        interface Starter {
            void start();
        }
    }
}
