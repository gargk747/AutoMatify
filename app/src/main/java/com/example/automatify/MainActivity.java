package com.example.automatify;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView imageView,imageView1;
    ImageButton retake,ocr;
    TextView helperText;
    String currentPhotoPath = null;
    BottomSheetDialog bottomSheetDialog;
    TextToSpeech textToSpeech;

    //Uri photoUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.imageView);
        retake=findViewById(R.id.retake);
        imageView1=findViewById(R.id.imageView1);
        helperText=findViewById(R.id.helperText);
        ocr=findViewById(R.id.ocr);

        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(null);
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                            Manifest.permission.CAMERA
                    },100);
                }

                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                try{
                    Log.d("test","Its working here");
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    if (photoFile != null) {
                        Uri photoUri = FileProvider.getUriForFile(MainActivity.this,
                                "com.example.android.fileprovider",
                                photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        mLauncher.launch(intent);
                    }
                }catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    // Display some error message
                }
            }
        });
    }
    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        ExifInterface exif = null;
                        try {
                            exif = new ExifInterface(currentPhotoPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);

                        int angle = 0;

                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                            angle = 90;
                        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                            angle = 180;
                        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                            angle = 270;
                        }

                        Matrix mat = new Matrix();
                        mat.postRotate(angle);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;

                        Bitmap bmp = BitmapFactory.decodeFile(currentPhotoPath,
                                options);
                        Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                                bmp.getHeight(), mat, true);
                        ByteArrayOutputStream outstudentstreamOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                                outstudentstreamOutputStream);
                        imageView.setImageBitmap(bitmap);
                        helperText.setVisibility(View.INVISIBLE);
                        imageView1.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        ImageToText(bitmap);
                        ocr.setVisibility(View.VISIBLE);
                        ocr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageToText(bitmap);
                            }
                        });
                    }
                }
            });

    private void ImageToText(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap,0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result=recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                Log.d("TESTING"," "+text.getText());
                String resultText = text.getText();
                showModalBottom(resultText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Unable to Convert Image to Text", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showModalBottom(String resultText) {

        bottomSheetDialog=new BottomSheetDialog(MainActivity.this,R.style.BottomSheetTheme);
        View sheetView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout,(ViewGroup)findViewById(R.id.bottom_sheet));
        EditText modal_textView=sheetView.findViewById(R.id.modal_textView);
        ImageButton speak=sheetView.findViewById(R.id.speak);
        modal_textView.setText(resultText);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(modal_textView.getText(),TextToSpeech.QUEUE_FLUSH,null,null);
            }
        });

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                textToSpeech.stop();
            }
        });
        TextView translate =sheetView.findViewById(R.id.translate);
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.stop();
                Log.d("CHECK",modal_textView.getText().toString());
                Intent intent= new Intent(MainActivity.this,TranslatorActivity.class);
                intent.putExtra("Text",modal_textView.getText().toString());
                startActivity(intent);
            }
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    private File createImageFile() throws IOException {

        long timeStamp = System.currentTimeMillis();
        String imageFileName = "NAME_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}