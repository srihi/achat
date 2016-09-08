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

    public static final int QR_HEIGHT = 400;
    public static final int QR_WIDTH = 400;

    public static Bitmap generateShareCodeQR(String shareCode, int dipFactor)
    {
        BarcodeEncoder encoder = new BarcodeEncoder();
        Bitmap bitmap = null;

        try
        {
            int width = QR_WIDTH * (dipFactor / 160);
            int height = QR_HEIGHT * (dipFactor / 160);

            bitmap = encoder.encodeBitmap(shareCode, BarcodeFormat.QR_CODE, width, height);
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
