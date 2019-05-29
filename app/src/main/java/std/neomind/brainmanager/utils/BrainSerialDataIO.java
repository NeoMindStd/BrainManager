package std.neomind.brainmanager.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import std.neomind.brainmanager.MainActivity;

public class BrainSerialDataIO {
    private static final  String TAG = "BrainSerialDataIO";

    public static void saveNextReivewTimeInfo(Context context,ArrayList<Integer> idList, ArrayList<Long> dateList) throws SaveFailException, ListNotEqualSizeException{
        if(idList.size() != dateList.size()) {
            Log.d(TAG, "시리얼 IO 저장 양 리스트 사이즈가 다름");
            throw new ListNotEqualSizeException();
        }
        try{
            Log.d(TAG, "시리얼 IO 저장 시작");
            FileOutputStream fileStream = new FileOutputStream(new File(context.getFilesDir(),"BrainAlarm.data"));
            try {
                ObjectOutputStream os = new ObjectOutputStream(fileStream);
                os.writeObject(idList);
                os.writeObject(dateList);
                os.close();
                fileStream.close();
                Log.d(TAG, "시리얼 IO 저장 성공");
            }
            catch (Exception ioe){
                Log.d(TAG, "시리얼 IO 저장파일 Close 실패");
                throw new SaveFailException();
            }
        }
        catch(FileNotFoundException e2){
            Log.d(TAG, "시리얼 IO 저장 실패");
            throw new SaveFailException();
        }
    }
    public static void getNextReviewTimeInfo(Context context, ArrayList<Integer> idList, ArrayList<Long> dateList) throws LoadFailException, ListNotEqualSizeException{
        try {
            Log.d(TAG, "시리얼 IO 불러오기 시작");
            FileInputStream fileStream = new FileInputStream(new File(context.getFilesDir(), "BrainAlarm.data"));
            try {
                ObjectInputStream os = new ObjectInputStream(fileStream);

                ArrayList<Integer> tempIdList = (ArrayList<Integer>) os.readObject();
                ArrayList<Long> tempDateList = (ArrayList<Long>) os.readObject();
                //y
                os.close();
                fileStream.close();
                if(idList.size() != dateList.size()){
                    Log.d(TAG, "시리얼 IO 불러오기 양 리스트 사이즈가 다름");
                    throw new ListNotEqualSizeException();
                }
                idList.addAll(tempIdList);
                dateList.addAll(tempDateList);
                Log.d(TAG, "시리얼 IO 불러오기 성공");
            } catch (Exception ioe) {
                Log.d(TAG, "시리얼 IO 불러오기 파일 Close 실패");
                throw new LoadFailException();
            }
        } catch (FileNotFoundException e) {    //새로 만드는 부분 => 없앰
            Log.d(TAG, "시리얼 IO 불러오기 실패");
            throw new LoadFailException();
        }
    }

    public static void deleteOneNextReivewTimeInfo(Context context, int id) throws SaveFailException, ListNotEqualSizeException{
        ArrayList<Integer> idList;
        ArrayList<Long> dateList;
        try {
            FileInputStream fileStream = new FileInputStream(new File(context.getFilesDir() ,"BrainAlarm.data"));
            try {
                ObjectInputStream os = new ObjectInputStream(fileStream);

                idList = (ArrayList<Integer>)os.readObject();
                dateList = (ArrayList<Long>) os.readObject();
                //y
                os.close();
                fileStream.close();
                if(idList.size() != dateList.size()){
                    Log.d(TAG, "시리얼 IO 삭제 전 불러오기 양 리스트 사이즈가 다름");
                    throw new ListNotEqualSizeException();
                }
                try {
                    FileOutputStream fileStream2 = new FileOutputStream(new File(context.getFilesDir(), "BrainAlarm.data"));
                    try {
                        ObjectOutputStream os2 = new ObjectOutputStream(fileStream2);

                        int tmp = idList.indexOf(id);
                        dateList.remove(tmp);
                        idList.remove(tmp);


                        os2.writeObject(idList);
                        os2.writeObject(dateList);
                        os2.close();
                        fileStream2.close();
                    } catch (Exception ioe) {
                        Log.d(TAG, "시리얼 IO 삭제 후 파일 저장 Close 실패");
                        throw new SaveFailException();
                    }
                }
                catch (Exception ioe){
                    Log.d(TAG, "시리얼 IO 삭제 후 파일 저장 실패");
                    throw new SaveFailException();
                }
            }
            catch (Exception ioe){
                Log.d(TAG, "시리얼 IO 삭제 전 불러온 파일 Close 실패");
                throw new SaveFailException();
            }
        } catch (Exception e) {
            Log.d(TAG, "시리얼 IO 삭제 전 파일 불러오기 실패");
            throw new SaveFailException();
        }
    }

    public static class SaveFailException extends Exception{}
    public static class LoadFailException extends Exception{}
    public static class ListNotEqualSizeException extends Exception{}
}
