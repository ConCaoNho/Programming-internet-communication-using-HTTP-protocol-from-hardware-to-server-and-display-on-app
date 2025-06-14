package com.duong_21011224.bth9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.ZoneId;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView tvTest, tvData1, tvData2;
    private Handler handler = new Handler();
    private OkHttpClient client = new OkHttpClient();
    private static final int UPDATE_INTERVAL = 1000; // 1 giây
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTest= findViewById(R.id.tvTest);
        Button btSend = findViewById(R.id.btSend);
        EditText EDvar1 = findViewById(R.id.EDvar1);
        EditText EDvar2 = findViewById(R.id.EDvar2);
        tvData1 = findViewById(R.id.tvData1);
        tvData2 = findViewById(R.id.tvData2);
        btSend.setOnClickListener(v->{
            //Goi phuong thuc sendPostRequest voi cac tham so tuong ung
            String param1 = EDvar1.getText().toString();
            String param2 = EDvar2.getText().toString();
            sendPostRequest(param1, param2);
        });
        startAutoUpdate();

    }
    private void sendPostRequest(String param1, String param2){
        OkHttpClient client = new OkHttpClient();
        // Creat JSON Object includ parameter
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("param1",param1);
            jsonObject.put("param2",param2);
        } catch (JSONException e){
            e.printStackTrace();
        }
        // Chuyen Json object thanh chuoi json
        String jsonBody= jsonObject.toString();
        RequestBody requestBody= RequestBody.create(jsonBody, MediaType.get("application/json;charset=utf-8"));
        Request request = new Request.Builder()
                .url("http://172.20.10.8/BTH9/process.php")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                displayToast("Error: "+ e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null){
                    // Chỉ đọc body một lần
                    final String responseBody = response.body().string();
                    displayToast(responseBody);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(),"Error HTTP: " + response.code(),Toast.LENGTH_SHORT).show();
                    });
                }
            }

        });
    }
    private void displayToast(String message){
        runOnUiThread(()->{
            try {
                tvTest.setText(message);
                // Kiểm tra xem message có phải JSON không
                if (message.trim().startsWith("{") && message.trim().endsWith("}")) {
                    JSONObject jsonObject = new JSONObject(message);
                    if (jsonObject.has("result")) {
                        String var_ = jsonObject.getString("result");
                        tvTest.setText(var_);
                    } else {
                        tvTest.setText("No result field in response");
                    }
                }
            } catch (JSONException ex){
                tvTest.setText("JSON Parse Error: " + ex.getMessage());
                ex.printStackTrace();
            } catch (Exception ex){
                tvTest.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
    private void updateDataFromJson(String message) {
        runOnUiThread(() -> {
            try {
                if (message.trim().startsWith("{") && message.trim().endsWith("}")) {
                    JSONObject jsonObject = new JSONObject(message);
                    if (jsonObject.has("var1") && jsonObject.has("var2")) {
                        String var1 = jsonObject.getString("var1");
                        String var2 = jsonObject.getString("var2");

                        tvData1.setText(var1);
                        tvData2.setText(var2);
                    } else {
                        Toast.makeText(getApplicationContext(), "Thiếu var1 hoặc var2", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Phản hồi không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Lỗi phân tích JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startAutoUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchDataFromServer();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, 0);
    }

    private void fetchDataFromServer() {
        Request request = new Request.Builder()
                .url("http://172.20.10.8/BTH9/get_data.php")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> displayToast("Lỗi: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("HTTP", "Response: " + responseBody);
                    updateDataFromJson(responseBody);
                } else {
                    runOnUiThread(() -> displayToast("Lỗi HTTP: " + response.code()));
                }
            }
        });
    }

}