package com.dieam.reactnativeconekta.modules;

import android.app.Activity;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import org.json.JSONObject;

import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Token;

public class RNConekta extends ReactContextBaseJavaModule {
    Boolean isCollected = false;

    public RNConekta(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNConekta";
    }

    @ReactMethod
    public void createToken(ReadableMap info, final Callback successCallback, final Callback failureCallback) {

        String publicKey = info.getString("publicKey");
        String lang = info.getString("lang");

        Conekta.setPublicKey(publicKey);
        Conekta.setLanguage(lang);

        if ( isCollected ) {
            isCollected = true;
            Conekta.collectDevice(getCurrentActivity());
        }

        String cardNumber = info.getString("cardNumber");
        String name = info.getString("name");
        String cvc = info.getString("cvc");
        String expMonth = info.getString("expMonth");
        String expYear = info.getString("expYear");

        Card card = new Card(name, cardNumber, cvc, expMonth, expYear);
        Token token = new Token(getCurrentActivity());

        token.onCreateTokenListener( new Token.CreateToken(){
            @Override
            public void onCreateTokenReady(JSONObject data) {
                try {
                    successCallback.invoke(data.toString());
                } catch (Exception err) {
                    failureCallback.invoke(err.getMessage());
                }
            }
        });

        token.create(card);
    }
}