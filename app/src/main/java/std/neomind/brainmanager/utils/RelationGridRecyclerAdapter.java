package std.neomind.brainmanager.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.RecyclerView;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

import std.neomind.brainmanager.R;
import std.neomind.brainmanager.data.Keyword;

public class RelationGridRecyclerAdapter extends RecyclerView.Adapter<RelationGridRecyclerAdapter.RelationViewHolder> {
    //rContext 액티비티 context 를 저장함.
    private Context rContext;

    private Keyword rSelectK;

    //rKeywords 전체 키워드들의 배열리스트를 저장함.
    private ArrayList<Keyword> rKeywords;

    public RelationGridRecyclerAdapter(Context context, Keyword targetK, ArrayList<Keyword> keywords) {
        rContext = context;
        rSelectK = targetK;
        rKeywords = keywords;
    }

    //    public RelationGridRecyclerAdapter(Context rContext) {
    //        this.rContext = rContext;
    //        this.rKeywords = null;
    //    }
    @Override
    public RelationViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_relation_keyword_list, viewGroup, false);

        RelationViewHolder viewHolder = new RelationViewHolder(view);

        return viewHolder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //아이템을 생성할 때 발생하는 메소드
    @Override
    public void onBindViewHolder(@NonNull RelationViewHolder viewHolder, int position) {
        Keyword currentItemKey = rKeywords.get(position);
        if(!currentItemKey.name.equals("")) {
            viewHolder.textView.setGravity(Gravity.CENTER);
            viewHolder.textView.setTextAppearance(R.style.textExamTheme);
            viewHolder.textView.setText(rKeywords.get(position).name);
            KeyGridClickListener keyGridClickListener = new KeyGridClickListener(rKeywords.get(position));
            viewHolder.textView.setOnClickListener(keyGridClickListener);
            boolean ck_rel = false;
            //기존 관계성 체크
            for(Integer id : rSelectK.getRelationIds()){
                if(currentItemKey.id == id) {
                    ck_rel = true;
                    break;
                }
            }
            if (ck_rel) {
                currentItemKey.setSelected(true);
                viewHolder.textView.setBackground(ContextCompat.getDrawable(rContext, R.drawable.review_relation_blue_edge));
            } else {
                viewHolder.textView.setBackground(ContextCompat.getDrawable(rContext, R.drawable.review_relation_edge));
            }
        }
        else{
            viewHolder.textView.setBackground(ContextCompat.getDrawable(rContext, R.drawable.review_relation_gray_edge));
        }
    }
    @Override
    public int getItemCount() { return (null != rKeywords) ? rKeywords.size() : 0; }

    ///재사용 되어질지도 모르는 코드는 일단 주석처리
//    public Object getItem(int index) {
//        return (null != rKeywords) ? rKeywords.get(index) : 0;
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
//        String keytxt =rKeywords.get(index).text;
//
//        if (keytxt != "") {
//            if (null != convertView)
//                textView = (TextView) convertView;
//            else {
//                textView = new TextView(rContext);
//                textView.setText(keytxt);
//
//                // 클릭을 처리하는 KeyGridClickListener 객체를 정의
//                // 그리고 그것을 textView 클릭 리스너로 설정합니다.
//                KeyGridClickListener keyGridClickListener
//                        = new KeyGridClickListener(rContext, rKeywords.get(index), textView);
//                textView.setOnClickListener(keyGridClickListener);
//                textView.setBackground(ContextCompat.getDrawable(rContext, R.drawable.review_relation_edge));
//                textView.setHeight(300);
//                textView.setGravity(Gravity.CENTER);
//            }
//        }
//        else
//        {
//                textView = new TextView(rContext);
//                // TODO change
//                //textView.setBackground(ContextCompat.getDrawable(rContext, R.drawable.gray_full_edge));
//                textView.setHeight(300);
//        }
//        return textView;
//    }

    class RelationViewHolder extends RecyclerView.ViewHolder implements AnimateViewHolder {
        TextView textView;

        RelationViewHolder(View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.reviewRelation_item_textView);
        }


        @Override
        public void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
            ViewCompat.setTranslationY(itemView, -itemView.getHeight() * 0.3f);
            ViewCompat.setAlpha(itemView, 0f);
        }

        @Override
        public void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {

        }

        @Override
        public void animateAddImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {
            ViewCompat.animate(itemView).
                    translationY(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(listener)
                    .start();
        }

        @Override
        public void animateRemoveImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {
            ViewCompat.animate(itemView).
                    translationY(-itemView.getHeight() * 0.3f)
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(listener)
                    .start();
        }
    }

    private class KeyGridClickListener implements View.OnClickListener {
        private Keyword key;

        public KeyGridClickListener(Keyword keyword) {
            this.key = keyword;
        }

        public void onClick(View v) {
            //---------------------------------------------------------
            // 클릭된 키워드들을 저장함.
            //
            BrainDBHandler dbHandler = new BrainDBHandler(rContext);
            if (key.isSelected()) {
                key.setSelected(false);
                dbHandler.removeRelation(rSelectK.id, key.id);
                v.setBackground(ContextCompat.getDrawable(rContext, R.drawable.review_relation_edge));
            } else {
                key.setSelected(true);
                try {
                    dbHandler.addRelation(rSelectK.id, key.id);
                }
                catch(BrainDBHandler.DataDuplicationException e){
                    Toast.makeText(rContext, "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                v.setBackground(ContextCompat.getDrawable(rContext, R.drawable.review_relation_blue_edge));
            }
            dbHandler.close();
        }
    }
}

