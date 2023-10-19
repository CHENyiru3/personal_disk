public class SecondeHandVehicle extends Vehicle {
    private int numberOfOwners;
    public SecondeHandVehicle(String new regNo,String make, int yearOfManufacture,double value,int numberOfOwners){
        this.numberOfOwners=numberOfOwners;
        this.regNo=regNo;
        this.make=make;
        this.yearOfManufacture=yearOfManufacture;
        this.value=value;
    }

    public int getNumberOfOwners() {
        return numberOfOwners;
    }
    public hasMutipleOwners{
        boolean a=(numberOfOwners>1);
        return a;

    }
}
