package generalassemb.ly.translatefirebasetest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference messageReferance = firebaseDatabase.getReference("message");
    TextView text1;
    String sourceLan="&source=en";
    String targetLan="&target=es";
    String message;
    OkHttpClient client = new OkHttpClient();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text1 = (TextView) findViewById(R.id.text);


        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("ADDED", "something was added:" + dataSnapshot.child("body").toString());



                     message = dataSnapshot.getValue().toString()
                     .replace(" ","%20");

                text1.setText(message);
               // new DownloadUrlTask().execute(url+message+sourceLan+targetLan);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        messageReferance.addChildEventListener(listener);
    }

   private String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
       Log.d("RESPONSE",response.body().toString());
       return response.body().string();



    }
    private String downloadUrl(String url) throws IOException, JSONException {

        InputStream inputStream = null;

        try {
            URL nativeUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) nativeUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            inputStream = connection.getInputStream();

            return readInput(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

    }

    private String readInput(InputStream inputStream) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String read;

        while ((read = bufferedReader.readLine()) != null) {
            stringBuilder.append(read);
        }
        return stringBuilder.toString();
    }




    private class DownloadUrlTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            try {
                String json = downloadUrl(urls[0]);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d("TAG", "Translate response = "+s);


        }
    }

}