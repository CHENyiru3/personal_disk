import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;
public class Main{
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        ArrayList<String[]> array_str=new ArrayList<String[]>();
        String[] tem_str=new String[1];


        while(true){
            tem_str[0]=in.nextLine();
            if (tem_str[0].equals("END")){
                break;
            }else{
                array_str.add(tem_str);
            }
        }
        int count_lines=array_str.size();

        HashSet<String> tem_num=new HashSet<String>();
        ArrayList<String> first_line=new ArrayList<String>();
        first_line.add("student id");
        first_line.add("name");
        System.out.print(array_str.get(0));
        String tem_String;
        ArrayList<String> tem_data= new ArrayList<String>();
        ArrayList<ArrayList<String>> data=new ArrayList<ArrayList<String>>();
        for(int i=0;i<count_lines;i++){
            tem_str=array_str.get(i);
            tem_String=tem_str[0];
            ;
        }

    }
}