/**
 * An abstract task with a description and a status.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    protected Task() {
        this.description = "";
        this.isDone = false;
    }

    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    protected Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Mark task as done and return copy with updated state.
     * @return Copy of task with the state updated to done.
     */
    public abstract Task markAsDone();

    protected abstract String getTypeIcon();

    protected String getStatusIcon() {
        return (isDone ? "\u2713" : "\u2718"); //return tick or X symbols
    }
}
