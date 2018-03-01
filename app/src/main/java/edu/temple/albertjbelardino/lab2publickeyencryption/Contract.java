package edu.temple.albertjbelardino.lab2publickeyencryption;

import android.net.Uri;

/**
 * Created by albertjbelardino on 3/1/2018.
 */

public abstract class Contract {
    static final String PROVIDER_NAME = "edu.temple.albertjbelardino.lab2publickeyencryption.KeyPairProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/cryptographicKeyPairs";
    static final Uri CONTENT_URL = Uri.parse(URL);
}
