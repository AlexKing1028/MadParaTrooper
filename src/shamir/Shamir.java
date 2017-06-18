package shamir;

import main.model.Equipment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wesley shi on 2017/6/17.
 */
public class Shamir {
    public static void main(String[] args){
        ArrayList<Equipment> a[] = new ArrayList[3];
        for (int i=0; i<3; i++){
            a[i] = new ArrayList<>();
        }
        for (int i=0; i<4; i++){
            Box b;
            if (i == 3){
                b = new Box(3);
            } else{
                b = new Box(2);
            }
            Key[] keys = b.Lock(3);
            for (int j=0; j<3; j++){
                Equipment equipment = new Equipment("100"+i, b, keys[j], "aaa"+i);
                a[j].add(equipment);
            }
        }
        try{
            for (int i=0; i<3; i++){
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("equipments"+i+".obj"));
                oos.writeObject(a[i]);
                oos.close();
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream("equipments"+i+".obj"));
                ArrayList<Equipment> ae = ((ArrayList<Equipment>) ois.readObject());
                for(Equipment e: ae){
                    System.out.println(e.getId()+ " " + e.getKey().toString());
                }
                System.out.println();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
