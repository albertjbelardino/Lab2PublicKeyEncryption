package edu.temple.albertjbelardino.lab2publickeyencryption;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.spongycastle.util.io.pem.PemGenerationException;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemObjectGenerator;
import org.spongycastle.util.io.pem.PemWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static javax.crypto.Cipher.getInstance;

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

    PemObject messagePemObj;
    PemObject pubKeyPemObj;

    NdefMessage ndefMessage;

    PendingIntent pi;
    Intent intent;

    IntentFilter[] intentFiltersArray;

    // The URL used to target the content provider
    static final Uri CONTENT_URL = Contract.CONTENT_URL;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NfcAdapter nfcAdapter;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter != null)
            nfcAdapter.setNdefPushMessageCallback(null, this, this);

        pi = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        intentFiltersArray = new IntentFilter[] {ndefFilter};

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

                byte[] publicBytes = Base64.decode(b.getString("publicKey"), Base64.NO_PADDING);
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

                byte[] privateBytes = Base64.decode(b.getString("privateKey"), Base64.NO_PADDING);
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
            }
        });
    }

    private void decryptMessage() throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        byte[] s = cipher.doFinal(Base64.decode(encryptedTextView.getText().toString(), Base64.NO_PADDING));
        decryptedTextView.setText(Base64.encodeToString(s, Base64.NO_PADDING));
        requestKeyPairButton.setClickable(true);
        encryptButton.setClickable(true);
    }

    public void encryptMessage() throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] s = cipher.doFinal(Base64.decode(encryptedTextView.getText().toString(), Base64.NO_PADDING));
            encryptedTextView.setText(Base64.encodeToString(s, Base64.NO_PADDING));
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

    public String generatePubKeyPEM() {
        StringWriter sw = new StringWriter();
        PemWriter pw = new PemWriter(sw);
        try {
            pw.writeObject(new PemObjectGenerator() {
                @Override
                public PemObject generate() throws PemGenerationException {
                    pubKeyPemObj = new PemObject("PublicKey", publicKey.getEncoded());
                    return pubKeyPemObj;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sw.toString();
    }

    public String generateMessagePEM() {
        StringWriter sw = new StringWriter();
        PemWriter pw = new PemWriter(sw);
        try {
            pw.writeObject(new PemObjectGenerator() {
                @Override
                public PemObject generate() throws PemGenerationException {
                    messagePemObj = new PemObject("Message", encryptedTextView.getText().toString().getBytes());
                    return messagePemObj;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sw.toString();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (NfcAdapter.getDefaultAdapter(this) != null) {
            NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pi, intentFiltersArray, null);
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                processBeam(getIntent());
            }
        }
    }

    public void processBeam(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        if (rawMsgs != null){
            NdefMessage msg = (NdefMessage) rawMsgs[0];

            String messagePem = new String(msg.getRecords()[1].getPayload());
            String pubKeyPem = new String(msg.getRecords()[0].getPayload());
        }
    }

    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { NdefRecord.createMime(
                        "application/edu.temple.androidbeam", generatePubKeyPEM().getBytes()),
                        NdefRecord.createMime(
                                "application/edu.temple.androidbeam", generatePubKeyPEM().getBytes())
                });

        return msg;
    }


}