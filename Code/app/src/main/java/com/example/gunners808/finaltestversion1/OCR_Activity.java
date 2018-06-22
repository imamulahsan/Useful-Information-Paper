package com.example.gunners808.finaltestversion1;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OCR_Activity extends AppCompatActivity {

    ImageView image_view;
    ImageButton capture_button;
    TextView text_view;

    String mCurrentPhotoPath;
    String recognisedText;
    static final int REQUEST_TAKE_PHOTO = 1;
    public static final String LOG_TAG = "Camera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_);

        image_view = findViewById(R.id.image_view);
        capture_button = findViewById(R.id.capture_button);
        text_view = findViewById(R.id.text_view);

        capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dispatchTakePictureIntent();
            }
        });
    }

    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName,".jpg",storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager())!=null)
        {
            File photoFile = null;

            try
            {
                photoFile = createImageFile();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                photoFile = null;
                mCurrentPhotoPath = null;
            }
            if(photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
            }
        }


    }

    private void galleryAddPic()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {


            setPic();
            galleryAddPic();

        }
    }

    private void setPic()
    {
        int targetW = image_view.getMaxWidth();
        int targetH = image_view.getMaxHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scalefactor = Math.min(photoW/targetW,photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scalefactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        image_view.setImageBitmap(bitmap);

        if(bitmap != null)
        {
            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

            if(!textRecognizer.isOperational())
            {
                Log.w(LOG_TAG,"Detector dependencies are not yet available.");
                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null,lowstorageFilter) != null;

                if(hasLowStorage)
                {
                    Toast.makeText(this,"Low Storage", Toast.LENGTH_LONG).show();
                    Log.w(LOG_TAG,"Low Storage");
                }
            }

            Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(imageFrame);
            StringBuilder stringBuilder = new StringBuilder();

            for (int i=0; i<items.size(); i++)
            {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append(" ");
            }
            text_view.setText(stringBuilder.toString());
            recognisedText = text_view.getText().toString();

            Intent intent = new Intent(this, OCRoutputActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, recognisedText);
            startActivity(intent);
        }
    }
}
