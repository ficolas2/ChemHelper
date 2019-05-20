package com.hornedhorn.chemhelper.views;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hornedhorn.chemhelper.R;
import com.hornedhorn.chemhelper.data.Data;
import com.hornedhorn.chemhelper.utils.Utils;
import com.hornedhorn.chemhelper.data.Compound;
import com.hornedhorn.chemhelper.fragments.CompoundFragment;

import java.util.ArrayList;
import java.util.Map;

public class CompoundAdapter extends BaseAdapter {

    public enum CompoundAdapterData{
        FORMULA, NAME, ID, ORDER
    }

    private ArrayList<Map<CompoundAdapterData, String>> data;
    private LayoutInflater layoutInflater;
    private CompoundFragment compoundFragment;

    public CompoundAdapter(Activity activity, ArrayList<Map<CompoundAdapterData, String>> data, CompoundFragment compoundFragment){
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.compoundFragment = compoundFragment;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null)
            view = layoutInflater.inflate(R.layout.compound_search_layout, null);

        final Compound compound = Data.allCompounds.get(Integer.parseInt(data.get(position).get(CompoundAdapterData.ID)));

        ((TextView)view.findViewById(R.id.compound_name)).setText(data.get(position).get(CompoundAdapterData.NAME));

        SpannableStringBuilder formula = new SpannableStringBuilder(data.get(position).get(CompoundAdapterData.FORMULA));
        Utils.addSubscripts(formula);
        ((TextView)view.findViewById(R.id.compound_formula)).setText(formula, TextView.BufferType.SPANNABLE);

        final View options = view.findViewById(R.id.compound_options);
        options.setVisibility(Data.customCompounds.contains(compound) ? View.VISIBLE:View.GONE);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compoundFragment.clickCompoundOptions(compound, options);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compoundFragment.clickCompound(compound);
            }
        });
        return view;
    }

}
