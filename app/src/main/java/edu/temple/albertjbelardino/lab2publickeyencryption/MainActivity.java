package edu.temple.albertjbelardino.lab2publickeyencryption;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static javax.crypto.Cipher.*;

public class MainActivity extends AppCompatActivity {
    Button requestKeyPairButton;
    Button encryptButton;
    Button decryptButton;

    TextView encryptedTextView;
    TextView decryptedTextView;

    EditText messageEditText;

    PublicKey publicKey;
    PrivateKey privateKey;

    ContentResolver resolver;

    // The URL used to target the content provider
    static final Uri CONTENT_URL =
            Uri.parse("content://edu.temple.albertjbelardino." +
                    "lab2publickeyencryption.KeyPairProvider/cryptographicKeyPairs");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestKeyPairButton = (Button) findViewById(R.id.requestPairButton);
        encryptButton = (Button) findViewById(R.id.encryptButton);
        decryptButton = (Button) findViewById(R.id.decryptButton);

        encryptedTextView = (TextView) findViewById(R.id.encryptedTextView);
        decryptedTextView = (TextView) findViewById(R.id.decryptedTextView);

        messageEditText = (EditText) findViewById(R.id.messageEditText);

        resolver = getContentResolver();

        requestKeyPairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = resolver.query(CONTENT_URL, null, null, null, null);
                Bundle b = cursor.getExtras();

                byte[] publicBytes = android.util.Base64.decode(b.getString("publicKey"), 0);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                KeyFactory keyFactory = null;
                try {
                    keyFactory = KeyFactory.getInstance("RSA");
                    publicKey = keyFactory.generatePublic(keySpec);
                    encryptedTextView.setText(publicKey.toString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }

                byte[] privateBytes = android.util.Base64.decode(b.getString("privateKey"), 0);
                PKCS8EncodedKeySpec keySpec1 = new PKCS8EncodedKeySpec(privateBytes);
                try {
                    keyFactory = KeyFactory.getInstance("RSA");
                    privateKey = keyFactory.generatePrivate(keySpec1);
                    decryptedTextView.setText(privateKey.toString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    decryptedTextView.setText("invalid algo");
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                    decryptedTextView.setText("invalid key spec");
                }

                requestKeyPairButton.setClickable(false);

                /*
                try {
                    keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                    keyPairGenerator.initialize(1024);
                    keyPair = keyPairGenerator.generateKeyPair();

                    KeyFactory fact = KeyFactory.getInstance("RSA");
                    pub = fact.getKeySpec(keyPair.getPublic(),
                            RSAPublicKeySpec.class);
                    priv = fact.getKeySpec(keyPair.getPrivate(),
                            RSAPrivateKeySpec.class);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }

                ContentValues values = new ContentValues();

                values.put("publicExponent", pub.getPublicExponent().toString());
                values.put("privateExponent", priv.getPrivateExponent().toString());
                values.put("modulus", pub.getModulus().toString());

                resolver.insert(CONTENT_URL, values);
                Toast.makeText(MainActivity.this, "New Key Pair Added", Toast.LENGTH_SHORT).show();
                */
            }


        });

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    encryptMessage();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }

                requestKeyPairButton.setClickable(false);
                encryptButton.setClickable(false);

                /*
                //get message from edit text
                message = messageEditText.getText().toString();
                String pub, priv, mod;
                pub = priv = mod = "";
                PrivateKey pk;

                //get key value pair from key provider
                String[] cols = new String[]{"publicExponent", "privateExponent", "modulus"};

                Cursor cursor = resolver.query(CONTENT_URL, cols, null, null, null);

                if(cursor.moveToFirst()){

                    do{
                        pub = cursor.getString(cursor.getColumnIndex("publicExponent"));
                        priv = cursor.getString(cursor.getColumnIndex("privateExponent"));
                        mod = cursor.getString(cursor.getColumnIndex("modulus"));
                    }while (cursor.moveToNext());

                }

                //encrypt message
                try {
                    encryptMessage(message, pub, priv, mod);
                } catch (NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException
                        | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                */
            }
        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    decryptMessage();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                /*
                //get message from encrypted message text view
                String message = encryptedTextView.getText().toString();
                //get key value pair from key provider
                String pub, priv, mod;
                pub = priv = mod = "";

                //get key value pair from key provider
                String[] cols = new String[]{"publicExponent", "privateExponent", "modulus"};

                Cursor cursor = resolver.query(CONTENT_URL, cols, null, null, null);

                if(cursor.moveToFirst()){

                    do{
                        pub = cursor.getString(cursor.getColumnIndex("publicExponent"));
                        priv = cursor.getString(cursor.getColumnIndex("privateExponent"));
                        mod = cursor.getString(cursor.getColumnIndex("modulus"));
                    }while (cursor.moveToNext());
                }
                //decrypt message
                try {
                    decryptMessage(message, pub, priv, mod);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
                */
            }
        });
    }

    private void decryptMessage() throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        byte[] s = cipher.doFinal(android.util.Base64.decode(encryptedTextView.getText().toString(), Base64.NO_PADDING));
        decryptedTextView.setText(android.util.Base64.encodeToString(s, 0));
        requestKeyPairButton.setClickable(true);
        encryptButton.setClickable(true);
    }
    /*
    private void decryptMessage(String message, String pub, String priv, String mod)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        BigInteger publicExp = new BigInteger(pub);
        BigInteger privateExp = new BigInteger(priv);
        BigInteger modulus = new BigInteger(mod);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExp);
        PublicKey publicKey = (KeyFactory.getInstance("RSA")).generatePublic(spec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        decryptedTextView.setText(cipher.doFinal(message.getBytes()).toString());
    }
    */
    /*
    public void displayPubAndPriv(){

        String[] cols = new String[]{"publicExponent", "privateExponent", "modulus"};

        Cursor cursor = resolver.query(CONTENT_URL, cols, null, null, null);

        if(cursor.moveToFirst()){

            do{
                encryptedTextView.setText(cursor.getString(cursor.getColumnIndex("publicExponent")));
                decryptedTextView.setText(cursor.getString(cursor.getColumnIndex("privateExponent")));
                messageEditText.setText(cursor.getString(cursor.getColumnIndex("modulus")));
            }while (cursor.moveToNext());

        }
    }
    */

    public void encryptMessage() throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] s = cipher.doFinal(android.util.Base64.decode(messageEditText.getText().toString(), Base64.NO_PADDING));
            encryptedTextView.setText(android.util.Base64.encodeToString(s, 0));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            encryptedTextView.setText("no such algorithm exception");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            encryptedTextView.setText("no such padding exception");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            encryptedTextView.setText("invalid key exception");
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }
    /*
    public void encryptMessage(String message, String pubExp, String privExp, String mod)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        BigInteger publicExp = new BigInteger(pubExp);
        BigInteger privateExp = new BigInteger(privExp);
        BigInteger modulus = new BigInteger(mod);

        RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, privateExp);
        PrivateKey privateKey = (KeyFactory.getInstance("RSA")).generatePrivate(spec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        encryptedTextView.setText(cipher.doFinal(message.getBytes()).toString());
    }
    */

    /*
    public static PublicKey stringToPublicKey(byte[] key){
        try{
            byte[] byteKey = Base64.decode(key, Base64.DEFAULT);
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }



    public static PrivateKey stringToPrivateKey(byte[] key){
        try{
            byte[] byteKey = Base64.decode(key, Base64.DEFAULT);
            X509EncodedKeySpec X509privateKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePrivate(X509privateKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }*/
}
