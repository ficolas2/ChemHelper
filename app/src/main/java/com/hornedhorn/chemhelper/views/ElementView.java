package com.hornedhorn.chemhelper.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hornedhorn.chemhelper.MainActivity;
import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.data.Element;

public class ElementView extends RelativeLayout {

    private Element element;

    private View color;
    private TextView symbol, number;

    private MainActivity activity;

    public ElementView(Context context) {
        this(context, null);
    }

    public ElementView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ElementView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.periodic_table_element, this);

        color = findViewById(R.id.element_color);
        symbol = findViewById(R.id.element_symbol);
        number = findViewById(R.id.element_number);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (element!=null && activity !=null)
                 activity.setInfoFragment(element);
            }
        });
    }

    public void setElement(Element element){
        this.element = element;
        this.symbol.setText(element.getFormulaString());
        this.number.setText(Integer.toString(element.atomicNumber));

        Resources r = getResources();
        int colorId = r.getIdentifier( element.category ,"color", getContext().getPackageName());
        color.setBackgroundColor( r.getColor( colorId ) );
    }

    public void setActivity(MainActivity activity){
        this.activity = activity;
    }
}
