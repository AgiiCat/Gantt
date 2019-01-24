package window;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;


import static window.Data.employees;
import static window.Data.tasks;


public class AdditionalWindow {
    public static void showAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showAlert(String message) {
        showAlert(message,"Error!");
    }

    public static void showAddEmployeeWindow() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Adding Employee to Gantt chart.");
        dialog.setHeaderText("Enter Employee name.");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            if(name == null || name.isEmpty()){
                showAlert("Wrong Employee name!");
                return;
            }
            Employee newemployee = new Employee(name);
            employees.add(newemployee);
//            System.out.println(newemployee);
        });
    }
    public static void showAddTaskWindow() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Adding new Task to Gantt chart.");
        dialog.setHeaderText("Enter Task details.");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        DatePicker startDate = new DatePicker(LocalDate.now());
        DatePicker endDate = new DatePicker(LocalDate.now());

        ObservableList<Employee> observableListEmployee = FXCollections.observableArrayList(employees);
        ChoiceBox<Employee> employee = new ChoiceBox<>();
        employee.setItems(observableListEmployee);

        TextField taskName = new TextField();
        TextArea description = new TextArea();
        GridPane content = new GridPane();

        content.add(new Text("Start date:"),0,0);
        content.add(new Text("End date:"),0,1);
        content.add(new Text("Employee:"),0,2);
        content.add(new Text("Task Name:"),0,3);
        content.add(new Text("Description:"),0,4);
        content.add(startDate,1,0);
        content.add(endDate,1,1);
        content.add(employee,1,2);
        content.add(taskName,1,3);
        content.add(description,1,4);
        dialogPane.setContent(content);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                Date convStartDate = Date.from(Instant.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault())));
                Date convEndDate = Date.from(Instant.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault())));
                if(convStartDate.after(convEndDate)){
                    showAlert("Choosed wrong dates!");
                    return null;
                }
                if(employee.getValue() == null){
                    showAlert("Didn't choose Employee!");
                    return null;
                }
                if(taskName.getText() == null || taskName.getText().isEmpty()){
                    showAlert("Please Insert Job name!");
                    return null;
                }
                return new Task(convStartDate, convEndDate,employee.getValue().getEmployeeID(), taskName.getText(), description.getText());
            }
            return null;
        });
        Optional<Task> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Task result) -> {
            tasks.add(result);
//            System.out.println(result);
        });
    }
    private static Text relationObject(TaskRelation r, Dialog<Task> dialog, int taskID){
        Text textRelation = new Text();
        textRelation.setText(r.toString());

        textRelation.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if(showDeleteTaskRelationWindow(r)) {
                dialog.close();
                showEditTaskWindow(taskID);
            }
        });
        return textRelation;
    }
    private static boolean showDeleteTaskRelationWindow(TaskRelation r){
        Task taskTo=Task.findInLinkedListByID(tasks,r.getTaskIDTo());
        Task taskFrom=Task.findInLinkedListByID(tasks,r.getTaskIDFrom());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation deleting Task Relation");
        alert.setHeaderText(null);
        alert.setContentText("Do you want delete this relation?\n"+r.toString());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            taskTo.removeRelationTo(r);
            taskFrom.removeRelationFrom(r);
            return true;
        }
        return false;
    }
    public static void showEditTaskWindow(int taskID) {
        Task choosedTask = Task.findInLinkedListByID(tasks,taskID);

        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(new ButtonType("CONFIRM END"), new ButtonType("SAVE"),ButtonType.CANCEL);

        DatePicker startDate = new DatePicker(choosedTask.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        DatePicker endDate = new DatePicker(choosedTask.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        ChoiceBox<Employee> employee = new ChoiceBox<>();
        employee.setItems(FXCollections.observableArrayList(employees));

        TextField taskName = new TextField(choosedTask.getTaskName());
        TextArea description = new TextArea(choosedTask.getDescription());

        GridPane content = new GridPane();
        employee.getSelectionModel().select(Employee.getIndexOfElementByID(employees,choosedTask.getEmployeeID()));

        VBox relations = new VBox();
        for(TaskRelation r : choosedTask.getRelationsTo())
            relations.getChildren().add(relationObject(r, dialog, choosedTask.getTaskID()));
        for(TaskRelation r : choosedTask.getRelationsFrom())
            relations.getChildren().add(relationObject(r, dialog, choosedTask.getTaskID()));

        Button addRelationBtn = new Button("Add relation");
        addRelationBtn.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                TaskRelation result = TaskRelation.unPack(showAddTaskRelationWindow(choosedTask.getTaskID()));
                if(result!=null) {
                    relations.getChildren().add(relationObject(result, dialog, choosedTask.getTaskID()));
                }
            }
        });

        content.add(new Text("Start date:"),0,0);
        content.add(new Text("Employee:"),0,2);
        content.add(new Text("End date:"),0,1);
        content.add(new Text("Task Name:"),0,3);
        content.add(new Text("Description:"),0,4);
        content.add(addRelationBtn,2,0);
        content.add(new Text("Relations:"),2,1);
        content.add(startDate,1,0);
        content.add(employee,1,2);
        content.add(endDate,1,1);
        content.add(taskName,1,3);
        content.add(description,1,4);
        content.add(relations,2,2,1,3);
        dialogPane.setContent(content);

        dialog.setResultConverter((ButtonType button) -> {
            if (button.getText().equals("SAVE")) {
                Date convStartDate = Date.from(Instant.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault())));
                Date convEndDate = Date.from(Instant.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault())));
                if(convStartDate.after(convEndDate)){
                    showAlert("Choosed wrong dates! ");
                    return null;
                }
                if(employee.getValue() == null){
                    showAlert("Didn't choose Employee! ");
                    return null;
                }
                if(choosedTask.checkPossibility(tasks, convStartDate,convEndDate,employee.getValue().getEmployeeID())) {
                    choosedTask.setStartDate(convStartDate);
                    choosedTask.setEndDate(convEndDate);
                    choosedTask.setEmployeeID(employee.getValue().getEmployeeID());
                    choosedTask.setTaskName(taskName.getText());
                    choosedTask.setDescription(description.getText());
                    return choosedTask;
                }
            } else if (button.getText().equals("CONFIRM END")){
                Task.removeFromLinkedList(tasks, choosedTask);
            }
            return null;
        });
        Optional<Task> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Task result) -> {
//            System.out.println(result);
        });
    }
    public static void showEditEmployeeWindow(int employeeID){
        Employee choosedEmployee = Employee.findInLinkedListByID(employees,employeeID);

        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Edit Employee");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(new ButtonType("DELETE"), new ButtonType("SAVE"),ButtonType.CANCEL);

        TextField name = new TextField(choosedEmployee.getName());

        GridPane content = new GridPane();
        content.add(new Text("Name:"),0,0);
        content.add(name,1,0);
        dialogPane.setContent(content);

        dialog.setResultConverter((ButtonType button) -> {
            if (button.getText().equals("SAVE")) {
                if(name == null || name.getText().isEmpty()){
                    showAlert("Wrong Employee name!");
                    return null;
                }
                choosedEmployee.setName(name.getText());
                return choosedEmployee;
            } else if (button.getText().equals("DELETE")){
                for(Task o: tasks)
                    if(o.getEmployeeID()==choosedEmployee.getEmployeeID()){
                        showAlert("Cannot delete Employee. Have active task!");
                        return null;
                    }
                Employee.removeFromLinkedList(employees, choosedEmployee);
            }
            return null;
        });
        Optional<Employee> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Employee result) -> {
            System.out.println(result);
        });
    }
    public static Optional<TaskRelation> showAddTaskRelationWindow(int taskID){
        Task choosedTask = Task.findInLinkedListByID(tasks,taskID);

        Dialog<TaskRelation> dialog = new Dialog<>();
        dialog.setTitle("Add Relation to job: "+choosedTask.getTaskName());
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);

        ChoiceBox<Task> taskTo = new ChoiceBox<>();
        taskTo.setItems(FXCollections.observableArrayList(tasks));
        taskTo.setConverter(new TaskConverter());

        ComboBox<TaskRelationType> taskRelationType = new ComboBox<>();
        taskRelationType.getItems().setAll(TaskRelationType.values());

        GridPane content = new GridPane();
        content.add(new Text("Choosed Task:"),0,0);
        content.add(new Text("Target Task:"),0,1);
        content.add(new Text("Relation type:"),0,2);
        content.add(new Text(choosedTask.getTaskName()),1,0);
        content.add(taskTo,1,1);
        content.add(taskRelationType,1,2);
        dialogPane.setContent(content);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                if(taskTo.getValue().getTaskID()==choosedTask.getTaskID()){
                    showAlert("Cannot create relation from task to the same task");
                    return null;
                }
                Task targetTask = Task.findInLinkedListByID(tasks,taskTo.getValue().getTaskID());
                switch(taskRelationType.getValue()){
                    case SS:
                        if(choosedTask.getStartDate().before(targetTask.getStartDate())){
                            showAlert("Cannot create relation because Start Dates are not compatible");
                            return null;
                        }
                        break;
                    case SF:
                        if(!choosedTask.getEndDate().after(targetTask.getStartDate())){
                            showAlert("Cannot create relation because End Date is before target Start Date");
                            return null;
                        }
                        break;
                    case FS:
                        if(!choosedTask.getStartDate().after(targetTask.getStartDate())){
                            showAlert("Cannot create relation because Start Date is before target Start Date");
                            return null;
                        }
                        break;
                    case FF:
                        if(!choosedTask.getEndDate().after(targetTask.getEndDate())){
                            showAlert("Cannot create relation because End Date is before target End Date");
                            return null;
                        }
                }
                System.out.println(taskRelationType.getValue());
                TaskRelation result = new TaskRelation(choosedTask.getTaskID(),targetTask.getTaskID(),taskRelationType.getValue());
                choosedTask.addRelationTo(result);
                targetTask.addRelationFrom(result);
                return result;
            }
            return null;
        });
        Optional<TaskRelation> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((TaskRelation result) -> {
//            System.out.println(result);
        });
        return optionalResult;
    }
}
