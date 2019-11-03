package sa.aqwas.wainnakolsample.data.repository;

import android.util.Log;

import androidx.room.Room;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import sa.aqwas.wainnakolsample.data.db.AppDatabase;
import sa.aqwas.wainnakolsample.data.db.entities.Restaurant;
import sa.aqwas.wainnakolsample.data.state.StateLiveData;
import sa.aqwas.wainnakolsample.utils.Constants;
import sa.aqwas.wainnakolsample.utils.HttpConnector;
import sa.aqwas.wainnakolsample.utils.MyApp;
import sa.aqwas.wainnakolsample.utils.Utils;

public class RestaurantRepository {
    private static final String TAG = RestaurantRepository.class.getSimpleName();

    private static RestaurantRepository mInstance;
    private static AppDatabase mDatabase;

    private RestaurantRepository(){
        initDB();
    }

    public synchronized static RestaurantRepository getInstance(){
        if(mInstance == null){
            mInstance = new RestaurantRepository();
        }
        return mInstance;
    }

    public void getRestaurant(double latitude, double longitude, StateLiveData<Restaurant> observable){
        refreshRestaurant(latitude, longitude, observable);
    }

    private void refreshRestaurant(double latitude, double longitude, StateLiveData<Restaurant> observable){
        observable.setLoading();
        String url = Constants.GET_RESTAURANT_URL;
        url = url.concat("?uid=").concat(""+latitude).concat(",").concat(""+longitude).concat("&get_param=value");
        Log.d(TAG, "getRestaurant url: " + url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "getRestaurant response: " + response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has(Constants.PARAMETER_ERROR) && jsonObject.getString(Constants.PARAMETER_ERROR).equals(Constants.PARAMETER_NO_ERROR)){
                        Gson gson = Utils.getGson();
                        Type type = new TypeToken<Restaurant>() {}.getType();
                        Restaurant restaurantItem;
                        restaurantItem = gson.fromJson(jsonObject.toString(), type);
                        mDatabase.restaurantDao().insertAll(restaurantItem);

                        observable.setSuccess(restaurantItem);
                    }else{
                        observable.setError(Utils.getErrorObjectFromJSONObject(jsonObject));
                    }
                }catch (JSONException e){
                    Log.d(TAG, "JSON Exception: " + e.getMessage());
                    observable.setError(Utils.getErrorObjectFromJSONException(e));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: " + error.getMessage());
                observable.setError(Utils.getErrorObjectFromVolleyError(error));
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        HttpConnector.getInstance(MyApp.getInstance()).addToRequestQueue(request);
    }

    private static AppDatabase initDB(){
        if(mDatabase != null){
            return mDatabase;
        }else{
            mDatabase = Room.databaseBuilder(MyApp.getInstance(), AppDatabase.class, Constants.DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            return mDatabase;
        }
    }
}
