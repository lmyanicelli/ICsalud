package com.luciayanicelli.icsalud.Login;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;


public class Encriptador {

    static final String TAG = "SimpleKeystoreApp";
 //   static final String CIPHER_TYPE = "RSA/ECB/PKCS1Padding";
 //   static final String CIPHER_PROVIDER = "AndroidOpenSSL";
    private final Context context;

    String aliasText;
 //   String startText;


  //  EditText startText, decryptedText, encryptedText;
    List<String> keyAliases;
 //   ListView listView;
  //  KeyRecyclerAdapter listAdapter;

    KeyStore keyStore;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Encriptador(Context context, String aliasText) {
        this.aliasText = aliasText;
        this.context = context;
      //  this.startText = startText;

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        }
        catch(Exception e) {}
        //agrego yo//
        createNewKeys();
        //
        refreshKeys();
    }

 /*   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        }
        catch(Exception e) {}
        refreshKeys();

        setContentView(R.layout.activity_main);

        View listHeader = View.inflate(this, R.layout.activity_main_header, null);
        aliasText = (EditText) listHeader.findViewById(R.id.aliasText);
        startText = (EditText) listHeader.findViewById(R.id.startText);
        decryptedText = (EditText) listHeader.findViewById(R.id.decryptedText);
        encryptedText = (EditText) listHeader.findViewById(R.id.encryptedText);

        listView = (ListView) findViewById(R.id.listView);
        listView.addHeaderView(listHeader);
        listAdapter = new KeyRecyclerAdapter(this, R.id.keyAlias);
        listView.setAdapter(listAdapter);
    }
*/



    private void refreshKeys() {
        keyAliases = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                keyAliases.add(aliases.nextElement());
            }
        }
        catch(Exception e) {}

   //     if(listAdapter != null)
     //       listAdapter.notifyDataSetChanged();
    }

  //  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  //  @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void createNewKeys() {
        //String alias = aliasText.getText().toString();
        String alias = aliasText;
        try {
            // Create new key if needed
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = null;
           //     if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    spec = new KeyPairGeneratorSpec.Builder(context)
                            .setAlias(alias)
                            .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                            .setSerialNumber(BigInteger.ONE)
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();
              //  }
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();
            }
        } catch (Exception e) {
          //  Toast.makeText(this, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
        }
        refreshKeys();
    }
/*
    public void deleteKey(final String alias) {
        AlertDialog alertDialog =new AlertDialog.Builder(context)
                .setTitle("Delete Key")
                .setMessage("Do you want to delete the key \"" + alias + "\" from the keystore?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            keyStore.deleteEntry(alias);
                            refreshKeys();
                        } catch (KeyStoreException e) {
                         //   Toast.makeText(CifrarContrasena.this,"Exception " + e.getMessage() + " occured",Toast.LENGTH_LONG).show();
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }
    */

    public String encryptString(String initialText) {
        String encryptedText;

        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(aliasText, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

          //  String initialText = startText.getText().toString();
            if(initialText.isEmpty()) {
              //  Toast.makeText(this, "Enter text in the 'Initial Text' widget", Toast.LENGTH_LONG).show();
                encryptedText = "error";
            }

            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);
            cipherOutputStream.write(initialText.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] vals = outputStream.toByteArray();
          //  encryptedText.setText(Base64.encodeToString(vals, Base64.DEFAULT));
            encryptedText = Base64.encodeToString(vals, Base64.DEFAULT);

        } catch (Exception e) {
         //   Toast.makeText(this, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
            encryptedText = "error";
        }

        return encryptedText;
    }

    public String decryptString(String encryptedText) {
        String decryptedText;
        try {
         /*   KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            output.init(Cipher.DECRYPT_MODE, privateKey);
            */
         //cambio 07/02/18

            //cambio por lo anterior
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(aliasText, null);;

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

////
            //String cipherText = encryptedText.getText().toString();
            String cipherText = encryptedText;
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            String finalText = new String(bytes, 0, bytes.length, "UTF-8");
          //  decryptedText.setText(finalText);
            decryptedText = finalText;

        } catch (Exception e) {
          //  Toast.makeText(this, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            decryptedText = "error";
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return decryptedText;
    }

/*
    public class KeyRecyclerAdapter extends ArrayAdapter<String> {

        public KeyRecyclerAdapter(Context context, int textView) {
            super(context, textView);
        }

        @Override
        public int getCount() {
            return keyAliases.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item, parent, false);

            final TextView keyAlias = (TextView) itemView.findViewById(R.id.keyAlias);
            keyAlias.setText(keyAliases.get(position));
            Button encryptButton = (Button) itemView.findViewById(R.id.encryptButton);
            encryptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    encryptString(keyAlias.getText().toString());
                }
            });
            Button decryptButton = (Button) itemView.findViewById(R.id.decryptButton);
            decryptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    decryptString(keyAlias.getText().toString());
                }
            });
            final Button deleteButton = (Button) itemView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteKey(keyAlias.getText().toString());
                }
            });

            return itemView;
        }

        @Override
        public String getItem(int position) {
            return keyAliases.get(position);
        }

    }
    */
}
