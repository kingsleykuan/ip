package owen;

import owen.command.Command;
import owen.task.TaskList;

/**
 * Owen is a personal assistant chatbot.
 * As Owen is an immutable class, modifications will return a copy
 * with updated internal state.
 */
public class Owen implements Chatbot {
    private static final String STORAGE_PATH = "data/owen.txt";

    private final boolean isRunning;
    private final String latestResponse;
    private final TaskList taskList;
    private final Storage storage;

    /**
     * Creates an Owen chatbot with a hello response.
     */
    public Owen() {
        this.isRunning = true;

        StringBuilder stringBuilder = new StringBuilder();
        String logo = ""
                + " /\\_/\\ \n"
                + "((OvO))\n"
                + "():::()\n"
                + " VV-VV \n";
        stringBuilder.append(logo);
        stringBuilder.append("\nHello I am Owen the Owl!");
        this.latestResponse = stringBuilder.toString();

        this.storage = new Storage(STORAGE_PATH);
        this.taskList = this.storage.readTaskList();
    }

    private Owen(boolean isRunning, String latestResponse, TaskList taskList, Storage storage) {
        this.isRunning = isRunning;
        this.latestResponse = latestResponse;
        this.taskList = taskList;
        this.storage = storage;
    }

    @Override
    public Owen shutdown() {
        String shutdownResponse = "Bye. Hope to see you again soon!";
        return new Owen(false, shutdownResponse, this.taskList, this.storage);
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public String getResponse() {
        return this.latestResponse;
    }

    @Override
    public Owen parseCommand(String commandString) {
        try {
            Command command = Parser.parseCommand(commandString);

            switch (command.getType()) {
            case TODO:
            case EVENT:
            case DEADLINE:
                return this.addTask(command.getOriginal());
            case LIST:
                return this.listTasks();
            case DONE:
                return this.doneTask(parseTaskNumber(command.getArgs()));
            case DELETE:
                return this.deleteTask(parseTaskNumber(command.getArgs()));
            case FIND:
                return this.findTask(command.getArgs());
            case BYE:
                return this.shutdown();
            default:
                throw new OwenException("I'm sorry, but I don't know what that means...");
            }
        } catch (OwenException exception) {
            String exceptionResponse = exception.getMessage();
            return new Owen(this.isRunning, exceptionResponse, this.taskList, this.storage);
        }
    }

    private static int parseTaskNumber(String taskNumber) throws OwenException {
        try {
            return Integer.parseInt(taskNumber);
        } catch (NumberFormatException exception) {
            throw new OwenException("Task number must be specified...");
        }
    }

    /**
     * Adds task to Owen task list.
     * 
     * @param task Task string to add.
     * @return Copy of Owen updated with new task and response.
     * @throws OwenException Task could not be parsed.
     */
    private Owen addTask(String task) throws OwenException {
        TaskList addedTaskList = this.taskList.addTask(task);
        int numTasks = addedTaskList.getNumTasks();
        String addedFormat = ""
                + "Got it. I've added this task:\n"
                + "    %s\n"
                + "Now you have %d tasks in the list.";
        String addedResponse = String.format(
                addedFormat, addedTaskList.getTask(numTasks), numTasks);
        Storage addedStorage = this.storage.writeTaskList(addedTaskList);
        return new Owen(this.isRunning, addedResponse, addedTaskList, addedStorage);
    }

    /**
     * Lists tasks in Owen task list.
     * 
     * @return Copy of Owen updated with new response.
     */
    private Owen listTasks() {
        String listResponse = this.taskList.toString();
        return new Owen(this.isRunning, listResponse, this.taskList, this.storage);
    }

    /**
     * Marks task as done in Owen task list.
     * 
     * @param taskNumber Task number to mark as done.
     * @return Copy of Owen updated with modified task list and response.
     * @throws OwenException Task number does not exist.
     */
    private Owen doneTask(int taskNumber) throws OwenException {
        TaskList doneTaskList = this.taskList.markAsDone(taskNumber);
        String doneFormat = "Nice! I've marked this task as done:\n    %s";
        String doneResponse = String.format(
                doneFormat, doneTaskList.getTask(taskNumber).toString());
        Storage doneStorage = this.storage.writeTaskList(doneTaskList);
        return new Owen(this.isRunning, doneResponse, doneTaskList, doneStorage);
    }

    /**
     * Delete task from Owen task list.
     * 
     * @param taskNumber Task number to delete.
     * @return Copy of Owen updated with modified task list and response.
     * @throws OwenException Task number does not exist.
     */
    private Owen deleteTask(int taskNumber) throws OwenException {
        TaskList deleteTaskList = this.taskList.deleteTask(taskNumber);
        String deleteFormat = ""
                + "Noted. I've removed this task:\n"
                + "    %s\n"
                + "Now you have %d tasks in the list.";
        int newNumTasks = deleteTaskList.getNumTasks();
        String deleteResponse = String.format(
                deleteFormat, this.taskList.getTask(taskNumber), newNumTasks);
        Storage deleteStorage = this.storage.writeTaskList(deleteTaskList);
        return new Owen(this.isRunning, deleteResponse, deleteTaskList, deleteStorage);
    }

    /**
     * Finds task in Owen task list.
     * 
     * @param searchString Search string to search within tasks.
     * @return Copy of Owen with updated response.
     */
    private Owen findTask(String searchString) {
        String findResult = this.taskList.findTask(searchString);
        String findFormat = "Here are the matching tasks in your list:\n%s";
        String findResponse = String.format(findFormat, findResult);
        return new Owen(this.isRunning, findResponse, this.taskList, this.storage);
    }
}
