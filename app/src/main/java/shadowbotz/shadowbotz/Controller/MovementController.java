package shadowbotz.shadowbotz.Controller;

import shadowbotz.shadowbotz.View.MainActivity;

public class MovementController {
    ImageAdapter imageAdapter;


    public MovementController(ImageAdapter imageAdapter) {
        this.imageAdapter = imageAdapter;
    }

    public void setBody(int body){
        imageAdapter.mThumbIds[body] = 8;
        //4 corners
        imageAdapter.mThumbIds[body-14] = 8; //set the whole body
        imageAdapter.mThumbIds[body-16] = 8;
        imageAdapter.mThumbIds[body+14] = 8;
        imageAdapter.mThumbIds[body+16] = 8;

        //the rest
        imageAdapter.mThumbIds[body+1] = 8;
        imageAdapter.mThumbIds[body-1] = 8;
        imageAdapter.mThumbIds[body+15] = 8;
        imageAdapter.mThumbIds[body-15] = 8;
    }

    public void setHead(int head){
        imageAdapter.mThumbIds[head] = 9;
    }

    public int turnRightwhenFaceRight(int head, int body){
        imageAdapter.mThumbIds[head] = 8;
        head = body+15;
        imageAdapter.mThumbIds[head] = 9;
        return head;
    }
    public int turnRightwhenFaceLeft(int head, int body){
        imageAdapter.mThumbIds[head] = 8;
        head = body-15;
        imageAdapter.mThumbIds[head] = 9;
        return head;
    }
    public int turnRightwhenFaceUp(int head, int body){
        imageAdapter.mThumbIds[head] = 8;
        head = body+1;
        imageAdapter.mThumbIds[head] = 9;
        return head;
    }
    public int turnRightwhenFaceDown(int head, int body){
        imageAdapter.mThumbIds[head] = 8;
        head = body-1;
        imageAdapter.mThumbIds[head] = 9;
        return head;
    }


    public int turnLeftwhenFaceRight(int head, int body){
        imageAdapter.mThumbIds[head] = 8;
        head = body-15;
        imageAdapter.mThumbIds[head] = 9;
        return head;
    }
    public int turnLeftwhenFaceLeft(int head, int body){
        imageAdapter.mThumbIds[head] = 8;
        head = body+15;
        imageAdapter.mThumbIds[head] = 9;
        return head;
    }
    public int turnLeftwhenFaceUp(int head, int body){
        imageAdapter.mThumbIds[head] = 8;
        head = body-1;
        imageAdapter.mThumbIds[head] = 9;
        return head;
    }
    public int turnLeftwhenFaceDown(int head, int body){
        imageAdapter.mThumbIds[head] = 8;
        head = body+1;
        imageAdapter.mThumbIds[head] = 9;
        return head;
    }

    public void moveForwardWhenFaceRight(int head, int body){
        imageAdapter.mThumbIds[head+1] = 9;

        //4 corners
        imageAdapter.mThumbIds[(body-14)+1] = 8; //set the whole body
        imageAdapter.mThumbIds[(body-16)+1] = 8;
        imageAdapter.mThumbIds[(body+14)+1] = 8;
        imageAdapter.mThumbIds[(body+16)+1] = 8;

        //the rest
        imageAdapter.mThumbIds[(body)+1] = 8;
        imageAdapter.mThumbIds[(body-1)+1] = 8;
        imageAdapter.mThumbIds[(body+15)+1] = 8;
        imageAdapter.mThumbIds[(body-15)+1] = 8;

        //convert back of robot to explored
        imageAdapter.mThumbIds[(body-1)] = 1;
        imageAdapter.mThumbIds[(body-16)] = 1;
        imageAdapter.mThumbIds[(body+14)] = 1;
    }

    public void moveForwardWhenFaceLeft(int head, int body){
        imageAdapter.mThumbIds[head-1] = 9;

        //4 corners
        imageAdapter.mThumbIds[(body-14)-1] = 8; //set the whole body
        imageAdapter.mThumbIds[(body-16)-1] = 8;
        imageAdapter.mThumbIds[(body+14)-1] = 8;
        imageAdapter.mThumbIds[(body+16)-1] = 8;

        //the rest
        imageAdapter.mThumbIds[(body)-1] = 8;
        imageAdapter.mThumbIds[(body+1)-1] = 8;
        imageAdapter.mThumbIds[(body+15)-1] = 8;
        imageAdapter.mThumbIds[(body-15)-1] = 8;

        //convert back of robot to explored
        imageAdapter.mThumbIds[(body+1)] = 1;
        imageAdapter.mThumbIds[(body+16)] = 1;
        imageAdapter.mThumbIds[(body-14)] = 1;
    }

    public void moveForwardWhenFaceUp(int head, int body){
        imageAdapter.mThumbIds[head-15] = 9;

        //4 corners
        imageAdapter.mThumbIds[(body-14)-15] = 8; //set the whole body
        imageAdapter.mThumbIds[(body-16)-15] = 8;
        imageAdapter.mThumbIds[(body+14)-15] = 8;
        imageAdapter.mThumbIds[(body+16)-15] = 8;

        //the rest
        imageAdapter.mThumbIds[(body)-15] = 8;
        imageAdapter.mThumbIds[(body-1)-15] = 8;
        imageAdapter.mThumbIds[(body+15)-15] = 8;
        imageAdapter.mThumbIds[(body+1)-15] = 8;

        //convert back of robot to explored
        imageAdapter.mThumbIds[(body+14)] = 1;
        imageAdapter.mThumbIds[(body+15)] = 1;
        imageAdapter.mThumbIds[(body+16)] = 1;
    }

    public void moveForwardWhenFaceDown(int head, int body){
        imageAdapter.mThumbIds[head+15] = 9;

        //4 corners
        imageAdapter.mThumbIds[(body-14)+15] = 8; //set the whole body
        imageAdapter.mThumbIds[(body-16)+15] = 8;
        imageAdapter.mThumbIds[(body+14)+15] = 8;
        imageAdapter.mThumbIds[(body+16)+15] = 8;

        //the rest
        imageAdapter.mThumbIds[(body)+15] = 8;
        imageAdapter.mThumbIds[(body-1)+15] = 8;
        imageAdapter.mThumbIds[(body+1)+15] = 8;
        imageAdapter.mThumbIds[(body-15)+15] = 8;

        //convert back of robot to explored
        imageAdapter.mThumbIds[(body-14)] = 1;
        imageAdapter.mThumbIds[(body-15)] = 1;
        imageAdapter.mThumbIds[(body-16)] = 1;
    }


    public void turnLeft(int head, int body){
        if(head == body+1){ //face right
            head = turnLeftwhenFaceRight(head, body);
        }
        else if(head == body-1) { //face left
            head = turnLeftwhenFaceLeft(head, body);
        }
        else if(head == body-15) { //face up
            head = turnLeftwhenFaceUp(head, body);
        }
        else{ //face down
            head = turnLeftwhenFaceDown(head, body);
        }
    }

    public void moveForward(int head, int body){

    }

    public void turnRight(int head, int body){
        if(head == body+1){ //face right
            head = turnRightwhenFaceRight(head, body);
            MainActivity.sendMessage("tr");
        }
        else if(head == body-1) { //face left
            head = turnRightwhenFaceLeft(head, body);

        }
        else if(head == body-15) { //face up

            head = turnRightwhenFaceUp(head, body);

        }
        else{ //face down
            head = turnRightwhenFaceDown(head, body);
        }
    }

}
