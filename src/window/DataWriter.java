package window;

import java.io.*;
import java.util.LinkedList;


public class DataWriter extends Data{
    private static void saveDataToFile(Object data, String fileName){
        try {
            FileOutputStream fout = new FileOutputStream("E:\\Projekty_Intellij\\Gantt\\src\\window\\"+fileName+".data");
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void saveEmployees(){
        saveDataToFile(employees,"employees");
    }
    private static void saveTasks(){
        saveDataToFile(tasks,"tasks");
    }
    private static void saveConfig(){
        LinkedList<Integer> config = new LinkedList<>();
        config.add(Employee.nextID());
        config.add(Task.nextID());
        saveDataToFile(config,"config");
    }
    public static void saveData(){
        saveEmployees();
        saveTasks();
        saveConfig();
    }
}
