package std.neomind.brainmanager;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import std.neomind.brainmanager.data.Keyword;

public class RelationGridAdapter extends BaseAdapter {
    Context context = null;
    //context는 액티비티 context값을 저장함.
    //allKey가 전체 키워드들의 배열리스트를 저장함.
    private ArrayList<Keyword> allKey;

    public RelationGridAdapter(Context context, ArrayList<Keyword> mKey) {
        this.context = context;
        this.allKey = mKey;
    }

    public RelationGridAdapter(Context context) {
        this.context = context;
        this.allKey = null;
    }
    //임시 getter, getter 지양에 따른 임시 메소드
    public int getCount() { return (null != allKey) ? allKey.size() : 0; }
    public Object getItem(int index) {
        return (null != allKey) ? allKey.get(index) : 0;
    }
    public long getItemId(int position) {
        return position;
    }
    //////////////////////////
    public View getView(int index, View convertView, ViewGroup parent) {
        TextView textView = null;
        String keytxt =allKey.get(index).text;

        if (keytxt != "") {
            if (null != convertView)
                textView = (TextView) convertView;
            else {
                textView = new TextView(context);
                textView.setText(keytxt);

                // 클릭을 처리하는 KeyGridClickListener 객체를 정의
                // 그리고 그것을 textView 클릭 리스너로 설정합니다.
                KeyGridClickListener keyGridClickListener
                        = new KeyGridClickListener(context, allKey.get(index), textView);
                textView.setOnClickListener(keyGridClickListener);
                textView.setBackground(ContextCompat.getDrawable(context, R.drawable.edge));
                textView.setHeight(300);
                textView.setGravity(Gravity.CENTER);
            }
        }
        else
        {
                textView = new TextView(context);
                // TODO change
                //textView.setBackground(ContextCompat.getDrawable(context, R.drawable.gray_full_edge));
                textView.setHeight(300);
        }
        return textView;
    }
}
