package com.example.jeremychen.downloadapp2asynctask;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Jeremychen on 2018/1/29.
 */

public class MyDownLoad_Method {

    public static void download(String url, String file_path, Execution_Operation operator)
    {
        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(url, file_path, operator);
        downloadAsyncTask.execute();
    }


    public static class DownloadAsyncTask extends AsyncTask<String, Integer, Boolean>
    {
        private String m_url;
        private String file_path;
        private Execution_Operation operation;

        public DownloadAsyncTask(String url, String file_path, Execution_Operation operation) {
            this.m_url = url;
            this.file_path = file_path;
            this.operation = operation;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(operation != null)
            {
                operation.onStart();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (operation!=null)
            {
                operation.onSuccess(aBoolean);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values != null && values.length > 0)
            {
                if(operation != null)
                    operation.onProgress(values[0]);
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String app_url = m_url;

            try {
                URL url = new URL(app_url);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                int App_content = urlConnection.getContentLength();

                File file = new File(file_path);
                if(file.exists())
                {
                    boolean result = file.delete();
                    if(!result)
                    {
                        if(operation != null)
                        {
                            operation.onFail(-1, "文件删除失败！");
                        }
                        else
                            return false;
                    }
                }

                OutputStream outputStream = new FileOutputStream(file);

                int downloadSize = 0;
                int len = 0;
                byte[] b = new byte[1024];

                while((len = inputStream.read(b)) != -1)
                {
                    outputStream.write(b,0,len);
                    downloadSize += len;
                    publishProgress(downloadSize * 100 / App_content);
                }
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                if(operation!=null)
                {
                    operation.onFail(-2,e.getMessage());
                }
                e.printStackTrace();
            }
            return true;

        }
    }

    public interface Execution_Operation
    {
        /*
           some operations used to initialize some views status before the download progress!
         */
        void onStart();

        /*
            some operations used to change the status after the download progress!
         */
        void onSuccess(boolean code);

        /*
            some operations used to demonstrate a behavior in case of there is a mistake!
         */
        void onFail(int flag, String message);

        /*
            operations used to update our progress bar!
         */
        void onProgress(int progress);
    }
}
