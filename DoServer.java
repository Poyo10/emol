package ar.com.nowait.emedido;

import android.os.AsyncTask;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class DoServer {

    String gServicio;
    ArrayList<SoapProps> gData;

    public IdDescri CallWS(String tServicio, ArrayList<SoapProps> tData) {

        this.gServicio = tServicio;
        this.gData=tData;

        AsyncCallWS task = new AsyncCallWS();
        task.execute();

        IdDescri RetVal = new IdDescri(1, "RetTxt");
        return RetVal;
    }

    public static class SoapProps{

        private String Nombre;
        private String Valor;

        public SoapProps(String Nombre, String Valor) {
            this.Nombre = Nombre;
            this.Valor = Valor;
        }

        public String getNombre() {
            return Nombre;
        }
        public void setNombre(String Nombre){
            this.Nombre = Nombre;
        }

        public String getValor() {
            return Valor;
        }
        public void setValor(String Valor){
            this.Valor = Valor;
        }

    }


    public class IdDescri{

        private int Id;
        private String Descri;

        public IdDescri(int Id, String Descri) {
            this.Id = Id;
            this.Descri = Descri;
        }

        public int getId() {
            return Id;
        }
        public void setId(int Id){
            this.Id = Id;
        }

        public String getDescri() {
            return Descri;
        }
        public void setDescri(String Descri){
            this.Descri = Descri;
        }

    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {}

        @Override
        protected Void doInBackground(Void... params) {  // LLamo al WS
            DoWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {  // Retorno del WS

//            if ( Integer.parseInt( RetId)<=0) {
//                Toast.makeText(getBaseContext(), RetTxt , Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(getBaseContext(), "Multa registrada.", Toast.LENGTH_SHORT).show();
//                finish();
//            };
            // String lsX= RetTxt;
        }

        @Override
        protected void onCancelled() {
            //SetCtrl(true);
        }

    }

    public void DoWS() {

        String SOAP_ACTION = "http://nowait.com.ar/" + gServicio;
        String METHOD_NAME = gServicio;
        String NAMESPACE = "http://nowait.com.ar/";
        String URL = "http://nowait.com.ar:8888/wsEme.asmx";

        SoapObject resSoap;
        int RetId = 0;
        String RetTxt = "";

        try {

            SoapObject Request = new SoapObject(NAMESPACE, gServicio);

            for (SoapProps Prop :gData) {
                Request.addProperty(Prop.Nombre, Prop.Valor);

            }

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);

            resSoap =(SoapObject)soapEnvelope.getResponse();
            RetId= Integer.parseInt(resSoap.getProperty("Id").toString());
            RetTxt=resSoap.getProperty("Descri").toString();

        } catch (Exception ex) {
            RetId=-1;
            RetTxt="Error accediendo al servidor." + ex.getMessage();

        }

    }

}
