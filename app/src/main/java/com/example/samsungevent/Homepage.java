package com.example.samsungevent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.squareup.picasso.Picasso;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Homepage extends AppCompatActivity {

    private Bitmap bitmap = null,mp;
    private Uri uri;
    private Module module = null;
    Button camerab,galleryb;
    ImageView imageView;
    public  static final int RequestPermissionCode  = 1 ;
    Boolean isFileChosen = false ;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            mp = bitmap;
            String stringUri = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
            uri = Uri.parse(stringUri);
            imageView.setImageBitmap(bitmap);
            isFileChosen = true;
        }else if (requestCode == 1 && data.getData() != null) {
            Uri imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
            uri = imageUri;
            try {
                mp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            isFileChosen = true;
        }
    }

//    public void EnableRuntimePermission(){
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(Homepage.this,
//                Manifest.permission.CAMERA))
//        {
//
//            Toast.makeText(Homepage.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
//
//        } else {
//
//            ActivityCompat.requestPermissions(Homepage.this,new String[]{
//                    Manifest.permission.CAMERA}, RequestPermissionCode);
//
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
//
//        switch (RC) {
//
//            case RequestPermissionCode:
//
//                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    Toast.makeText(Homepage.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
//
//                } else {
//
//                    Toast.makeText(Homepage.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
//
//                }
//                break;
//        }
//    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        imageView = findViewById(R.id.imageView);
        camerab=(Button)findViewById(R.id.camera);
        galleryb=(Button)findViewById(R.id.gallery);
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("kitten.jpg"));
            // loading serialized transcript module from packaged into app android asset model.pt,
            // app/src/model/assets/model.pt
            module = Module.load(assetFilePath(this, "model.pt"));
        } catch (IOException e) {
            Log.e("PTMobileWalkthru", "Error reading assets", e);
            //  finish();
        }

        camerab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForCameraPermission() && checkForReadPermission() && checkForWritePermission()) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 7);
                }else
                    Toast.makeText(Homepage.this, "Give Permissions and try again.", Toast.LENGTH_SHORT).show();
            }
        });

        galleryb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkForReadPermission() && checkForWritePermission()) {
                    Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    cameraIntent.setType("image/*");
                    startActivityForResult(cameraIntent, 1);
                }else
                    Toast.makeText(Homepage.this, "Give Permissions and try again.", Toast.LENGTH_SHORT).show();
            }
        });

       // imageView.setImageBitmap(bitmap);
        // showing image on UI


        final Button button = findViewById(R.id.inferButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                imageView.invalidate();
//                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

                if(!isFileChosen){
                    Toast.makeText(Homepage.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                        TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);

                // running the model
                final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();

                // getting tensor content as java array of floats
                final float[] scores = outputTensor.getDataAsFloatArray();

                // searching for the index with maximum score
                float maxScore = -Float.MAX_VALUE;
                int maxScoreIdx = -1;
                for (int i = 0; i < scores.length; i++) {
                    if (scores[i] > maxScore) {
                        maxScore = scores[i];
                        maxScoreIdx = i;
                    }
                }

                String className = ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx];

                Intent intent = new Intent(Homepage.this,ResultsActivity.class);
                intent.putExtra("uri",uri.toString());
                intent.putExtra("result",className);
                Log.d("Intent Uri",uri.toString());
                startActivity(intent);
            }
        });
    }

    private boolean checkForWritePermission() {
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(Homepage.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {   requestPermissions(
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                2000);
            //Permission automatically granted for Api<23 on installation
        }
        else
            return true;

        return false;
    }

    private boolean checkForCameraPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(Homepage.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        { requestPermissions(
                new String[]{Manifest.permission.CAMERA},
                2000);
        }
        else
            return true;

        return false;
    }

    private boolean checkForReadPermission() {
        if(Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(Homepage.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        { requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                2000);
            //Permission automatically granted for Api<23 on installation
        }
        else
            return true;

        return false;
    }
}