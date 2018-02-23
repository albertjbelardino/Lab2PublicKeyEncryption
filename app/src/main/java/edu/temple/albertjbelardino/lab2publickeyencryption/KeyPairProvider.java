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

            final Bundle bundle = new Bundle();

            bundle.putString("publicKey", android.util.Base64.encodeToString(publicKey.getEncoded(), 0));
            bundle.putString("privateKey", android.util.Base64.encodeToString(privateKey.getEncoded(), 0));

            Cursor cursor = query(CONTENT_URL, null, null, null, null);
            cursor.setExtras(bundle);

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
