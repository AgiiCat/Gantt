package window;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;


public class DataReader extends Data{
    private static Object readObject(String fileName){
        try {
            FileInputStream fileIStream = new FileInputStream("E:\\Projekty_Intellij\\Gantt\\src\\window\\"+fileName+".data");
            if(fileIStream==null)
                return null;
            ObjectInputStream is = new ObjectInputStream(fileIStream);
            if(is==null)
                return null;
            return is.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void readEmployeeList(){
        try {
            employees.addAll((LinkedList<Employee>) readObject("employees"));
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public static void readTaskList(){
        try {
            tasks.addAll((LinkedList<Task>) readObject("tasks"));
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public static void readConfig(){
        LinkedList<Integer> list = new LinkedList<>();
        try {
            list.addAll((LinkedList<Integer>) readObject("config"));
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        int i=0;
        for(Integer o : list){
            switch (i){
                case 0:
                    employeeInitialID=o;
                    break;
                case 1:
                    taskInitialID=o;
                    break;
            }
            i++;
        }
    }
    public static int getEmployeeInitialID(){
        readConfig();
        return employeeInitialID;
    }
    public static int getTaskInitialID(){
        readConfig();
        return taskInitialID;
    }
}
