package uk.co.jakelee.blacksmith.helper;

import android.util.Log;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

import uk.co.jakelee.blacksmith.model.Player_Info;

public class SupportCodeHelper {
    private static final String encryptionPwd = "it's" + "just a" + "support code!";

    public static boolean applyCode(String code) {
        String decodedString = decode(code);
        if (!decodedString.equals("")) {
            Player_Info.executeQuery(decodedString);
            return true;
        } else {
            return false;
        }
    }

    public static String decode(String encrypted) {
        String plaintext = "";
        try {
            plaintext = AESCrypt.decrypt(encryptionPwd, encrypted);
        } catch (GeneralSecurityException e) {
            Log.d("Blacksmith", e.toString());
        }
        return plaintext;
    }

    public static String encode(String plaintext) {
        String encrypted = "";
        try {
            encrypted = AESCrypt.encrypt(encryptionPwd, plaintext);
        } catch (GeneralSecurityException e) {
            Log.d("Blacksmith", e.toString());
        }
        return encrypted;
    }
}
