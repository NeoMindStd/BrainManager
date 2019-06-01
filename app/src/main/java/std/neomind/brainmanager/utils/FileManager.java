package std.neomind.brainmanager.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import std.neomind.brainmanager.data.Keyword;

public final class FileManager {

    public static boolean moveFile(File origin, File target, boolean isOriginDelete) {
        try {
            FileInputStream in = new FileInputStream(origin);
            FileOutputStream out = new FileOutputStream(target);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();

            in.close();
            out.close();

            return ((!isOriginDelete) || origin.delete());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteFile(File file) {
        if (file.exists())
            if (file.delete()) return true;
            else return false;
        else return false;
    }

    public static String getExtension(File origin) {
        String filename = origin.getName();
        int index = filename.lastIndexOf(".");
        if (index == -1) return "";
        return filename.substring(index + 1);
    }

    public static void refreshGallery(Context context, String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static boolean isInternalStorageFile(Context context, String filePath) {
        return filePath != null && filePath.indexOf(context.getFilesDir().toString()) != -1;
    }
}
