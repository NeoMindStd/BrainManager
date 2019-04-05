package std.neomind.brainmanager;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


import androidx.core.content.ContextCompat;
import std.neomind.brainmanager.R;
import std.neomind.brainmanager.data.Keyword;

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
        if(click) {
            click = false;
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.edge));
        }
        else {
            click=true;
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_edge));
        }


    }
}
