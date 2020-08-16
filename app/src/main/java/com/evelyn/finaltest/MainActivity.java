package com.evelyn.finaltest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.evelyn.finaltest.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Evelyn Ling's code
    ImageView imageView;
    ImageView imageView2;
    ArrayList<Integer> array = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!OpenCVLoader.initDebug())
        {
            Log.d("evelyn","OpenCv Fail");
        }
        else{
            Log.d("evelyn","OpenCv Success");
        }
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        Button btn = findViewById(R.id.button);
        try {
            btn.setOnClickListener(new View.OnClickListener() {
                Mat image = Utils.loadResource(MainActivity.this,R.drawable.qrcode, CvType.CV_8UC4);
                Mat image2 = Utils.loadResource(MainActivity.this,R.drawable.white, CvType.CV_8UC4);
                @Override
                public void onClick(View v) {
                    try {
                        Bitmap bitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(image,bitmap);
                        imageView.setImageBitmap(bitmap);
                        decodeQRCode(bitmap);
                        Bitmap bitmap2 = Bitmap.createBitmap(image2.width(), image2.height(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(image2,bitmap2);
                        imageView2.setImageBitmap(bitmap2);
                        //drawLine(image, array);
                        Scalar lineColor = new Scalar(255,0,0,255);

                        final int lineThickness = 5;
                        for(int j = 0; j < 12; j += 2)
                        {
                            Point startingP = new Point(array.get(j), array.get(j+1));
                            Point endingP = new Point(array.get(j+2), array.get(j+3));
                            Imgproc.line(image2, startingP, endingP, lineColor, lineThickness);
                            Log.d("evelyn", ""+array.get(j)+","+array.get(j+1));
                            Log.d("evelyn", ""+array.get(j+2)+","+array.get(j+3));
                        }
                        Utils.matToBitmap(image,bitmap);
                        imageView.setImageBitmap(bitmap);
                        Utils.matToBitmap(image2,bitmap2);
                        imageView2.setImageBitmap(bitmap2);
                    } catch (Exception e){
                        Log.d("evelyn",""+e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try{
                InputStream inputStream = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                final Bitmap finalBitmap = bitmap;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(finalBitmap);
                    }
                });
                return bitmap;
            }
            catch(MalformedURLException e){
                e.printStackTrace();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            decodeQRCode(bitmap);

        }
    }

    void decodeQRCode(Bitmap bitmap){
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        String result = qrCodeDetector.detectAndDecode(mat);
        Log.d("evelyn", result);
        String[] lines = result.split(" ");
            for (String point: lines){
                String[] p = point.split(",");
                array.add(Integer.parseInt(p[0]));
                array.add(Integer.parseInt(p[1]));
                Log.d("evelyn", ""+Integer.parseInt(p[0]));
                Log.d("evelyn", ""+Integer.parseInt(p[1]));
            }
        }
    }
