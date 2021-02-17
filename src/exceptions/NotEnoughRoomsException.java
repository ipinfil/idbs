package exceptions;

import java.util.ArrayList;
import java.util.List;

public class NotEnoughRoomsException extends RuntimeException{
    int assignedRooms = 0;
    public NotEnoughRoomsException() {
        super();
    }

    public int getAssignedRooms() {
        return assignedRooms;
    }

    public void setAssignedRooms(int info) {
        this.assignedRooms = info;
    }
}
