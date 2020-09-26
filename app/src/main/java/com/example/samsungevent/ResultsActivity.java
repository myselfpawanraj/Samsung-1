package com.example.samsungevent;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ResultsActivity extends AppCompatActivity {
    TextView result,mgdlRatio,mmolRatio,risk,suggestedAction;
    ImageView imageView;
    Button buttonDownloadPdf;
    String uri;
    Bitmap bitmap;
    String sugarLevel,stringMgdlRatio,stringMmolLRatio,stringSuggestion,stringRisk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        result = findViewById(R.id.textResult);
        mgdlRatio = findViewById(R.id.mg_dl_ratio);
        mmolRatio = findViewById(R.id.mmol_L_ratio);
        risk = findViewById(R.id.risk);
        suggestedAction = findViewById(R.id.suggested_action);
        imageView = findViewById(R.id.inputImage);
        buttonDownloadPdf = findViewById(R.id.buttonDownloadPdf);
        getSupportActionBar().setTitle("Result Of Sugar Test..");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sugarLevel = getIntent().getStringExtra("result");
        uri = getIntent().getStringExtra("uri");

        Picasso.get().load(uri).into(imageView);
        result.setText(sugarLevel);
        setUpUi(sugarLevel);

        buttonDownloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // loadBitmap automatically call createPdf on getting bitmap
                Log.d("button","clicked");
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uri));
                    Log.d("bitmap","clicked");
                    CreatePDF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUpUi(String sugarLevel) {
        if(sugarLevel.equals(getString(R.string.mild))) {
           //Normal
            stringMgdlRatio = "108";
            stringMmolLRatio = "6";
            stringRisk = "No Risk";
            stringSuggestion = "No Action Needed";
       }
       else if(sugarLevel.equals(getString(R.string.moderate))) {
           // Borderline
            stringMgdlRatio = "150";
            stringMmolLRatio = "8.2";
            stringRisk = "Moderate";
            stringSuggestion = "Consult Your Doctor";
       }
       else if(sugarLevel.equals(getString(R.string.severe))) {
           // high
            stringMgdlRatio = "250";
            stringMmolLRatio = "13.7";
            stringRisk = "High";
            stringSuggestion = "Seek Medical Attention";
       }
       else if(sugarLevel.equals(getString(R.string.proliferative_dr))) {
            // dangerously high
            stringMgdlRatio = "315+";
            stringMmolLRatio = "11";
            stringRisk = "Very High";
            stringSuggestion = "Seek Medical Attention";
        }
       else {
            // dangerously low
            stringMgdlRatio = "315+";
            stringMmolLRatio = "11";
            stringRisk = "Very High";
            stringSuggestion = "Seek Medical Attention";
        }
        mgdlRatio.setText(stringMgdlRatio);
        mmolRatio.setText(stringMmolLRatio);
        risk.setText(stringRisk);
        suggestedAction.setText(stringSuggestion);
    }

    public void CreatePDF() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // add title
        paint.setColor(Color.BLACK);
        paint.setTextSize(12f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("SAMSUNG SUGAR LEVEL REPORT",pageInfo.getPageWidth()/2, 30, paint);

        paint.setTextSize(10f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("mg/dl    =   "+stringMgdlRatio,50, 60, paint);

        paint.setTextSize(10f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("mmol/L   =   "+stringMmolLRatio,50, 80, paint);

        paint.setTextSize(10f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Risk Level is ",50, 100, paint);
        paint.setColor(Color.BLUE);
        canvas.drawText(stringRisk,180, 100, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(11f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Suggestions - ",55, 130, paint);
        paint.setTextSize(10f);
        canvas.drawText(stringSuggestion,180, 130, paint);

        // add photo
        canvas.drawText("Your Submitted Image is ",80, 150, paint);
        bitmap = Bitmap.createScaledBitmap(bitmap,250,300,true);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawBitmap(bitmap,20,180,paint);

        // finish page
        pdfDocument.finishPage(page);


        // Save PDF File
        String filePath = Environment.getExternalStorageDirectory().getPath()+"/Download/"+ "SamSungReport - "+Calendar.getInstance().getTimeInMillis()+".pdf";
        File file = new File(filePath);
        try {
            Log.d("pdf","saved");
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Pdf Saved Successfully\n Check Your Downloads.", Toast.LENGTH_LONG).show();
            //openPdf();
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        pdfDocument.close();
    }

    private void openPdf() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Download/"+ "SamSungReport.pdf"); // Here you declare your pdf path
        Intent pdfViewIntent = new Intent(Intent.ACTION_VIEW);
        pdfViewIntent.setDataAndType(Uri.fromFile(file),"application/pdf");
        pdfViewIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(pdfViewIntent, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }
}