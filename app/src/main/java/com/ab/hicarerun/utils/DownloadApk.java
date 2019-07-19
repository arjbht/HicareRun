package com.ab.hicarerun.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.ab.hicarerun.BuildConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadApk extends AsyncTask<String, Void, Void> {
    ProgressDialog progressDialog;
    int status = 0;

    private Context context;

    public void setContext(Context context, ProgressDialog progress) {
        this.context = context;
        this.progressDialog = progress;
    }

    public void onPreExecute() {
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");

            // c.setDoOutput(true);
            c.connect();

            File sdcard = Environment.getExternalStorageDirectory();
            File myDir = new File(sdcard, "Download");
            myDir.mkdirs();
            File outputFile = new File(myDir, "app-debug.apk");
            if (outputFile.exists()) {
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.flush();
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.fromFile(new File(sdcard, "Download/app-debug.apk")), "application/vnd.android.package-archive");
//            intent.setDataAndType(FileProvider.getUriForFile(context,"com.ab.hicarerun.utils.DownloadFileProvider",sdcard));
//            Uri apkURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".utils.DownloadFileProvider", sdcard);
//            intent.setDataAndType(apkURI,"application/vnd.android.package-archive");
            Uri uri=FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider", new File(sdcard, "Download/app-debug.apk"));
            intent.setDataAndType(uri,"application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION); // without this flag android returned a intent error!
            SharedPreferencesUtility.savePrefBoolean(context, SharedPreferencesUtility.IS_USER_LOGIN,
                    false);
            context.startActivity(intent);


        } catch (FileNotFoundException fnfe) {
            status = 1;
            Log.e("File", "FileNotFoundException! " + fnfe);
        } catch (Exception e) {
            Log.e("UpdateAPP", "Exception " + e);
        }
        return null;
    }

    public void onPostExecute(Void unused) {
        progressDialog.dismiss();

        if (status == 1)
            Toast.makeText(context, "File Not Available", Toast.LENGTH_LONG).show();
    }
}