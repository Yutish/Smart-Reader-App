package com.example.smartreader;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

//for text to speech ....
public class mTextSpeaker implements TextToSpeech.OnInitListener {


    private TextToSpeech tts;
    private Context contextToPlayIn;
    private float speedOfSpeaking = 1.0f;


    //constructor to accept the context....
    public mTextSpeaker(Context c) {
        contextToPlayIn = c;
        tts = new TextToSpeech(contextToPlayIn, this);
    }

    //for changing speed
    void setSpeedOfSpeaking(float speedRate) {
        speedOfSpeaking = speedRate;
    }


    //for reading....
    void speakThis(String textToSpeak) {
        tts.setSpeechRate(speedOfSpeaking);
        tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    //for stopping....
    void nowStop() {
        tts.stop();
    }

    //on destroy....
    void nowDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}
