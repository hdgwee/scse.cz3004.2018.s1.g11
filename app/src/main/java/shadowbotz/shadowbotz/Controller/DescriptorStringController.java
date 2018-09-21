package shadowbotz.shadowbotz.Controller;

import android.util.Log;

import java.math.BigInteger;

import static android.content.ContentValues.TAG;

public class DescriptorStringController {

    private ImageAdapter imageAdapter;
    private String originalDescriptorString;
    private int numberOfExploredTiles =0; //use this to check if there is any padding

    public DescriptorStringController(ImageAdapter imageAdapter) {
        this.imageAdapter = imageAdapter;
    }

    public void splitImageString(String arrowString){
        /*Example: (6, 5, D),(3, 9, R),(1, 15, D),(7, 19, L),(14, 14, U)*/
        arrowString = arrowString.substring(1, arrowString.length()-1);
        String[] processedArrowString = arrowString.split("\\),\\(");

        for (String s : processedArrowString){
            String[] temp = s.split(", ");
            int x = Integer.parseInt(temp[0]);
            int y = Integer.parseInt(temp[1]);
            imageAdapter.mThumbIds[Math.abs(19-y) * 15 + x] = 3;
        }
        imageAdapter.notifyDataSetChanged();
    }

    public void descriptorString1(String descriptorString1){
        numberOfExploredTiles =0;
        String padded = new BigInteger(descriptorString1, 16).toString(2);

        //pad string with leading zeros
        String formatPad = "%" + (descriptorString1.length() * 4) + "s";
        padded = String.format(formatPad, padded).replace(" ", "0");

        padded = padded.substring(2, padded.length()-2);
        originalDescriptorString = padded;

        Integer[] integers = new Integer[padded.length()];
        // Creates the integer array.
        for (int i = 0; i < integers.length; i++) {
            integers[i] = Integer.parseInt(String.valueOf(padded.charAt(((19-Math.abs(i/15))*15) + (i%15)))); //((19-Math.abs(i/15))*15) + (i%15)) =>to convert the axis
            if(integers[i] == 1){
                numberOfExploredTiles++;
            }
        }
        imageAdapter.mThumbIds = integers;
        imageAdapter.notifyDataSetChanged();

    }

    public void descriptorString2(String descriptorString2){
        String padded = new BigInteger(descriptorString2, 16).toString(2);

        //pad string with leading zeros
        String formatPad = "%" + (descriptorString2.length() * 4) + "s";
        padded = String.format(formatPad, padded).replace(" ", "0");

        /*Ensure padding is at the back*/ //TODO: check if padding works
        int numOfPaddings = padded.length()- numberOfExploredTiles;
        padded = padded.substring(0, padded.length()-numOfPaddings);

        int count = 0;
        char[] charOfOriginalDescriptorString = originalDescriptorString.toCharArray();

        for (int i=0; i< originalDescriptorString.length(); i++){
            if(String.valueOf(charOfOriginalDescriptorString[i]).equals("1")){
                if(String.valueOf(padded.charAt(count)).equals("1")){
                    charOfOriginalDescriptorString[i] = '2';
                }
                count++;
            }
        }
        String temp = String.valueOf(charOfOriginalDescriptorString);

        Integer[] integers = new Integer[temp.length()];
        // Creates the integer array.
        for (int i = 0; i < integers.length; i++) {
            integers[i] = Integer.parseInt(String.valueOf(temp.charAt(((19-Math.abs(i/15))*15) + (i%15)))); //((19-Math.abs(i/15))*15) + (i%15)) =>to convert the axis
        }
        imageAdapter.mThumbIds = integers;
        imageAdapter.notifyDataSetChanged();
        Log.d(TAG, "descriptorString1: "+ padded);

    }
}
