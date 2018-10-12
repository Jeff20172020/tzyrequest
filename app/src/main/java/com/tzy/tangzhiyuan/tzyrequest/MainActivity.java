package com.tzy.tangzhiyuan.tzyrequest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tzy.requestlib.XHttpManager;
import com.tzy.requestlib.core.xrequest.XRequest;
import com.tzy.requestlib.gson.DefaultGsonResponseConverter;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XHttpManager.getInstance().setDebug(true)
                .setEncryptPolicy(new XHttpManager.EncryptPolicy() {
                    @Override
                    public HashMap<String, String> encrypt(HashMap<String, String> parameters) {
                        return parameters;
                    }
                }).setResponseConverter(DefaultGsonResponseConverter.create());


        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XRequest.create("http://www.wanandroid.com/project/list/1/json")
                        .addParameter("cid", 294)
                        .addConfiguration("isSign", false)
                        .addConfiguration("otherKey", "asdfasdfdsaadf2342353")
                        .setRetryCount(2)
                        .observable(DemoResponse.class)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new io.reactivex.functions.Consumer<DemoResponse>() {
                            @Override
                            public void accept(DemoResponse stringResponse) throws Exception {
                                Log.i(TAG, "dalBaseResponse: " + stringResponse.toString());
                            }

                        }, new io.reactivex.functions.Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.i(TAG, ": " + throwable);

                            }
                        });
            }
        });
    }
}
