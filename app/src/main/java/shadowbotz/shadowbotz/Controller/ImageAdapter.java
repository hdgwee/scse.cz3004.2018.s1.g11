package shadowbotz.shadowbotz.Controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


import shadowbotz.shadowbotz.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);

        } else {
            imageView = (ImageView) convertView;
        }

        switch(mThumbIds[position]){
            case 0: //unexplored
                imageView.setImageResource(R.drawable.tile);
                break;
            case 1: //explored
                imageView.setImageResource(R.drawable.tile_explored);
                break;
            case 2: //obstacle
                imageView.setImageResource(R.drawable.tile_obstacle);
                break;
            case 3: //up arrow
                imageView.setImageResource(R.drawable.tile_arrow_up);
                break;
            case 4://down arrow
                imageView.setImageResource(R.drawable.tile_arrow_down);
                break;
            case 5://left arrow
                imageView.setImageResource(R.drawable.tile_arrow_left);
                break;
            case 6://right arrow
                imageView.setImageResource(R.drawable.tile_arrow_right);
                break;
            case 8: //robot body tile
                imageView.setImageResource(R.drawable.tile_robot_body);
                break;
            case 9: //robot head tile
                imageView.setImageResource(R.drawable.tile_robot_head);
                break;
        }
        imageView.setLayoutParams(new ViewGroup.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        imageView.setPadding(0, 0, 0, 0);
//        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    // references to our images
    public Integer[] mThumbIds = {
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    };
}