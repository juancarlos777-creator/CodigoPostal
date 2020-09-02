package juan.desarollador.codigopostal.Consultas;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import juan.desarollador.codigopostal.callback;

public  class webServiceDatos {
    String url = "https://sepomex-wje6f4jeia-uc.a.run.app/api/zip-codes/";
    public void volley(String codigoPostal, Context context,final callback callback) {
        try {
            url += codigoPostal;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    callback.onSuccessResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }); requestQueue.add(stringRequest);
            requestQueue.cancelAll("TAG");
        }catch (Exception e){
            Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show();
        }
    }

    public String url(String address) {
        return url + address;
    }


}
