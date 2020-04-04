package ar.com.nowait.emedido;

import java.util.ArrayList;
import java.util.Date;

public class Global {

    public static ArrayList<GeoLectura> gGeoPoints = new ArrayList<>();

    public static class GeoLectura{

        private String Longitud;
        private String Latitud;
        private long FecHora;
        private long DifSegs;
        private float DifMts;

        public float getDifMts() {
            return DifMts;
        }
        public void setDifMts(float DifMts){
            this.DifMts = DifMts;
        }

        public long getDifSegs() {
            return DifSegs;
        }
        public void setDifSegs(long DifSegs){
            this.DifSegs = DifSegs;
        }

        public String getLongitud() {
            return Longitud;
        }
        public void setLongitud(String Longitud){
            this.Longitud = Longitud;
        }

        public String getLatitud() {
            return Latitud;
        }
        public void setLatitud(String Latitud){
            this.Latitud = Latitud;
        }

        public long getFecHora() {
            return FecHora;
        }
        public void setFecHora(long nFecHora){
            this.FecHora = nFecHora;
        }
    }

}

