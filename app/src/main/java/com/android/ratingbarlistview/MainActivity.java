package com.android.ratingbarlistview;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListenerActivity {

    private ListView listView;
    private ArrayList<Item> itemList;
    private PackageManager packageManager;
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.list_view);
        itemList = new ArrayList<>();

        itemAdapter = new ItemAdapter(this, R.layout.item_listview, itemList, this);

        listView.setAdapter(itemAdapter);

        packageManager = getPackageManager();

        new LoadApplications().execute();
    }


    @Override
    public void runSelectedApp(int position) {
        ApplicationInfo app = itemList.get(position).getApplicationInfo();

        try{
            Intent intent = packageManager.getLaunchIntentForPackage(app.packageName);

            if(intent != null) {
                startActivity(intent);
            }
        } catch(ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            for (ApplicationInfo applicationInfo : checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA))) {
                String appName = (String) applicationInfo.loadLabel(packageManager);
                itemList.add(new Item(applicationInfo, 0, appName));
            }
            return null;
        }

        private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {

            ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();

            for(ApplicationInfo info : list) {
                try{
                    if(packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                        appList.add(info);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return appList;
        }

        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();
            listView.invalidateViews();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, null, "Loading apps info...");
            super.onPreExecute();
        }
    }
}