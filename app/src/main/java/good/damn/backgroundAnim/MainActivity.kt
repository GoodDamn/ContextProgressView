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
            val url = URL("https://file-examples.com/storage/fe629099fc646eff79529f9/2017/11/file_example_WAV_10MG.wav");

            val connection = url.openConnection();

            connection.connect();


            val contentLength = connection.contentLengthLong;
            Log.d(TAG, "onCreate: CONNECTION CREATED");
            Log.d(TAG, "onCreate: GETTING CONTENT LENGTH: $contentLength")
            Log.d(TAG, "onCreate: CONNECTION TIMEOUT: ${connection.connectTimeout}");
            Log.d(TAG, "onCreate: CONTENT ENCODING: ${connection.contentEncoding}");
            Log.d(TAG, "onCreate: CONTENT-TYPE: ${connection.contentType}");
            Log.d(TAG, "onCreate: DATE: ${connection.date}");

            val divider = contentLength / 100;

            val downloadStream = url.openStream();
            var i: Int;
            val buffer = ByteArray(512);
            val baos = ByteArrayOutputStream();
            while (true) {
                i = downloadStream.read(buffer);
                Log.d(TAG, "onCreate: DOWNLOAD FILE PROCESS... $i ${baos.size()}")
                animatedView.linesCount = (100 - baos.size() / divider).toInt();
                if (i == -1)
                    break;
                baos.write(buffer,0,i);
            }

            Log.d(TAG, "onCreate: DOWNLOAD PROCESS FINISHED! ${baos.toByteArray().size}");
            baos.close();
            downloadStream.close();
        }).start();
    }
}