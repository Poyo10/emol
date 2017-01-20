package ar.com.nowait.emedido;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SoapObject resSoap;
    String RetId = "";
    String RetTxt = "";
    String RetFrente = "";
    String RetDire = "";
    int RetValWS =0;
    String gEmail = "";
    String gPass ="";
    int gIdUsu = 0;
    int gIdTipoUsu = 0;
    String gNombreUsu = "";
    int gIdMuni = 0;

    private View mProgressView;
    private EditText tbDomi;
    private Button cbConsultar;
    private ListView list;

    ArrayList<Lista_entrada> datos = new ArrayList<Lista_entrada>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mProgressView = findViewById(R.id.consu_progress);
        cbConsultar = (Button) findViewById(R.id.cbConsultar);
        tbDomi=(EditText) findViewById(R.id.tbDominio);

        gEmail = getIntent().getStringExtra("tEmail");
        gPass = getIntent().getStringExtra("tPass");
        gIdMuni = Integer.parseInt(getIntent().getStringExtra("tIdMuni"));
        gIdUsu = Integer.parseInt(getIntent().getStringExtra("tIdUsu"));
        gIdTipoUsu = Integer.parseInt(getIntent().getStringExtra("tIdTipoUsu"));
        gNombreUsu = getIntent().getStringExtra("tNombreUsu");

        list = (ListView) findViewById(R.id.lista);
        list.setAdapter(new Lista_adaptador(this, R.layout.entrada, datos){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    TextView texto_superior_entrada = (TextView) view.findViewById(R.id.tvDominio);
                    if (texto_superior_entrada != null)
                        texto_superior_entrada.setText(((Lista_entrada) entrada).get_textoEncima());

                    TextView texto_inferior_entrada = (TextView) view.findViewById(R.id.tvHora);
                    if (texto_inferior_entrada != null)
                        texto_inferior_entrada.setText(((Lista_entrada) entrada).get_textoDebajo());

                    TextView texto_retval = (TextView) view.findViewById(R.id.tvRetVal);
                    if (texto_retval != null)
                    texto_retval.setText(((Lista_entrada) entrada).get_RetVal());

                    //ImageView imagen_entrada = (ImageView) view.findViewById(R.id.imageView_imagen);
                    //if (imagen_entrada != null)
                    //    imagen_entrada.setImageResource(((Lista_entrada) entrada).get_idImagen());
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> pariente, View view, int posicion, long id) {
                Lista_entrada elegido = (Lista_entrada) pariente.getItemAtPosition(posicion);
                //CharSequence texto = elegido.get_RetVal();
                String rv =elegido.get_RetVal();
                final String lDomi = elegido.get_textoEncima();
                if ( ! rv.startsWith ("Correcto")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setMessage("Desea generar una Multa para el dominio " + elegido.get_textoEncima() );
                    alertDialog.setTitle("Infracción.");
                    alertDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {

                            Intent i = new Intent( MainActivity.this , multa.class );
                            i.putExtra("tIdUsu",gIdUsu);
                            i.putExtra("tNombreUsu",gNombreUsu);
                            i.putExtra("tIdTipoUsu",gIdTipoUsu);
                            i.putExtra("tIdMuni",gIdMuni);
                            i.putExtra("tEmail",gEmail);
                            i.putExtra("tPass",gPass);
                            i.putExtra("tDominio",lDomi);

                            startActivity(i);

                            //finish();

                            //código Java si se ha pulsado sí
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //código java si se ha pulsado no
                        }
                    });
                    alertDialog.show();

                return true;
                }
                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "este es " + list.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                Lista_entrada elegido = (Lista_entrada) parent.getItemAtPosition(position);
                tbDomi.setText(elegido.get_textoEncima());
            }
        });


    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {}

        @Override
        protected Void doInBackground(Void... params) {
            PedirWSCli();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            SetCtrl(true);

            if (RetValWS<0) {
                Toast.makeText(getBaseContext(), "Error en la respuesta del servidor.", Toast.LENGTH_SHORT).show();
            };
            String lsX= RetTxt;
            if (! RetFrente.equals("")){
                lsX = lsX +  "\n" + RetFrente +  "\n" + RetDire;
            }

            AddLine(tbDomi.getText().toString(), lsX);

        }

        @Override
        protected void onCancelled() {
            SetCtrl(true);
        }

    }

    public void PedirWSCli() {

        String SOAP_ACTION = "http://nowait.com.ar/CheckDominio";
        String METHOD_NAME = "CheckDominio";
        String NAMESPACE = "http://nowait.com.ar/";
        String URL = "http://emol.nowait.com.ar:8080/wsEme.asmx";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            String mDominio= tbDomi.getText().toString();
            Request.addProperty("tUsu", gEmail);
            Request.addProperty("tPass", gPass);
            Request.addProperty("tDominio",mDominio);
            Request.addProperty("tIdZona", "1");

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);

            resSoap =(SoapObject)soapEnvelope.getResponse();
            RetId=resSoap.getProperty("Id").toString();
            RetTxt=resSoap.getProperty("Descri").toString();
            RetFrente = "";
            RetDire = "";
            if (resSoap.getPropertyCount()>2){
                RetFrente = "Frentista en Zona: " + resSoap.getProperty("FrenIdZona").toString();
                RetFrente = RetFrente + " de "  + resSoap.getProperty("FrenDesde").toString() + " a " +resSoap.getProperty("FrenHasta").toString();
                RetDire = resSoap.getProperty("FrenDire").toString();
            }

            RetValWS=0;

        } catch (Exception ex) {
            RetId="-1";
            RetTxt="Error accediendo al servidor.";
            RetFrente = "";
            RetDire = "";

            RetValWS=-1;
        }

    }

    private void AddLine(String tDomi,  String tTxt ){
        String tHora="";
        Calendar c = new GregorianCalendar();
        tHora = String.format("%02d", c.get(Calendar.HOUR) );
        tHora = tHora + ":" + String.format("%02d", c.get(Calendar.MINUTE) );

        datos.add(0,new Lista_entrada(tDomi,tHora, tTxt));

        ((Lista_adaptador)list.getAdapter()).notifyDataSetChanged();

        if (!RetId.equals("-1")){
            tbDomi.setText("");
        }
    }

    //Este método se ejecutará cuando se presione el botón
    public void Consultar(View view) {
        RetId="";
        RetTxt="";
        RetFrente = "";
        RetDire = "";

        tbDomi = (EditText)this.findViewById(R.id.tbDominio);
        String lsDomi=tbDomi.getText().toString().toUpperCase();
        lsDomi=lsDomi.trim();

        if (lsDomi.length()<6){
            Toast.makeText(getBaseContext(), "Ingrese un dominio válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        tbDomi.setText(lsDomi);
        SetCtrl(false);

        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }

    private void SetCtrl(Boolean tModo){
    if (tModo) {
        mProgressView.setVisibility(View.INVISIBLE);
        tbDomi.setEnabled(true);
        cbConsultar.setEnabled(true);
        list.setEnabled(true);

    }else{
        mProgressView.setVisibility(View.VISIBLE);
        tbDomi.setEnabled(false);
        cbConsultar.setEnabled(false);
        list.setEnabled(false);

    }


    }
}
