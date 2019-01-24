package window;

import javafx.util.StringConverter;

public class TaskConverter extends StringConverter<Task> {
    @Override
    public String toString(Task object) {
        return object.getTaskName();
    }

    @Override
    public Task fromString(String string) {
        return null;
    }
}
