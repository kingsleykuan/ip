/**
 * Todo task without any date/time attached to it.
 */
public class TodoTask extends Task {
    public TodoTask(String taskArgs) {
        super(taskArgs);
    }

    private TodoTask(String description, boolean isDone) {
        super(description, isDone);
    }

    @Override
    public TodoTask markAsDone() {
        return new TodoTask(this.description, true);
    }

    @Override
    public String toString() {
        String taskFormat = "[%s][%s] %s";
        return String.format(
                taskFormat,
                this.getTypeIcon(),
                this.getStatusIcon(),
                this.description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }
}
