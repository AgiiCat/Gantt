package window;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Employee implements Serializable {
    private static final AtomicInteger autoIncrementID = new AtomicInteger(DataReader.getEmployeeInitialID());

    private int employeeID;
    private String name;
    public Employee(String name){
        this.employeeID = autoIncrementID.incrementAndGet();
        this.name=name;
    }

    public int getEmployeeID() {return employeeID; }
    public String getName() {
        return name;
    }
    public void setName(String name){ this.name=name;}
    @Override
    public String toString(){
        return employeeID+": "+name;
    }

    public static int nextID(){ return autoIncrementID.get();}
    public static Employee findInLinkedListByID(LinkedList<Employee> list, int id){
        for(Employee o : list)
            if (o.getEmployeeID()==id)
                return o;
        return null;
    }
    public static int getIndexOfElementByID(LinkedList<Employee> list, int id){
        int i=0;
        for(Employee o : list) {
            if (o.getEmployeeID()==id)
                return i;
            i++;
        }
        return -1;
    }
    public static void removeFromLinkedList(LinkedList<Employee> list, Employee o){
        list.remove(o);
    }
}
