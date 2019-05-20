package com.hornedhorn.chemhelper;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import com.hornedhorn.chemhelper.data.Compound;
import com.hornedhorn.chemhelper.data.Data;
import com.hornedhorn.chemhelper.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CompoundSuggestionProvider extends ContentProvider {

    private static Comparator<Object[]> rowComparator = new Comparator<Object[]>() {
        @Override
        public int compare(Object[] o1, Object[] o2) {
            return Integer.signum(((String)o1[1]).length() -
                    ((String)o2[1]).length());
        }
    };

    private static String[] matrixCursorColumns = {
            "_id", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA
    };

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor searchResults = new MatrixCursor(matrixCursorColumns);
        String searchString = uri.getLastPathSegment().toLowerCase();

        if (searchString.length()<3)
            return searchResults;

        ArrayList<Object[]> rows = new ArrayList<>();


        for (Compound compound : Data.allCompounds){
            Object[] newRow = new Object[3];
            String containingName = compound.getContainingName(searchString);
            if ( containingName != null ){
                newRow[1] = Utils.formatName(containingName);
                newRow[2] = Utils.formatName(containingName);
                rows.add(newRow);
            }
        }

        Collections.sort(rows, rowComparator);

        int id = 0;
        for (Object[] row : rows) {
            row[0] = id;
            searchResults.addRow(row);
            id++;
        }

        return searchResults;
    }

    @Override
    public String getType( Uri uri ) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
