package com.ongoni.csit.ssunews.db;

import android.provider.BaseColumns;

public class SSUDbContract implements BaseColumns {

    public static final String TABLE_NAME = "articles";

    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PUBDATE = "pub_date";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_GUID = "guid";

    private SSUDbContract() {}
}