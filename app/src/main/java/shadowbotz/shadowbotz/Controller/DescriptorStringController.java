package shadowbotz.shadowbotz.Controller;


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
}
