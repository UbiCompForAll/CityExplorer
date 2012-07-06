package org.ubicompforall.CityExplorer.data;

//Only available in API >9. We use 7, I think...
/**
import android.app.DownloadManager;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class DownloadManagerActivity extends Activity {
     private long quueue_for_url;

     private DownloadManager dm;

        // Called when the activity is first created.
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        long downloadId = intent.getLongExtra(
                                DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                        Query query = new Query();
                        query.setFilterById(quueue_for_url);
                        Cursor c = dm.query(query);
                        if (c.moveToFirst()) {
                            int columnIndex = c
                                    .getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c
                                    .getInt(columnIndex)) {

                                ImageView view = (ImageView) findViewById(R.id.imageView1);
                                String uri_String_abcd = c
                                        .getString(c
                                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                view.setImageURI(Uri.parse(uri_String_abcd));
                            }
                        }
                    }
                }
            };

            registerReceiver(receiver, new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        public void onClick(View view) {
            dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

            Request request_for_url = new Request(
                    Uri.parse("http://fc03.deviantart.net/fs14/i/2007/086/9/1/Steve_Jobs_portrait_by_tumb.jpg"));

            Request request_for_url1 = new Request(
                    Uri.parse("http://2.bp.blogspot.com/_q7Rxg4wqDyc/S5ZRVLxVYuI/AAAAAAAAAvU/fQAUZ2XFcp8/s400/katrina-kaif.jpg"));
            Request request_for_url2 = new Request(
                    Uri.parse("http://www.buzzreactor.com/sites/default/files/Bill-Gates1.jpg"));

            quueue_for_url = dm.enqueue(request_for_url);
            quueue_for_url = dm.enqueue(request_for_url1);
            quueue_for_url = dm.enqueue(request_for_url2);

        }

        public void showDownload(View view) {
            Intent i = new Intent();
            //try more options to show downloading , retrieving and complete
            i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
            startActivity(i);
        }
    }
**/
