package std.neomind.brainmanager.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import std.neomind.brainmanager.R;
import std.neomind.brainmanager.data.Keyword;

public class RelationGridAdapter extends RecyclerView.Adapter<RelationGridAdapter.RelationGridHolder> {
    Context context = null;
    //context는 액티비티 context값을 저장함.
    //allKey가 전체 키워드들의 배열리스트를 저장함.
    private ArrayList<Keyword> allKey;

    public class RelationGridHolder extends RecyclerView.ViewHolder {
        protected TextView item;


        public RelationGridHolder(View view) {
            super(view);
            this.item = (TextView) view.findViewById(R.id.griditem);
        }
    }
    //

    public RelationGridAdapter(Context context, ArrayList<Keyword> mKey) {
        this.context = context;
        this.allKey = mKey;
    }

    //    public RelationGridAdapter(Context context) {
//        this.context = context;
//        this.allKey = null;
//    }
    @Override
    public RelationGridHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, null);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.keywordlist, viewGroup, false);

        RelationGridHolder viewHolder = new RelationGridHolder(view);

        return viewHolder;
    }
    //아이템을 생성할 때 발생하는 메소드
    @Override
    public void onBindViewHolder(@NonNull RelationGridHolder viewholder, int position) {
        if(allKey.get(position).name != "") {
            viewholder.item.setGravity(Gravity.CENTER);
            viewholder.item.setText(allKey.get(position).name);
            KeyGridClickListener keyGridClickListener = new KeyGridClickListener(context, allKey.get(position), viewholder.item);
            viewholder.item.setOnClickListener(keyGridClickListener);
        }
        else{
            viewholder.item.setBackground(ContextCompat.getDrawable(context, R.drawable.gray_edge));
        }

    }


    public int getItemCount() { return (null != allKey) ? allKey.size() : 0; }

    ///재사용 되어질지도 모르는 코드는 일단 주석처리
//    public Object getItem(int index) {
//        return (null != allKey) ? allKey.get(index) : 0;
//    }
//    public long getItemId(int position) {
//        return position;
//
//    }
//    //////////////////////////
//
//
//    public View getView(int index, View convertView, ViewGroup parent) {
//        TextView textView = null;
//        String keytxt =allKey.get(index).text;
//
//        if (keytxt != "") {
//            if (null != convertView)
//                textView = (TextView) convertView;
//            else {
//                textView = new TextView(context);
//                textView.setText(keytxt);
//
//                // 클릭을 처리하는 KeyGridClickListener 객체를 정의
//                // 그리고 그것을 textView 클릭 리스너로 설정합니다.
//                KeyGridClickListener keyGridClickListener
//                        = new KeyGridClickListener(context, allKey.get(index), textView);
//                textView.setOnClickListener(keyGridClickListener);
//                textView.setBackground(ContextCompat.getDrawable(context, R.drawable.edge));
//                textView.setHeight(300);
//                textView.setGravity(Gravity.CENTER);
//            }
//        }
//        else
//        {
//                textView = new TextView(context);
//                // TODO change
//                //textView.setBackground(ContextCompat.getDrawable(context, R.drawable.gray_full_edge));
//                textView.setHeight(300);
//        }
//        return textView;
//    }

    public class KeyGridClickListener implements View.OnClickListener {
        Context context;
        Keyword key;
        TextView textView;
        private Boolean click;

        public KeyGridClickListener(Context context, Keyword keyword, TextView textView) {
            this.context = context;
            this.key = keyword;
            this.textView = textView;
            this.click = false;
        }

        public void onClick(View v) {
            //---------------------------------------------------------
            // 클릭된 키워드들을 저장함.
            //
            if (click) {
                click = false;
                textView.setBackground(ContextCompat.getDrawable(context, R.drawable.edge));
            } else {
                click = true;
                textView.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_edge));
            }


        }
    }
}
