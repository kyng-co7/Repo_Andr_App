package com.example.android.watsonvisrecogex;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;

//Last Updated: 10.JUN.2018

public class MainActivity extends AppCompatActivity {

    private VisualRecognition vrClient;
    private CameraHelper camHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Visual Recognition client
        vrClient = new VisualRecognition(
                VisualRecognition.VERSION_DATE_2016_05_20,
                getString(R.string.api_key)
        );

        //Initialize camera helper
        camHelper = new CameraHelper(this);
    }//onCreate(#)

    public void takePicture(View view) {

        //utilizing device's default camera app
        camHelper.dispatchTakePictureIntent();
    }//takePicture()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == CameraHelper.REQUEST_IMAGE_CAPTURE){
            final Bitmap photo = camHelper.getBitmap(resultCode);
            final File photoFileLoc = camHelper.getFile(resultCode);

            ImageView preview = findViewById(R.id.preview);
            preview.setImageBitmap(photo);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    VisualClassification response =
                            vrClient.classify(
                                    new ClassifyImagesOptions.Builder()
                                        .images(photoFileLoc)
                                        .build()

                            ).execute();

                    //Process more code here
                    ImageClassification imgClassification =
                            response.getImages().get(0);

                    VisualClassifier visClassifier =
                            imgClassification.getClassifiers().get(0);

                    final StringBuffer output = new StringBuffer();
                    for(VisualClassifier.VisualClass vcItrObj: visClassifier.getClasses()){
                            if(vcItrObj.getScore() > 0.7f){
                            output.append("<")
                                    .append(vcItrObj.getName())
                                    .append(">");
                        }//endIf

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView detectedObjects =
                                        findViewById(R.id.detected_objects);

                                detectedObjects.setText(output);
                            }//run()
                        });

                    }//end4Loop
                } //run()
            }); //end thread
        }//endIf

    }//onActivityResult(#...)

}//cls EOL
