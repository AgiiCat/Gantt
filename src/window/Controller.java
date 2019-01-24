package window;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import static window.Data.employees;
import static window.Data.tasks;


public class Controller implements Initializable {
    @FXML private ScrollPane startPane;

    public static final SimpleDateFormat finalDateFormatWithYear = new SimpleDateFormat("dd.MM.yy");
    public static final SimpleDateFormat finalDateFormatWithoutYear = new SimpleDateFormat("dd.MM");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DataReader.readEmployeeList();
        DataReader.readTaskList();
//        loadDebugData();
        reloadGrid();
    }
    private void loadDebugData(){
        employees.add(new Employee("Jan Kowalski"));
        employees.add(new Employee("Janusz Zygfryd"));
        employees.add(new Employee("Janusz Kowalski"));
        try{
            tasks.add(new Task(finalDateFormatWithYear.parse("01.01.19"),finalDateFormatWithYear.parse("04.01.19"),1,"BEST task"));
            tasks.add(new Task(finalDateFormatWithYear.parse("02.01.19"),finalDateFormatWithYear.parse("05.01.19"),2,"To do work"));
            tasks.add(new Task(finalDateFormatWithYear.parse("01.01.19"),finalDateFormatWithYear.parse("04.02.19"),3,"get some stuff"));
            tasks.add(new Task(finalDateFormatWithYear.parse("17.01.19"),finalDateFormatWithYear.parse("19.01.19"),1, "Important things"));
        } catch (Exception e){
        }
    }
    public void reloadGrid(){
        GridPane table = new GridPane();
        table.setId("dataGrid");
        table.gridLinesVisibleProperty().set(true);

        startPane.setFitToHeight(true);
        startPane.setFitToWidth(true);

        Text textElement = new Text("Employees list:");
        table.add(textElement,0,0);

        Date firstDate = getFirstDate();
        Date lastDate = getLastDate();

        SimpleDateFormat dateFormat = finalDateFormatWithoutYear;
        if(variousYears(firstDate,lastDate))
            dateFormat = finalDateFormatWithYear;

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(20.0);
        cc.setFillWidth(true);
        cc.setHgrow( Priority.ALWAYS );
        table.getColumnConstraints().add(cc);

        int horizontalGridCount=daysBetween(firstDate, lastDate)+1;
        for (int j = 0; j < horizontalGridCount; ++j) {
            cc = new ColumnConstraints();
            cc.setPercentWidth(80.0/horizontalGridCount);
            cc.setFillWidth(true);
            cc.setHgrow( Priority.ALWAYS );
            table.getColumnConstraints().add(cc);
        }

        Calendar incrementDate = Calendar.getInstance();
        incrementDate.setTime(firstDate);
        Calendar actualDate = Calendar.getInstance();
        actualDate.setTime(new Date());
        for(int i = 0; i < horizontalGridCount;i++){
            Text textDate = new Text(dateFormat.format(incrementDate.getTime()));
            StackPane backGroundPane = new StackPane();
            if(dateFormat.format(incrementDate.getTime()).equals(dateFormat.format(actualDate.getTime())))
                backGroundPane.setStyle("-fx-background-color: rgba(255, 215, 0, 0.5);");
            backGroundPane.getChildren().add(textDate);
            table.add(backGroundPane,i+1, 0);
            incrementDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        {
            int i = 0;
            for (Employee o : employees) {
                Text textEmployee = new Text();
                textEmployee.setText(o.toString()+" -->");
                textEmployee.getStyleClass().add("employeeElement");
                textEmployee.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        AdditionalWindow.showEditEmployeeWindow(o.getEmployeeID());
                        reloadGrid();
                    }
                });
                table.add(textEmployee,0, ++i);
            }
        }
        for (Task o : tasks) {
            StackPane task = new StackPane();
            task.setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");
            task.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    AdditionalWindow.showEditTaskWindow(o.getTaskID());
                    reloadGrid();
                }
            });
            Label textDescription = new Label(o.getTaskName());
            textDescription.setWrapText(true);
            task.getChildren().add(textDescription);
            int EmployeeColumn = Employee.getIndexOfElementByID(employees,o.getEmployeeID());
            if(EmployeeColumn!=-1)
                table.add(task,daysBetween(firstDate,o.getStartDate())+1,EmployeeColumn+1,daysBetween(o.getStartDate(),o.getEndDate())+1,1);
        }
        startPane.setContent(table);
        DataWriter.saveData();
    }

    private Date getFirstDate(){
        if(tasks.isEmpty())
            return new Date();
        Date result = tasks.getFirst().getStartDate();
        for(Task o : tasks)
            if (o.getStartDate().before(result))
                result=o.getStartDate();
        return result;
    }
    private Date getLastDate(){
        if(tasks.isEmpty())
            return new Date();
        Date result = tasks.getFirst().getEndDate();
        for(Task o : tasks)
            if (o.getEndDate().after(result))
                result=o.getEndDate();
        return result;
    }
    private boolean variousYears(Date a,Date b){
        Calendar calA = Calendar.getInstance();
        Calendar calB = Calendar.getInstance();
        calA.setTime(a);
        calB.setTime(b);
        int yearA = calA.get(Calendar.YEAR);
        int yearB = calB.get(Calendar.YEAR);
        return yearA != yearB;
    }
    public static int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public void addEmployee() {
        AdditionalWindow.showAddEmployeeWindow();
        reloadGrid();
    }
    public void addTask() {
        AdditionalWindow.showAddTaskWindow();
        reloadGrid();
        //test
    }
}
