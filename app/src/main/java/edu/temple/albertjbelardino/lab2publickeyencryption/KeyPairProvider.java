package edu.temple.albertjbelardino.lab2publickeyencryption;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;

/**
 * Created by albertjbelardino on 2/10/2018.
 */

public class KeyPairProvider extends ContentProvider {

    static final String PROVIDER_NAME = "edu.temple.albertjbelardino.lab2publickeyencryption.KeyPairProvider";

    static final String URL = "content://" + PROVIDER_NAME + "/cryptographicKeyPairs";
    static final Uri CONTENT_URL = Uri.parse(URL);

    static final int uriCode = 1;

    private static HashMap<String, String> values;

    @Override
    public boolean onCreate() {
        return true;
    }
        @Override
        public Cursor query(Uri uri, final String[] cols, String selection, String[] selectionArgs, String sortOrder) {
            final KeyPair keyPair = generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            final RSAPrivateKeySpec priv;
            final RSAPublicKeySpec pub;
            final Bundle bundle = new Bundle();

            bundle.putString("publicKey", android.util.Base64.encodeToString(publicKey.getEncoded(), 0));
            bundle.putString("privateKey", android.util.Base64.encodeToString(privateKey.getEncoded(), 0));

            KeyFactory fact;

            try {

                fact = KeyFactory.getInstance("RSA");

                priv = fact.getKeySpec(keyPair.getPrivate(),
                        RSAPrivateKeySpec.class);
                pub = fact.getKeySpec(keyPair.getPublic(),
                        RSAPublicKeySpec.class);


            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }


            final Cursor cursor = new Cursor() {
                @Override
                public int getCount() {
                    return 0;
                }

                @Override
                public int getPosition() {
                    return 0;
                }

                @Override
                public boolean move(int i) {
                    return false;
                }

                @Override
                public boolean moveToPosition(int i) {
                    return false;
                }

                @Override
                public boolean moveToFirst() {
                    return false;
                }

                @Override
                public boolean moveToLast() {
                    return false;
                }

                @Override
                public boolean moveToNext() {
                    return false;
                }

                @Override
                public boolean moveToPrevious() {
                    return false;
                }

                @Override
                public boolean isFirst() {
                    return false;
                }

                @Override
                public boolean isLast() {
                    return false;
                }

                @Override
                public boolean isBeforeFirst() {
                    return false;
                }

                @Override
                public boolean isAfterLast() {
                    return false;
                }

                @Override
                public int getColumnIndex(String s) {
                    return 0;
                }

                @Override
                public int getColumnIndexOrThrow(String s) throws IllegalArgumentException {
                    return 0;
                }

                @Override
                public String getColumnName(int i) {
                    return null;
                }

                @Override
                public String[] getColumnNames() {
                    return new String[0];
                }

                @Override
                public int getColumnCount() {
                    return 0;
                }

                @Override
                public byte[] getBlob(int i) {
                    return new byte[0];
                }

                @Override
                public String getString(int i) {
                    return null;
                }

                @Override
                public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {

                }

                @Override
                public short getShort(int i) {
                    return 0;
                }

                @Override
                public int getInt(int i) {
                    return 0;
                }

                @Override
                public long getLong(int i) {
                    return 0;
                }

                @Override
                public float getFloat(int i) {
                    return 0;
                }

                @Override
                public double getDouble(int i) {
                    return 0;
                }

                @Override
                public int getType(int i) {
                    return 0;
                }

                @Override
                public boolean isNull(int i) {
                    return false;
                }

                @Override
                public void deactivate() {

                }

                @Override
                public boolean requery() {
                    return false;
                }

                @Override
                public void close() {

                }

                @Override
                public boolean isClosed() {
                    return false;
                }

                @Override
                public void registerContentObserver(ContentObserver contentObserver) {

                }

                @Override
                public void unregisterContentObserver(ContentObserver contentObserver) {

                }

                @Override
                public void registerDataSetObserver(DataSetObserver dataSetObserver) {

                }

                @Override
                public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

                }

                @Override
                public void setNotificationUri(ContentResolver contentResolver, Uri uri) {

                }

                @Override
                public Uri getNotificationUri() {
                    return null;
                }

                @Override
                public boolean getWantsAllOnMoveCalls() {
                    return false;
                }

                @Override
                public void setExtras(Bundle bundle) {

                }

                @Override
                public Bundle getExtras() {
                    return bundle;
                }

                @Override
                public Bundle respond(Bundle bundle) {
                    return null;
                }
            };

            return cursor;
        }

    public KeyPair generateKeyPair() {

        KeyPair keyPair = null;

        try {
            KeyPairGenerator keyPairGenerator;

            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            keyPair = keyPairGenerator.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return keyPair;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
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

}
