package com.matemaker.rtt.app.Classes.Adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;

public class ExtendedListView extends ListView {

    private android.view.ViewGroup.LayoutParams params;
    private int old_count = 0;

    public ExtendedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getCount() != old_count) {
            old_count = getCount();
            params = getLayoutParams();
            int Height;
            if (getChildAt(0) == null)
                Height = 200;
            else
                Height = getChildAt(0).getHeight();
            params.height = getCount() * (old_count > 0 ? Height + (3/2) : 0);
            setLayoutParams(params);
        }

        super.onDraw(canvas);
    }

}