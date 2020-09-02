package juan.desarollador.codigopostal;
import androidx.appcompat.app.AppCompatActivity;
import juan.desarollador.codigopostal.Consultas.webServiceDatos;
import juan.desarollador.codigopostal.Consultas.webServicePoliginos;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnPolygonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, OnPolygonClickListener, View.OnClickListener {
    GoogleMap gmap;
    Polyline polyline1 =null;
    Button btnActualizar;
    JSONObject jsonEvent;
    TextInputEditText txtCp,txtPais,txtEntidad,txtCiudad,txtAlcaldia,txtColonia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnActualizar=findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(this);

        txtCp=findViewById(R.id.txtCp);
        txtPais=findViewById(R.id.txtPais);
        txtEntidad=findViewById(R.id.txtEntidad);
        txtCiudad=findViewById(R.id.txtCiudad);
        txtAlcaldia=findViewById(R.id.txtAlcaldia);
        txtColonia=findViewById(R.id.txtColonia);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap=googleMap;
    }


    public void dibujarPoligono(ArrayList<LatLng> coordList){
        Double xmax=coordList.get(0).latitude,xmin=coordList.get(0).latitude,ymax=coordList.get(0).longitude,ymin=coordList.get(0).longitude;
        for (int i=0;i<coordList.size();i++){

            if(xmax<=coordList.get(i).latitude){
                xmax=coordList.get(i).latitude;
            }
            if(xmin>=coordList.get(i).latitude){
                xmin=coordList.get(i).latitude;
            }

            if(ymax<=coordList.get(i).longitude){
                ymax=coordList.get(i).longitude;
            }
            if(ymin>=coordList.get(i).longitude){
                ymin=coordList.get(i).longitude;
            }

        }
        Double xcamara=0.0,ycamara=0.0;
        xcamara = xmin + ((xmax - xmin) / 2);
        ycamara = ymin + ((ymax - ymin) / 2);

LatLng sydney = new LatLng(coordList.get(0).latitude, coordList.get(0).longitude);
        gmap.addMarker(new MarkerOptions().position(sydney)
                .title(""));


        polyline1 = gmap.addPolyline(new PolylineOptions()
                .clickable(true)
                .addAll(coordList).color(Color.BLUE));


        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(xcamara, ycamara), 14));
        gmap.setOnPolylineClickListener(this);
        gmap.setOnPolygonClickListener(this);

    }




    @Override
    public void onPolygonClick(Polygon polygon) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnActualizar:
                if(!txtCp.getText().toString().equals("")){
                    ocultarTeclado();
                    consultarPoligonos(txtCp.getText().toString());
                    consultarDatos(txtCp.getText().toString());
                }else{
alerta("INGRESA UN CODIGO POSTAL");
                }

                break;
        }
    }

public void consultarDatos(final String codigoPostal){
    limpiarCajaTxt();
        webServiceDatos dts =new webServiceDatos();
        dts.volley(codigoPostal, getBaseContext(), new callback() {
            @Override
            public void onSuccessResponse(String result) {
                try {

                    JSONObject jsonaux = new JSONObject(result);
                    JSONObject Jasonobject = jsonaux.getJSONObject("federal_entity");
                    txtCp.setText(codigoPostal);
                    txtPais.setText("México");
                    txtCiudad.setText(jsonaux.getString("locality"));
                    txtEntidad.setText(Jasonobject.getString("name"));

                    Jasonobject = jsonaux.getJSONObject("municipality");
                    txtAlcaldia.setText(Jasonobject.getString("name"));


                    JSONArray jsonarray = new JSONArray(jsonaux.get("settlements").toString());
                    JSONObject objeto = jsonarray.getJSONObject(0);
                    String valorCodigo = objeto.getString("name");
                 txtColonia.setText(valorCodigo);


                } catch (JSONException e) {
                    consola(e.toString());
                    limpiarCajaTxt();
                }

            }

            @Override
            public void Error(String msj) {

            }
        });

}
public void consultarPoligonos(String codigoPostal){
    gmap.clear();
    webServicePoliginos poliginos= new  webServicePoliginos();
    poliginos.volley(codigoPostal, getBaseContext(), new callback() {
        @Override
        public void onSuccessResponse(String result) {
            try {
                jsonEvent = new JSONObject(result);
                JSONObject Jasonobject = jsonEvent.getJSONObject("geometry");
                String[] cordenadas = Jasonobject.getString("coordinates").split("],");
                ArrayList<LatLng> coordList = new ArrayList<LatLng>();
                for (int i=0;i<cordenadas.length;i++){

                    cordenadas[i] = cordenadas[i].replace("]","");
                    cordenadas[i] = cordenadas[i].replace("[","");
                    String[] posicion= cordenadas[i].split(",");
                    Double p1=Double.parseDouble(posicion[0]);
                    Double p2=Double.parseDouble(posicion[1]);
                    coordList.add(new LatLng(p2, p1));
                }
dibujarPoligono(coordList);
            }catch (JSONException e){
                consola("Error");
                consola(e.toString());
                limpiarCajaTxt();
            }
        }

        @Override
        public void Error(String msj) {
            try {
                alerta(msj);
            }catch (Exception e){
                alerta(e.toString());
            }
        }
    });
}

    public void consola(String mjs) {
        Log.w("--->", mjs);
    }
    void alerta(String texto)
    {
        Toast.makeText(getBaseContext(), texto, Toast.LENGTH_LONG).show();
    }
    public void ocultarTeclado(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtCp.getWindowToken(), 0);
    }
    public void limpiarCajaTxt(){
        txtCp.setText("");
        txtColonia.setText("");
        txtAlcaldia.setText("");
        txtCiudad.setText("");
        txtPais.setText("");
        txtEntidad.setText("");
    }
}
