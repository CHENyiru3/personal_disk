public class Die {
    private int facevalue = 1;
    public int roll(){
        facevalue = Math.random()*6+1;
    }
}
