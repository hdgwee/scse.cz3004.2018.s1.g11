package shadowbotz.shadowbotz.Controller;


import android.icu.text.LocaleDisplayNames;
import android.util.Log;

import java.math.BigInteger;

public class DescriptorStringController {

    private ImageAdapter imageAdapter;

    public DescriptorStringController(ImageAdapter imageAdapter) {
        this.imageAdapter = imageAdapter;
    }

    public void splitImageString(String arrowString){
        /*Example: (6, 5, D),(3, 9, R),(1, 15, D),(7,19, L),(14, 14, U)*/
        arrowString = arrowString.substring(1, arrowString.length()-1);
        String[] processedArrowString = arrowString.split("\\),\\(");

        for (String s : processedArrowString){
            String[] temp = s.split(",");
            int x = Integer.parseInt(temp[0]);
            int y = Integer.parseInt(temp[1]);
            imageAdapter.mThumbIds[Math.abs(19-y) * 15 + x] = 3;
        }
        imageAdapter.notifyDataSetChanged();
    }

    public int descriptorString1(String descriptorString1){
        // 38 bytes in total

        String padded = new BigInteger(descriptorString1, 16).toString(2);

        //pad string with leading zeros
        String formatPad = "%" + (descriptorString1.length() * 4) + "s";
        padded = String.format(formatPad, padded).replace(" ", "0");

        padded = padded.substring(2, padded.length()-2);

        Integer[] integers = new Integer[padded.length()];
        // Creates the integer array.
        for (int i = 0; i < integers.length; i++) {
            integers[i] = Integer.parseInt(String.valueOf(padded.charAt(((19-Math.abs(i/15))*15) + (i%15)))); //((19-Math.abs(i/15))*15) + (i%15)) =>to convert the axis
        }
        imageAdapter.mThumbIds = integers;
        imageAdapter.notifyDataSetChanged();
        return padded.length();
    }

    public void descriptorString2(){

    }
}
