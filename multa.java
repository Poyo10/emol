package ar.com.nowait.emedido;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
//import com.android.support.media.ExifInterface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class multa extends AppCompatActivity {


    SoapObject resSoap;
    int RetValWS =0;
    String RetId = "";
    String RetTxt = "";

    private String DirFotos = "EmolPics";
    private String fNamePath = "";
    private String fNameName = "";
    private String FotoEnc64 = "";

    private final int PHOTO_CODE=100;
    private final int SELECT_PICTURE=200;

    private ImageView imageView;
    private TextView tvDominio;
    private Button btAceptar;
    private View mProgressView;

    String gEmail = "";
    String gPass ="";
    int gIdUsu = 0;
    int gIdTipoUsu = 0;
    String gNombreUsu = "";
    int gIdMuni = 0;
    String gDominio = "";
    String gLatitud = "";
    String gLongitud = "";


/*    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private Boolean mRequestingLocationUpdates;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multa);

        imageView= (ImageView) findViewById(R.id.setPicture);
        tvDominio = (TextView) findViewById(R.id.tvDominio);
        btAceptar = (Button) findViewById(R.id.btAceptar);
        mProgressView = findViewById(R.id.login_progress);

        // Quitar para produccion ojota.
        gEmail = "fabio_d_rossi@hotmail.com";
        gPass = "123456";
        gIdMuni = 1;
        gIdUsu = 1;
        gIdTipoUsu =1;
        gNombreUsu = "Fabio";
        // Quitar para produccion.

        gEmail = getIntent().getStringExtra("tEmail");
        gPass = getIntent().getStringExtra("tPass");
        gIdMuni = Integer.parseInt(getIntent().getStringExtra("tIdMuni"));
        gIdUsu = Integer.parseInt(getIntent().getStringExtra("tIdUsu"));
        gIdTipoUsu = Integer.parseInt(getIntent().getStringExtra("tIdTipoUsu"));
        gNombreUsu = getIntent().getStringExtra("tNombreUsu");
        gLatitud =  getIntent().getStringExtra("tLat");
        gLongitud =  getIntent().getStringExtra("tLong");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String[] letra = {"Estacionado en lugar prohibido","Doble fila","Frente Garage","Circular en contra mano","Giro en U"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));


        // Esto es para evitar un error al ejecutar el intent de la camara.
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        openCamera();



    }

    private void openCamera() {
        // Creo la carpeta.
        File file = new File(Environment.getExternalStorageDirectory(), DirFotos);
        file.mkdirs();

        //Defino Ruta y nombre de archivo.
        fNamePath = Environment.getExternalStorageDirectory() + File.separator + DirFotos;
        fNameName = CrearFName();
        File newFile = new File( fNamePath + file.separator + "tmp_" + fNameName);

         //LLamo a la app camara.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Uri.fromFile(newFile)==null){}
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        startActivityForResult(intent, 1); // PHOTO_CODE);
    }

    private String CrearFName(){
        Calendar c = new GregorianCalendar();
        String RetVal="";

        RetVal = String.format("%04d", c.get(Calendar.YEAR) );
        RetVal = RetVal + String.format("%02d", c.get(Calendar.MONTH) + 1 );
        RetVal = RetVal + String.format("%02d", c.get(Calendar.DAY_OF_MONTH) );
        RetVal = RetVal + String.format("%02d", c.get(Calendar.HOUR_OF_DAY) );
        RetVal = RetVal + String.format("%02d", c.get(Calendar.MINUTE) );
        RetVal = RetVal + String.format("%02d", c.get(Calendar.SECOND) );
        RetVal = RetVal + "_" + String.format("%02d", gIdUsu );
        return RetVal + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);



        //if (resultCode == RESULT_OK){
            decodeBitmap(fNamePath + File.separator + "tmp_" + fNameName);
      /*  }else {
           Toast.makeText(multa.this,"Multa cancelada." , Toast.LENGTH_LONG).show();
           finish();
        }*/
    }

    private void decodeBitmap(String dir) {
        Bitmap bitmap;
        bitmap= BitmapFactory.decodeFile(dir);

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        int Angulo=0;
        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                Angulo=90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                Angulo=180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                Angulo=270;
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                Angulo=0;
            default:
                Angulo=0;
                break;
        }

        bitmap = rotateImage(bitmap,Angulo);

        imageView.setImageBitmap(bitmap);

        bitmap=redimensionarImagenMaximo(bitmap,bitmap.getWidth()/3, bitmap.getHeight()/3);



        try {
            FileOutputStream fOut = new FileOutputStream( fNamePath + File.separator + fNameName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        FotoEnc64=getEncoded64ImageStringFromBitmap(bitmap);
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    //Este método se ejecutará cuando se presione el botón cancelar
    public void Cancelar(View view) {
    finish();
    }

    //Este método se ejecutará cuando se presione el botón aceptar
    public void Aceptar(View view) {
        //btAceptar.setVisibility(view.INVISIBLE);
        imageView.setVisibility((View.INVISIBLE));
        mProgressView.setVisibility( View.VISIBLE );
        btAceptar.setEnabled(false);
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }


    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {}

        @Override
        protected Void doInBackground(Void... params) {  // LLamo al WS
            PedirWSMulta();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {  // Retorno del WS

            if ( Integer.parseInt( RetId)<=0) {
                Toast.makeText(getBaseContext(), RetTxt , Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getBaseContext(), "Multa registrada.", Toast.LENGTH_SHORT).show();
                finish();
            };
            // String lsX= RetTxt;
        }

        @Override
        protected void onCancelled() {
            //SetCtrl(true);
        }

    }

    public void PedirWSMulta() {

        String SOAP_ACTION = "http://nowait.com.ar/SetMulta";
        String METHOD_NAME = "SetMulta";
        String NAMESPACE = "http://nowait.com.ar/";
        String URL = "http://nowait.com.ar:8888/wsEme.asmx";

        try {

            String gLat = Global.gGeoPoints.get(Global.gGeoPoints.size()-1).getLatitud();
            String gLon = Global.gGeoPoints.get(Global.gGeoPoints.size()-1).getLongitud();

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("tUsu", gEmail);
            Request.addProperty("tPass", gPass);
            Request.addProperty("tIdUsu", gIdUsu );
            Request.addProperty("tDominio", gDominio );
            Request.addProperty("tIdMuni", gIdMuni);
            Request.addProperty("tfName", fNameName);
            Request.addProperty("tFotoEnc64", FotoEnc64);
            Request.addProperty("tLatitud", gLat);
            Request.addProperty("tLongitud", gLon);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);

            resSoap =(SoapObject)soapEnvelope.getResponse();
            RetId=resSoap.getProperty("Id").toString();
            RetTxt=resSoap.getProperty("Descri").toString();
            RetValWS=0;
        } catch (Exception ex) {
            RetId="-1";
            RetTxt="Error accediendo al servidor.";
            RetValWS=-1;
        }

    }

    public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth){
        //Redimensionamos
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeigth) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height,  matrix,  false);
    }


    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }



}
