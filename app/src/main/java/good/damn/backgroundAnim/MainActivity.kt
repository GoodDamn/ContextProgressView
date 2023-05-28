package good.damn.backgroundAnim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        val animatedView: AnimatedBackgroundView = findViewById(R.id.activity_animatedView);

        Thread(Runnable {
            val url = URL("http://212.183.159.230/100MB.zip");

            val connection = url.openConnection();

            connection.connect();

            val contentLength = connection.contentLength;
            Log.d(TAG, "onCreate: CONNECTION CREATED");
            Log.d(TAG, "onCreate: GETTING CONTENT LENGTH: $contentLength")
            Log.d(TAG, "onCreate: CONNECTION TIMEOUT: ${connection.connectTimeout}");
            Log.d(TAG, "onCreate: CONTENT ENCODING: ${connection.contentEncoding}");
            Log.d(TAG, "onCreate: CONTENT-TYPE: ${connection.contentType}");
            Log.d(TAG, "onCreate: DATE: ${connection.date}");

            val divider = contentLength / 200;

            val downloadStream = url.openStream();
            var i: Int;
            val buffer = ByteArray(2048);
            val baos = ByteArrayOutputStream();
            var progress: Int = 0;
            while (true) {
                i = downloadStream.read(buffer);
                //Log.d(TAG, "onCreate: DOWNLOAD FILE PROCESS... $i ${baos.size()}")
                progress += i;
                animatedView.linesCount = 200 - progress / divider;
                if (i == -1)
                    break;
                if (progress > 1_000_000) {
                    baos.reset();
                }
                baos.write(buffer,0,i);
            }

            animatedView.linesCount = 0;

            Log.d(TAG, "onCreate: DOWNLOAD PROCESS FINISHED!");

            baos.close();
            downloadStream.close();

            Thread.currentThread().interrupt();
        }).start();
    }
}