package com.qg.smartprinter.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qg.common.logger.Log;
import com.qg.deprecated.util.NetworkHelper;
import com.qg.smartprinter.R;
import com.qg.deprecated.adapter.PrintIdAdapter;
import com.qg.deprecated.logic.param.RegisterParam;
import com.qg.deprecated.logic.result.RegisterResult;
import com.qg.smartprinter.data.source.Message;
import com.qg.smartprinter.data.source.Order;
import com.qg.smartprinter.data.source.OrdersRepository;
import com.qg.smartprinter.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 攀登 on 2016/7/29.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ResigerActivity";
    private EditText shopkeeperName, account, password, storeName, storeAddress, storePhone, id;
    private ListView mListView;
    private TextView addPrintId;
    private Button register;
    private PrintIdAdapter adapter;
    private List<String> mList;
    private ArrayList<String> printIds;
    private String printId;

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityregister);
        initUI();
    }

    private void initUI() {
        shopkeeperName = (EditText) findViewById(R.id.shopkeeper_name);
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        storeName = (EditText) findViewById(R.id.store_name);
        storeAddress = (EditText) findViewById(R.id.store_address);
        storePhone = (EditText) findViewById(R.id.stroe_phone);
        mListView = (ListView) findViewById(R.id.print_id_list);
        addPrintId = (TextView) findViewById(R.id.add_printId);
        register = (Button) findViewById(R.id.register);
        addPrintId.setOnClickListener(this);
        register.setOnClickListener(this);

        mList = new ArrayList<>();
        adapter = new PrintIdAdapter(RegisterActivity.this, R.layout.print_id_item, mList);
        mListView.setAdapter(adapter);
    }

    private void register() {
        if (shopkeeperName.getText().toString().equals("") || account.getText().toString().equals("") || password.getText().toString().equals("") ||
                storeName.getText().toString().equals("") || storeAddress.getText().toString().equals("") || storePhone.getText().toString().equals("") ||
                mList.size() <= 0) {
            Toast.makeText(RegisterActivity.this, R.string.somethingnull, Toast.LENGTH_SHORT).show();
        } else {
            printIds = new ArrayList<>(mList.size());
            for (int i = 0; i < mList.size(); i++) {
                printIds.add(mList.get(i));
            }
            showLoadingDialog();
            new AsyncTask<String, Void, String>() {

                @Override
                protected String doInBackground(String... params) {
                    return requestRegister();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    // 处理结果
                    RegisterResult result = new Gson().fromJson(s, RegisterResult.class);
                    if (result.isOk()) {
                        Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, R.string.register_fail, Toast.LENGTH_LONG).show();
                    }
                    dismissMyDialog();
                }
            }.execute();
        }
    }

    public String requestRegister() {
        String response = "";
        String json = new Gson().toJson(new RegisterParam(shopkeeperName.getText().toString(), account.getText().toString(), password.getText().toString(),
                storeName.getText().toString(), storeAddress.getText().toString(), storePhone.getText().toString(), printIds));
        response = NetworkHelper.getInstance().postToServer("/register_app", json);
        return response;
    }

    private void setAddPrintId() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        final AlertDialog dialog = builder.create();
        View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.input_print_id, null);
        id = (EditText) view.findViewById(R.id.printerId);
        view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((printId = id.getText().toString()).equals("")) {
                    Toast.makeText(RegisterActivity.this, R.string.input_something, Toast.LENGTH_LONG).show();
                } else {
                    mList.add(printId);
                    adapter.notifyDataSetChanged();
                    dialog.cancel();
                }
            }
        });
        view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setView(view);
        dialog.setCancelable(false); // 设置这个对话框不能被用户按[返回键]而取消掉
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_printId:
                setAddPrintId();
                break;
            case R.id.register:
                register();
                break;
        }
    }
}
