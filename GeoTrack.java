package ar.com.nowait.emedido;
import android.location.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GeoTrack {

    static final String SEPARADOR = "|";

    public void AddPoint(String tLat, String tLong ) {

        Global.GeoLectura lLoc = new Global.GeoLectura();
        lLoc.setLatitud(tLat);
        lLoc.setLongitud(tLong);

        Date today = Calendar.getInstance().getTime();

        lLoc.setFecHora(today.getTime());

        long DiffSeg = 0;
        float DiffMts = 0;

        if (Global.gGeoPoints.size() > 0){
            DiffSeg = today.getTime() - Global.gGeoPoints.get(Global.gGeoPoints.size() - 1).getFecHora();

            Location locA = new Location("");
            locA.setLatitude(Double.parseDouble(tLat));
            locA.setLongitude(Double.parseDouble(tLong));

            Location locB = new Location("");
            locB.setLatitude(Double.parseDouble(Global.gGeoPoints.get(Global.gGeoPoints.size() - 1).getLatitud()));
            locB.setLongitude(Double.parseDouble(Global.gGeoPoints.get(Global.gGeoPoints.size() - 1).getLongitud()));
            DiffMts = locA.distanceTo(locB);

        }

        lLoc.setDifSegs(DiffSeg);
        lLoc.setDifMts(DiffMts);

        Global.gGeoPoints.add(lLoc);

    }

    public void UpPoints(){

        String lsX = "";
        for (Global.GeoLectura punto: Global.gGeoPoints) {
            lsX += punto.getLatitud() + SEPARADOR;
            lsX += punto.getLongitud() + SEPARADOR;
            lsX += punto.getFecHora() + SEPARADOR;
            lsX += punto.getDifMts() + SEPARADOR;
            lsX += punto.getDifSegs() + SEPARADOR;
        }
        
        DoServer DS = new DoServer();
        
        ArrayList<DoServer.SoapProps> sps = new ArrayList<DoServer.SoapProps>();

        DoServer.SoapProps sp = new DoServer.SoapProps("tUsu","fabio_d_rossi@hotmail.com");
        sps.add(sp);

        DoServer.SoapProps sp1 = new DoServer.SoapProps("tPass","123456");
        sps.add(sp1);

        DoServer.SoapProps sp2 = new DoServer.SoapProps("tIdUsu","1");
        sps.add(sp2);

        DoServer.SoapProps sp3 = new DoServer.SoapProps("tData",lsX);
        sps.add(sp3);

        DoServer.SoapProps sp4 = new DoServer.SoapProps("tIdMuni","1");
        sps.add(sp4);

        DS.CallWS("SetGeoRoute",sps);

    }




}
