package com.dankira.achat.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class AchatDbContracts
{
    public static final String CONTENT_AUTHORITY = "com.dankira.achat.contentprovider";
    public static final String PATH_SHOPPING_LIST = "shopping_list";
    public static final String PATH_SHOPPING_ITEM = "shopping_item";
    public static final String DATABASE_NAME = "achat.db";
    public static final int DATABBASE_VERSION = 7;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class ShoppingListTable implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_SHOPPING_LIST)
                .build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_SHOPPING_LIST;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_SHOPPING_LIST;

        public static final String TABLE_NAME = "shopping_list";
        public static final String LIST_TITLE = "list_title";
        public static final String LIST_DESCRIPTION = "list_desc";
        public static final String LIST_GUID = "list_guid";
        public static final String LIST_SHARE_STATUS = "list_share_status";
        public static final String LIST_CREATED_ON = "list_created_on";
        public static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LIST_TITLE + " TEXT UNIQUE NOT NULL, " +
                LIST_DESCRIPTION + " TEXT, " +
                LIST_GUID + " TEXT, " +
                LIST_CREATED_ON + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                LIST_SHARE_STATUS + " INTEGER DEFAULT 0) ";
        public static final String DEFAULT_SORT_ORDER = _ID + " ASC";

        public static Uri buildShoppingListUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_SQL);
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVer, int newVer)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public static final class ShoppingItemTable implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_SHOPPING_ITEM)
                .build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_SHOPPING_ITEM;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_SHOPPING_ITEM;

        public static final String TABLE_NAME = "shopping_item";
        public static final String LIST_GUID = "list_guid";
        public static final String ITEM_NAME = "item_name";
        public static final String ITEM_QUANTITY = "item_qty";
        public static final String ITEM_DESCRIPTION = "item_disc";
        public static final String ITEM_CHECKED = "item_checked";
        public static final String ITEM_CREATED_ON = "created_on";
        public static final String ITEM_GROUP = "item_group";
        public static final String ITEM_BAR_CODE = "item_barcode";

        public static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +
                _ID + " INTEGER DEFAULT 1, " +
                LIST_GUID + " TEXT NOT NULL REFERENCES " + ShoppingListTable.TABLE_NAME +
                "(" + ShoppingListTable.LIST_GUID + ") ON DELETE CASCADE," +
                ITEM_NAME + " TEXT NOT NULL, " +
                ITEM_QUANTITY + " INTEGER DEFAULT 1," +
                ITEM_DESCRIPTION + " TEXT, " +
                ITEM_BAR_CODE + " TEXT, " +
                ITEM_CHECKED + " INTEGER DEFAULT 0, " +
                ITEM_GROUP + " TEXT, " +
                ITEM_CREATED_ON + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                " PRIMARY KEY (" + LIST_GUID + "," + ITEM_NAME + ")" +
                " FOREIGN KEY (" + LIST_GUID + ") REFERENCES " +
                ShoppingListTable.TABLE_NAME + " ( " + ShoppingListTable._ID + " ) " +
                " )";

        public static final String DEFAULT_SORT_ORDER = _ID + " ASC";

        public static Uri buildShoppingItemUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_SQL);
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVer, int newVer)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public static Uri buildShoppingItemsForListUri()
        {
            return null;
        }
    }
}
