package window;

import java.io.Serializable;
import java.util.Optional;

import static window.Data.tasks;

public class TaskRelation implements Serializable {
    private int taskIDTo;
    private int taskIDFrom;
    private TaskRelationType relation;

    public TaskRelation(int taskIDTo, int taskIDFrom, TaskRelationType relation){
        this.taskIDTo=taskIDTo;
        this.taskIDFrom=taskIDFrom;
        this.relation=relation;
    }

    public int getTaskIDTo() {
        return this.taskIDTo;
    }
    public int getTaskIDFrom() { return this.taskIDFrom; }
    public TaskRelationType getRelation() {
        return this.relation;
    }
    @Override
    public String toString(){
        Task taskTo=Task.findInLinkedListByID(tasks,this.taskIDTo);
        Task taskFrom=Task.findInLinkedListByID(tasks,this.taskIDFrom);
        return this.relation+": "+taskFrom.getTaskName()+" -> "+taskTo.getTaskName();
    }

    public static TaskRelation unPack(Optional<TaskRelation> o){
        if(o.isPresent())
            return o.get();
        return null;
    }
}
