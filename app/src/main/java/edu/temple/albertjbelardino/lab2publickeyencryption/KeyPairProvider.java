package edu.temple.albertjbelardino.lab2publickeyencryption;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by albertjbelardino on 2/10/2018.
 */

public class KeyPairProvider extends ContentProvider {

    static final String PROVIDER_NAME = "edu.temple.albertjbelardino.lab2publickeyencryption.KeyPairProvider";

    static final String URL = "content://" + PROVIDER_NAME + "/cryptographicKeyPairs";
    static final Uri CONTENT_URL = Uri.parse(URL);

    static final String publicExponent = "publicExponent";
    static final String privateExponent = "privateExponent";
    static final String modulus = "modulus";
    static final int uriCode = 1;

    private static HashMap<String, String> values;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "cryptographicKeyPairs", uriCode);
    }

    private SQLiteDatabase sqlDB;
    static final String DATABASE_NAME = "cryptographicKeyPairs";
    static final String TABLE_NAME = "keyPairs";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME
            + " (publicExponent TEXT NOT NULL PRIMARY KEY, "
            + " privateExponent TEXT NOT NULL, " +
            " modulus TEXT NOT NULL);";


    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();
        if (sqlDB != null) {
            return true;
        }
        return false;
    }
        @Override
        public Cursor query(Uri uri, String[] cols, String selection, String[] selectionArgs, String sortOrder) {

            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

            queryBuilder.setTables(TABLE_NAME);

            switch (uriMatcher.match(uri)) {
                case uriCode:
                    queryBuilder.setProjectionMap(values);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            Cursor cursor = queryBuilder.query(sqlDB, cols, selection, selectionArgs, null,
                    null, sortOrder);

            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long rowID = sqlDB.insert(TABLE_NAME, null, values);

        if (rowID > 0) {

            Uri _uri = ContentUris.withAppendedId(CONTENT_URL, rowID);

            getContext().getContentResolver().notifyChange(_uri, null);

            return _uri;
        }
        Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqlDB) {
            sqlDB.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqlDB, int oldVersion, int newVersion) {
            sqlDB.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqlDB);
        }
    }
}
