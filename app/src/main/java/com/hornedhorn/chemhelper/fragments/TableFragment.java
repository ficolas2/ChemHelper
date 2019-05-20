package com.hornedhorn.chemhelper.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.hornedhorn.chemhelper.ChemApplication;
import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.data.Data;
import com.hornedhorn.chemhelper.data.Element;

public class TableFragment extends Fragment {

    public TableFragment(){

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SparseArray<Element> elements = Data.elements;
        Context context = getContext();
        TableLayout tableLayout = view.findViewById(R.id.periodicTable);
        TableRow row = null;

        ContextThemeWrapper tableContext = new ContextThemeWrapper(context, R.style.PeriodicTable_TableRow);
        ContextThemeWrapper atomicNumberContext = new ContextThemeWrapper(context, R.style.PeriodicTable_AtomicNumber);
        ContextThemeWrapper tableElementContext = new ContextThemeWrapper(context, R.style.PeriodicTable_TableElement);
        ContextThemeWrapper symbolContext = new ContextThemeWrapper(context, R.style.PeriodicTable_Symbol);

        int lastGroup = 0;
        int lastPeriod = 0;

        for (int i = 1; i <= elements.size(); i++) {
            Element element = elements.get(i);
            if (element==null) {
                Log.e("Periodic table", "Element " + i + " missing.");
                continue;
            }
            if ( !(element.group>0 && element.period>0) )
                continue;


            //New period
            if (lastPeriod < element.period) {
                lastGroup = 0;
                lastPeriod = element.period;
                row = new TableRow(tableContext);
                tableLayout.addView(row);
            }

            //Spaces when there are groups in between
            for (int group = lastGroup + 1; group < element.group; group++) {
                Space space = new Space(context, null, R.style.PeriodicTable_TableSpace);
                row.addView(space);
            }
            lastGroup = element.group;


            //Create the element button
            RelativeLayout relativeLayout = new RelativeLayout(tableElementContext);
            Resources r = getResources();
            int colorId = r.getIdentifier( element.category ,"color", context.getPackageName());
            relativeLayout.setBackgroundColor( r.getColor( colorId ) );
            row.addView(relativeLayout);

            //Atomic number
            TextView atomicNumber = new TextView(atomicNumberContext);
            atomicNumber.setText(Integer.toString(element.atomicNumber));
            relativeLayout.addView(atomicNumber);

            //Element symbol
            TextView symbol = new TextView(symbolContext);
            symbol.setText(element.getFormulaString());
            relativeLayout.addView(symbol);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.content_periodic_table, container, false);
    }
}
