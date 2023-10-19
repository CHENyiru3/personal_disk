import java.util.*;
public class Test {
    public static void main(String[] args) {
        SecondHandVehicle car1 = new SecondHandVehicle("a", "a", 1990, 1200.2, 2);
        System.out.println(car1.getRegNo());
        System.out.println(car1.getMake());
        System.out.println(car1.getYearOfManufacture());
        System.out.println(car1.getValue());
        System.out.println(car1.getNumberOfOwners());
        System.out.println(car1.hasMultipleOwners());
    }
}
public class Vehicle {
    protected String regNo;
    protected String make;
    protected int yearOfManufacture;
    public double value;

    public Vehicle(String my_regNo,String my_make,int my_yearOfManufacture,double my_value){
        regNo=my_regNo;
        make=my_make;
        yearOfManufacture=my_yearOfManufacture;
        value=my_value;
    }

    public double getValue() {
        return value;
    }

    public int getYearOfManufacture() {
        return yearOfManufacture;
    }

    public String getMake() {
        return make;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int calculateAge(int now){
        int difference=now-yearOfManufacture;
        return difference;
    }
}

public class SecondHandVehicle extends Vehicle {
    private int numberOfOwners;
    public SecondHandVehicle(String newRegNo, String make, int yearOfManufacture, double value, int numberOfOwners) {
        super(newRegNo, make, yearOfManufacture, value);
        this.numberOfOwners = numberOfOwners;
    }

    public int getNumberOfOwners() {
        return numberOfOwners;
    }

    public boolean hasMultipleOwners() {
        return numberOfOwners > 1;
    }
}

