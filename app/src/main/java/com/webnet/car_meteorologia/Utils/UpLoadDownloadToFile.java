package com.webnet.car_meteorologia.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class UpLoadDownloadToFile {

    //EXAMPLE
    //new UpLoadDownloadToFile().UploadingAsync(ConfigApp.pathEvidencias,"img.png");

    private final String ImgCar = "imagenescar";
    public static final String storageConnectionString =
            "DefaultEndpointsProtocol=http;"
                    + "AccountName=imgtdriver;"
                    + "AccountKey=SlTtVZX54AyoeWpacxuUGqb6FqAqDAcfro0bOSEwlTIsf/1xApQFSZrGuSNMJSX3bJnHU5oiI6trk0EZFLEioA==";


    public void UploadingAsync(String ruta_fotos, String localFilename, eventUploadImg callback) {
        new Uploading(ruta_fotos, localFilename,callback).execute();
    }

    public void DownloadAsync(String ruta_fotos, String localFilename) {
        new Download(ruta_fotos, localFilename).execute();
    }

    public void DownloadAsync(String ruta_fotos, String localFilename, ImageView mImageView) {
        new Download(ruta_fotos, localFilename, mImageView).execute();
    }


    public class Uploading extends AsyncTask<Void, Void, Void> {

        String ruta_fotos;
        String localFilename;
        eventUploadImg callback;

        public Uploading(String ruta_fotos, String localFilename,eventUploadImg callback) {
            this.ruta_fotos = ruta_fotos;
            this.localFilename = localFilename;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                System.out.println("SUBIENDO FOTO...");
                CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
                System.out.println("SUBIENDO FOTO2...");
                CloudBlobClient serviceClient = account.createCloudBlobClient();
                System.out.println("SUBIENDO FOTO3...");
                CloudBlobContainer container = serviceClient.getContainerReference(ImgCar);
                System.out.println("SUBIENDO FOTO4...");
                //container.createIfNotExists();
                System.out.println("SUBIENDO FOTO5...");
                CloudBlockBlob blob = container.getBlockBlobReference(this.localFilename.trim());
                System.out.println("SUBIENDO FOTO6...");
                File sourceFile = new File(this.ruta_fotos, this.localFilename);
                System.out.println("SUBIENDO FOTO7..." + sourceFile.getAbsolutePath());
                blob.upload(new FileInputStream(sourceFile), sourceFile.length());
                System.out.println("TERMINADO FOTO...");
            } catch (Exception e) {
                System.out.println("ERRO UPLODAMIG: " + e.getMessage());
            }

            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            this.callback.resultLoad(true);
        }

    }

    public class Download extends AsyncTask<Void, Void, String> {
        String ruta_fotos = Environment.getExternalStorageDirectory() + "/";
        ImageView mImageView;
        String localFilename;


        public Download(String ruta_fotos, String localFilename) {
            this.ruta_fotos = ruta_fotos;
            this.localFilename = localFilename;
        }

        public Download(String ruta_fotos, String localFilename, ImageView mImageView) {
            this.ruta_fotos = (ruta_fotos==""?this.ruta_fotos:ruta_fotos);
            this.localFilename = localFilename;
            this.mImageView = mImageView;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
                CloudBlobClient serviceClient = account.createCloudBlobClient();
                CloudBlobContainer container = serviceClient.getContainerReference(ImgCar);
                container.createIfNotExists();
                CloudBlockBlob blob = container.getBlockBlobReference(this.localFilename.trim());
                // Download the image file.


                File destinationFile = new File(this.ruta_fotos, this.localFilename);
                blob.downloadToFile(destinationFile.getAbsolutePath());
                return destinationFile.getAbsolutePath();
            } catch (FileNotFoundException e) {
                 //Crashlytics.logException(e);
            } catch (StorageException e) {
                 //Crashlytics.logException(e);
            } catch (Exception e) {
                 //Crashlytics.logException(e);
            }

            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String AbsolutePath) {
            super.onPostExecute(AbsolutePath);
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(AbsolutePath);
                this.mImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                 //Crashlytics.logException(e);
            }
        }

    }


}
