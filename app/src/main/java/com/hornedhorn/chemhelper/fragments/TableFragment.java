package com.hornedhorn.chemhelper.fragments;

import android.content.Context;
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
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.hornedhorn.chemhelper.MainActivity;
import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.data.Data;
import com.hornedhorn.chemhelper.data.Element;
import com.hornedhorn.chemhelper.views.ElementView;

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
                addSpace(row, context);
            }
            lastGroup = element.group;


            //Create the element button
            ElementView elementView = new ElementView(context);
            elementView.setElement(element);
            elementView.setActivity((MainActivity) getActivity());
            row.addView(elementView);
        }

        //Lanthanides
        row = new TableRow(tableContext);
        row.setPadding(0, 40, 0, 0);
        tableLayout.addView(row);

        for (int i=0; i<3; i++)
            addSpace(row, context);

        for (int i = 58; i <= 71; i++ ){
            Element element = elements.get(i);

            ElementView elementView = new ElementView(context);
            elementView.setElement(element);
            elementView.setActivity((MainActivity) getActivity());
            row.addView(elementView);
        }

        //Actinides
        row = new TableRow(tableContext);
        tableLayout.addView(row);

        for (int i=0; i<3; i++)
            addSpace(row, context);

        for (int i = 90; i <= 103; i++ ){
            Element element = elements.get(i);

            ElementView elementView = new ElementView(context);
            elementView.setElement(element);
            elementView.setActivity((MainActivity) getActivity());
            row.addView(elementView);
        }


    }

    private void addSpace(TableRow row, Context context){
        Space space = new Space(context, null, R.style.PeriodicTable_TableSpace);
        row.addView(space);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_periodic_table, container, false);
    }
}
