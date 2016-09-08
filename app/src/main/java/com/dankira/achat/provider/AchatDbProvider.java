package com.dankira.achat.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class AchatDbProvider extends ContentProvider
{
    private static final String LOG_TAG = AchatDbProvider.class.getSimpleName();

    private static final int SHOPPING_LIST = 0;
    private static final int SHOPPING_LIST_ID = 1;
    private static final int SHOPPING_ITEM = 2;
    private static final int SHOPPING_ITEM_ID = 3;

    private static final String shoppingListByIdSelection = AchatDbContracts.ShoppingListTable.TABLE_NAME + "." +
            AchatDbContracts.ShoppingListTable.LIST_GUID + "= ?";

    private static final String shoppingItemByIdSelection = AchatDbContracts.ShoppingItemTable.TABLE_NAME + "." +
            AchatDbContracts.ShoppingItemTable._ID + "= ?";
    private static final SQLiteQueryBuilder shoppingListQueryBuilder;
    private static final SQLiteQueryBuilder shoppingItemQueryBuilder;
    private static final UriMatcher uriMatcher;

    static
    {
        shoppingItemQueryBuilder = new SQLiteQueryBuilder();
        shoppingListQueryBuilder = new SQLiteQueryBuilder();

        shoppingListQueryBuilder.setTables(AchatDbContracts.ShoppingListTable.TABLE_NAME);
        shoppingItemQueryBuilder.setTables(AchatDbContracts.ShoppingItemTable.TABLE_NAME);
    }

    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AchatDbContracts.CONTENT_AUTHORITY, AchatDbContracts.PATH_SHOPPING_LIST, SHOPPING_LIST);
        uriMatcher.addURI(AchatDbContracts.CONTENT_AUTHORITY, AchatDbContracts.PATH_SHOPPING_LIST + "/*", SHOPPING_LIST_ID);
        uriMatcher.addURI(AchatDbContracts.CONTENT_AUTHORITY, AchatDbContracts.PATH_SHOPPING_ITEM, SHOPPING_ITEM);
        uriMatcher.addURI(AchatDbContracts.CONTENT_AUTHORITY, AchatDbContracts.PATH_SHOPPING_ITEM + "/*", SHOPPING_ITEM_ID);
    }

    private AchatDbHelper dbHelper;

    @Override
    public boolean onCreate()
    {
        dbHelper = new AchatDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor returnCursor;
        int matchedUri = uriMatcher.match(uri);

        switch (matchedUri)
        {
            case SHOPPING_LIST:
                returnCursor = getShoppingLists(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case SHOPPING_LIST_ID:
                returnCursor = getShoppingListById(uri, projection, sortOrder);
                break;
            case SHOPPING_ITEM:
                returnCursor = getShoppingItems(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case SHOPPING_ITEM_ID:
                returnCursor = getShoppingItemById(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI passed : " + uri);
        }

        ContentResolver cr = getContext().getContentResolver();

        if (cr != null)
        {
            returnCursor.setNotificationUri(cr, uri);
        }

        return returnCursor;
    }

    private Cursor getShoppingLists(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        String t = AchatDbContracts.ShoppingListTable.TABLE_NAME + ".";

        String SELECT_LIST = "SELECT " +
                t + AchatDbContracts.ShoppingListTable._ID + " AS " + AchatDbContracts.ShoppingListTable._ID + "," +
                t + AchatDbContracts.ShoppingListTable.LIST_TITLE + " AS " + AchatDbContracts.ShoppingListTable.LIST_TITLE + "," +
                t + AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION + " AS " + AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION + "," +
                t + AchatDbContracts.ShoppingListTable.LIST_GUID + " AS " + AchatDbContracts.ShoppingListTable.LIST_GUID + "," +
                t + AchatDbContracts.ShoppingListTable.LIST_CREATED_ON + " AS " + AchatDbContracts.ShoppingListTable.LIST_CREATED_ON + "," +
                t + AchatDbContracts.ShoppingListTable.LIST_SHARE_STATUS + " AS " + AchatDbContracts.ShoppingListTable.LIST_SHARE_STATUS + "," +
                "COUNT(" + AchatDbContracts.ShoppingItemTable.TABLE_NAME + "." +
                AchatDbContracts.ShoppingItemTable._ID + ") AS item_count" +
                " FROM " +
                AchatDbContracts.ShoppingListTable.TABLE_NAME + " LEFT JOIN " +
                AchatDbContracts.ShoppingItemTable.TABLE_NAME + " ON (" +
                t + AchatDbContracts.ShoppingListTable.LIST_GUID + "=" +
                AchatDbContracts.ShoppingItemTable.TABLE_NAME + "." +
                AchatDbContracts.ShoppingItemTable.LIST_GUID + ")" +
                " GROUP BY " +
                t + AchatDbContracts.ShoppingListTable.LIST_GUID;

        return dbHelper.getReadableDatabase().rawQuery(SELECT_LIST, selectionArgs);
    }

    private Cursor getShoppingListById(Uri uri, String[] projection, String sortOrder)
    {
        String listId = uri.getPathSegments().get(1);

        return shoppingListQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                shoppingListByIdSelection,
                new String[]{listId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getShoppingItems(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        return shoppingItemQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getShoppingItemById(Uri uri, String[] projection, String sortOrder)
    {
        String itemId = uri.getPathSegments().get(1);
        return shoppingItemQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                shoppingItemByIdSelection,
                new String[]{itemId},
                null,
                null,
                sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        final int matchedUri = uriMatcher.match(uri);

        switch (matchedUri)
        {
            case SHOPPING_LIST:
                return AchatDbContracts.ShoppingListTable.CONTENT_TYPE;
            case SHOPPING_LIST_ID:
                return AchatDbContracts.ShoppingListTable.CONTENT_ITEM_TYPE;
            case SHOPPING_ITEM:
                return AchatDbContracts.ShoppingItemTable.CONTENT_TYPE;
            case SHOPPING_ITEM_ID:
                return AchatDbContracts.ShoppingItemTable.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI passed : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues)
    {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int matchedUri = uriMatcher.match(uri);

        Uri returnUri;
        long idInserted = -1;
        switch (matchedUri)
        {
            case SHOPPING_LIST:
            {
                idInserted = db.insert(AchatDbContracts.ShoppingListTable.TABLE_NAME, null, contentValues);
                if (idInserted > 0)
                {
                    returnUri = AchatDbContracts.ShoppingListTable.buildShoppingListUri(idInserted);
                }
                else
                {
                    Log.e(LOG_TAG, "Failed to insert row into " + AchatDbContracts.ShoppingListTable.TABLE_NAME);
                    throw new android.database.SQLException("Failed to insert row into " + AchatDbContracts.ShoppingListTable.TABLE_NAME);
                }
                break;
            }
            case SHOPPING_ITEM:
            {
                idInserted = db.insert(AchatDbContracts.ShoppingItemTable.TABLE_NAME, null, contentValues);
                if (idInserted > 0)
                {
                    returnUri = AchatDbContracts.ShoppingItemTable.buildShoppingItemUri(idInserted);
                }
                else
                {
                    Log.e(LOG_TAG, "Failed to insert row into " + AchatDbContracts.ShoppingItemTable.TABLE_NAME);
                    throw new android.database.SQLException("Failed to insert row into " + AchatDbContracts.ShoppingItemTable.TABLE_NAME);
                }
                break;
            }
            default:
            {
                Log.e(LOG_TAG,"Unsupported operation due to unknown uri: " + uri);
                throw new UnsupportedOperationException("Unsupported operation due to unknown uri: " + uri);
            }
        }

        if (idInserted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String where, String[] whereArgs)
    {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int matchedUri = uriMatcher.match(uri);
        int deleteCount = -1;

        switch (matchedUri)
        {
            case SHOPPING_LIST:
            {
                // TODO: 7/26/2016 For db integrity, you will have to delete the items with this shopping list id first.
                deleteCount = db.delete(AchatDbContracts.ShoppingListTable.TABLE_NAME, where, whereArgs);
                break;
            }
            case SHOPPING_ITEM:
            {
                deleteCount = db.delete(AchatDbContracts.ShoppingItemTable.TABLE_NAME, where, whereArgs);
                break;
            }
            default:
            {
                Log.e(LOG_TAG,"Unsupported operation due to unknown uri: " + uri);
                throw new UnsupportedOperationException("Unsupported operation due to unknown uri: " + uri);
            }
        }
        if (deleteCount > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteCount;

    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int matchedUri = uriMatcher.match(uri);
        int updateCount = -1;

        switch (matchedUri)
        {
            case SHOPPING_LIST:
            {
                updateCount = db.update(AchatDbContracts.ShoppingListTable.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }
            case SHOPPING_ITEM:
            {
                updateCount = db.update(AchatDbContracts.ShoppingItemTable.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }
            default:
            {
                Log.e(LOG_TAG,"Unsupported operation due to unknown uri: " + uri);
                throw new UnsupportedOperationException("Unsupported operation due to unknown uri: " + uri);
            }
        }
        if (updateCount > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int matchedUri = uriMatcher.match(uri);


        switch (matchedUri)
        {
            case SHOPPING_LIST:
            {
                int insertCount = 0;
                db.beginTransaction();

                try
                {
                    for (ContentValues v : values)
                    {
                        long _id = -1;
                        try
                        {
                            _id = db.insertOrThrow(AchatDbContracts.ShoppingListTable.TABLE_NAME, null, v);
                        }
                        catch (Exception e)
                        {
                            Log.d(LOG_TAG,"An exception occurred while inserting into"+ AchatDbContracts.ShoppingListTable.TABLE_NAME+" table. Exception :"+ e.getMessage());
                        }

                        if (_id != -1)
                            insertCount++;
                    }
                    db.setTransactionSuccessful();
                }
                finally
                {
                    db.endTransaction();
                }

                if (insertCount > 0)
                    getContext().getContentResolver().notifyChange(uri, null);

                return insertCount;
            }
            case SHOPPING_ITEM:
            {
                int insertCount = 0;
                db.beginTransaction();

                try
                {
                    for (ContentValues v : values)
                    {
                        long _id = -1;
                        try
                        {
                            _id = db.insertOrThrow(AchatDbContracts.ShoppingItemTable.TABLE_NAME, null, v);
                        }
                        catch (Exception e)
                        {
                            Log.d(LOG_TAG,"An exception occurred while inserting into"+ AchatDbContracts.ShoppingItemTable.TABLE_NAME+" table. Exception :"+ e.getMessage());
                        }
                        if (_id != -1)
                            insertCount++;

                    }
                    db.setTransactionSuccessful();
                }
                finally
                {
                    db.endTransaction();
                }
                if (insertCount > 0)
                    getContext().getContentResolver().notifyChange(uri, null);

                return insertCount;
            }
            default:
            {
                return super.bulkInsert(uri, values);
            }
        }

    }
}
