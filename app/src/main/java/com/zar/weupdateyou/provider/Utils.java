package com.zar.weupdateyou.provider;

import android.util.Log;

import com.zar.weupdateyou.doc.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by KJS on 11/19/2016.
 */
public class Utils {
    public static boolean downloadFile_with_Get(String url, String fileFullPath)
    {
        if (url.equals("")) return false;

        int Read;
        int readByte = 0 ;
        double perByte = 0 ;
        int prevperByte = 0;

        try {
            File file = new File(fileFullPath);
            if (file.exists())	return true;

            URL mUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)mUrl.openConnection();

            int len = conn.getContentLength();
            InputStream is = conn.getInputStream();
            byte[] raster = new byte[5000 + 1];
            Log.e("needman", String.format("GetImageDownload Buffersize : %d", raster.length));
            FileOutputStream fos = new FileOutputStream(file);

            for (;;) {
                Read = is.read(raster);
                if (Read <= 0) 	break;

                readByte += Read;
                perByte =  (double)readByte / (double)len * 100.0 ;

                if ( (int)perByte != prevperByte) 	prevperByte = (int)perByte ;

                fos.write(raster,0, Read);
            }

            is.close();
            fos.close();
            conn.disconnect();
            if (raster != null) raster = null;


        } catch (Exception e) {
            File file = new File(fileFullPath);
            if (file.exists()) file.delete();
            return false;
        } catch (java.lang.OutOfMemoryError e) {
            File file = new File(fileFullPath);
            if (file.exists()) file.delete();
            return false;
        }
        return true;
    }

    public static String getSDCardFullPath(String filename){
        try{
            File file = null;
            file = new File(Constants.DOWNLOAD_DIR);

            if(!file.exists()){
                file.mkdir();
            }
            String strResult = "";
            strResult = String.format("%s/%s", Constants.DOWNLOAD_DIR, filename);

            return strResult;

        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }
    public static String getThumbImageFilePath(String url)
    {
        long time = System.currentTimeMillis();
        String strResult = String.format("%d_thumb",time);
        return strResult;
    }
    public static String getBigImageFilePath(String url)
    {
        long time = System.currentTimeMillis();
        String extension = "jpg";
        String strResult = String.format("%d_big%s",time,extension);
        return strResult;
    }
    /*public static String getMusicFileName(SongItem item)
    {
        String strResult = String.format("%d_%d.mp3",item.category_id,item.music_id);
        return getSDCardFullPath(strResult);
    }*/

    public static boolean deleteFile(String url)
    {
        File file = new File(url);
        if(file.exists())
            return file.delete();
        return true;
    }

   /* public static  String getImageFileName(SongItem item)
    {

        String strResult = String.format("%d_%d.png",item.category_id,item.music_id);
        return getSDCardFullPath(strResult);
    }*/


}