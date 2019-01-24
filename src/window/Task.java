package window;

import javax.management.relation.Relation;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import static window.AdditionalWindow.showAlert;
import static window.Data.tasks;

public class Task implements Serializable {
    public static final AtomicInteger autoIncrementID = new AtomicInteger(DataReader.getTaskInitialID());

    private int taskID;
    private Date startDate;
    private Date endDate;
    private int employeeID;
    private String taskName;
    private String description;
    private LinkedList<TaskRelation> relationsTo;
    private LinkedList<TaskRelation> relationsFrom;
    public Task(Date startDate, Date endDate, int employeeID, String taskName){
        this(startDate, endDate, employeeID,taskName,"");
    }
    public Task(Date startDate, Date endDate, int employeeID, String taskName, String description){
        this.taskID = autoIncrementID.incrementAndGet();
        this.startDate=startDate;
        this.endDate=endDate;
        this.employeeID=employeeID;
        this.taskName=taskName;
        this.description=description;
        relationsTo=new LinkedList<>();
        relationsFrom=new LinkedList<>();
    }
    public int getTaskID() { return this.taskID; }
    public Date getStartDate() { return this.startDate; }
    public Date getEndDate() { return this.endDate; }
    public int getEmployeeID() { return this.employeeID; }
    public String getTaskName() { return this.taskName; }
    public String getDescription() { return this.description; }
    public LinkedList<TaskRelation> getRelationsTo(){ return this.relationsTo; }
    public LinkedList<TaskRelation> getRelationsFrom(){ return this.relationsFrom; }
    public void setStartDate(Date startDate){
        this.startDate=startDate;
    }
    public void setEndDate(Date endDate){
        this.endDate=endDate;
    }
    public void setEmployeeID(int employeeID){
        this.employeeID=employeeID;
    }
    public void setTaskName(String taskName){
        this.taskName=taskName;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public void addRelationTo(TaskRelation relationTo){
        this.relationsTo.add(relationTo);
    }
    public void addRelationFrom(TaskRelation relationFrom){
        this.relationsFrom.add(relationFrom);
    }
    public void removeRelationTo(TaskRelation relationTo){
        this.relationsTo.remove(relationTo);
    }
    public void removeRelationFrom(TaskRelation relationFrom){
        this.relationsFrom.remove(relationFrom);
    }
    @Override
    public String toString(){
        return "ID: "+taskID+", date:("+startDate.toString()+", "+endDate.toString()+"), EmployeeID: "+employeeID+", Name: "+taskName+", Desc: "+description;
    }

    public static int nextID(){ return autoIncrementID.get(); }
    public static Task findInLinkedListByID(LinkedList<Task> list, int id){
        for(Task o : list)
            if (o.getTaskID()==id)
                return o;
        return null;
    }
    public static void removeFromLinkedList(LinkedList<Task> list, Task o){
        list.remove(o);
    }


    public boolean checkPossibility(LinkedList<Task> list, Date newStartDate, Date newEndDate, int newEmployeeID){
        for(Task o : list) {
            if (o.getTaskID() == this.taskID)
                continue;
            if (o.getEmployeeID() == newEmployeeID) {
                if ((!newStartDate.before(o.startDate) && !newStartDate.after(o.endDate))
                    ||(!newEndDate.before(o.startDate) && !newEndDate.after(o.endDate))
                    ||(!newStartDate.after(o.startDate) && !newEndDate.before(o.endDate))){
                    showAlert("It's impossible to do 2 Tasks at the same time!");
                    return false;
                }
            }
            for(TaskRelation r : this.getRelationsTo()){
                Task targetTask = Task.findInLinkedListByID(tasks,r.getTaskIDFrom());
                System.out.println(targetTask.toString());
                if ((r.getRelation()==TaskRelationType.SS && newStartDate.before(targetTask.getStartDate()))
                        ||(r.getRelation()==TaskRelationType.SF && !newEndDate.after(targetTask.getStartDate()))
                        ||(r.getRelation()==TaskRelationType.FS && !newStartDate.after(targetTask.getStartDate()))
                        ||(r.getRelation()==TaskRelationType.FF && !newEndDate.after(targetTask.getEndDate()))){
                    showAlert("Cannot change Task, because existing relations forbidding this!");
                    return false;
                }
            }
            for(TaskRelation r : this.getRelationsFrom()){
                Task taskTo = Task.findInLinkedListByID(tasks,r.getTaskIDTo());
                if ((r.getRelation()==TaskRelationType.SS && taskTo.getStartDate().before(newStartDate))
                        ||(r.getRelation()==TaskRelationType.SF && !taskTo.getStartDate().after(newEndDate))
                        ||(r.getRelation()==TaskRelationType.FS && !taskTo.getStartDate().after(newStartDate))
                        ||(r.getRelation()==TaskRelationType.FF && !taskTo.getEndDate().after(newEndDate))){
                    showAlert("Cannot change Task, because existing relations forbidding this!");
                    return false;
                }
            }
        }
        return true;
    }
}
