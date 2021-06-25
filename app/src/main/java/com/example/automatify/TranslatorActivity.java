package com.example.automatify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TranslatorActivity extends AppCompatActivity {

    String inputString;
    String sourceLanguage;
    ImageView backButton,speakOut;
    TextView sourceLang,sourceText,targetText;
    AutoCompleteTextView autoCompleteTextView;
    CircularProgressIndicator progressIndicator;
    TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);

        inputString=getIntent().getStringExtra("Text");

        sourceLang=findViewById(R.id.sourceLang);
        sourceText=findViewById(R.id.sourceText);
        targetText=findViewById(R.id.targetText);
        progressIndicator=findViewById(R.id.progress_circular_indicator);
        speakOut=findViewById(R.id.speakOut);

        sourceText.setText(inputString);

        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });


        autoCompleteTextView=findViewById(R.id.targetLang);
        ArrayAdapter<CharSequence> arrayAdapter=ArrayAdapter.createFromResource(this,R.array.Languages,R.layout.language_options);
        autoCompleteTextView.setText(arrayAdapter.getItem(0).toString(),false);
        autoCompleteTextView.setAdapter(arrayAdapter);
        String[] langList = getResources().getStringArray(R.array.Languages);
        String[] langListCodes = getResources().getStringArray(R.array.LanguagesCodes);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressIndicator.setVisibility(View.VISIBLE);
                textToSpeech.stop();
                Log.d("CHECK", "onItemClick: "+position+" "+id);
                String targetLang= langList[position];
                String targetLangCode=langListCodes[position];

                TranslatorOptions options =
                        new TranslatorOptions.Builder()
                                .setSourceLanguage(sourceLanguage)
                                .setTargetLanguage(targetLangCode)
                                .build();
                final Translator translator =
                        Translation.getClient(options);
                DownloadConditions conditions = new DownloadConditions.Builder()
                        .requireWifi()
                        .build();

                translator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void aVoid) {
                                translator.translate(inputString).addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(@NonNull String s) {
                                        progressIndicator.setVisibility(View.INVISIBLE);
                                        targetText.setText(s);
                                        speakOut.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                textToSpeech.speak(targetText.getText(),TextToSpeech.QUEUE_FLUSH,null,null);
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TranslatorActivity.this, "Unable to Translate text", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TranslatorActivity.this, "Unable to download Language files, Please connect to Wifi Network", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                textToSpeech.stop();
            }
        });



        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient(new LanguageIdentificationOptions.Builder()
                        .setConfidenceThreshold(0.1f)
                        .build());
        languageIdentifier.identifyLanguage(inputString)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode.equals("und")) {
                                    Log.d("TEST", "Can't identify language.");
                                } else {
                                    Log.i("TEST", "Language: " + languageCode);
                                    //for finding the language type
                                    sourceLanguage=TranslateLanguage.fromLanguageTag(languageCode);
                                    for(int i=0;i<langListCodes.length;i++){
                                        if(sourceLanguage.equals(langListCodes[i])){
                                            sourceLang.setText(langList[i]);
                                        }
//                                    sourceLanguage="en";
//                                    sourceLang.setText("ENGLISH");
                                }}
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TranslatorActivity.this, "Couldn't Identify the Language", Toast.LENGTH_SHORT).show();
                            }
                        });



    }
}