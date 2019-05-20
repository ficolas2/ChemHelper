package com.hornedhorn.chemhelper.data;

import android.util.SparseArray;

import java.util.ArrayList;

public class Data {
    public static final SparseArray<Element> elements = new SparseArray<>();
    public static final ArrayList<Compound> allCompounds = new ArrayList<>();
    public static final ArrayList<Compound> includedCompounds = new ArrayList<>();
    public static final ArrayList<Compound> customCompounds = new ArrayList<>();
}
