package com.dankira.achat;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Random;

/**
 * Created by da on 7/20/2016.
 */
public class ShareCodeHelper
{

    public static final int QR_HEIGHT = 150;
    public static final int QR_WIDTH = 150;
    public static final int SHARE_CODE_LENGTH = 8;

    public static String generateShareCode()
    {
        String randString = generateRandomString(SHARE_CODE_LENGTH);
        // TODO: 7/20/2016 Check if this code is already being used.

        return randString;

    }

    public static Bitmap generateShareCodeQR(String shareCode)
    {
        BarcodeEncoder encoder = new BarcodeEncoder();
        Bitmap bitmap = null;

        try
        {
            bitmap = encoder.encodeBitmap(shareCode, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static String generateRandomString(int len)
    {
        char[] chars_superset = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray();
        int superset_length = chars_superset.length;
        StringBuilder builder = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < len; i++)
        {
            char random_char = chars_superset[rand.nextInt(superset_length)];
            builder.append(random_char);
        }

        return builder.toString();
    }
}
