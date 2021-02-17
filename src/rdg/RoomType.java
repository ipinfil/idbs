package rdg;

import java.math.BigDecimal;
/**
 * @author Filip Lajcin
 * This class represents a row in table room_types in database.
 */
public class RoomType {
    Integer id;
    BigDecimal monthlyPayment;
    int capacity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
