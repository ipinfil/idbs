package rdg;

/**
 * @author Filip Lajcin
 * This class represents unpopularity statistics for certain room.
 */

public class Unpopularity {
    int order;
    Integer room_id;
    int numberOfReaccommodations;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Integer getRoom_id() {
        return room_id;
    }

    public void setRoom_id(Integer room_id) {
        this.room_id = room_id;
    }

    public int getNumberOfReaccommodations() {
        return numberOfReaccommodations;
    }

    public void setNumberOfReaccommodations(int numberOfReaccommodations) {
        this.numberOfReaccommodations = numberOfReaccommodations;
    }
}
