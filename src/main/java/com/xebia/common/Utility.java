package com.xebia.common;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Created by Pgupta on 26-07-2016.
 */
public class Utility {

    public static boolean isExpired(Date inputDate){
        LocalDate toCompareDate = inputDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate todayDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return toCompareDate.isBefore(todayDate);
    }

    public static String encode(String string){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(string.getBytes(), 0, string.length());
            String hashedPass = new BigInteger(1, messageDigest.digest()).toString(16);
            if (hashedPass.length() < 32) {
                hashedPass = "0" + hashedPass;
            }
            return hashedPass;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
