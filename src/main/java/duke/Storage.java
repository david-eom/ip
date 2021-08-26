package duke;

import duke.exception.DukeDatabaseException;
import duke.task.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    private String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }


    public Task[] loadData() throws DukeDatabaseException {
        File db = new File(this.filePath);
        ArrayList<Task> taskList = new ArrayList<>();
        try {
            Scanner fileReader = new Scanner(db);
            while (fileReader.hasNextLine()) {
                taskList.add(readEntry(fileReader.nextLine()));
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            this.createDatabase();
        }
        return taskList.toArray(new Task[0]);
    }


    private void createDatabase() throws DukeDatabaseException {
        File db = new File(this.filePath);
        File dir = new File(db.getParent());
        dir.mkdir();
        try {
            db.createNewFile();
        } catch (IOException e) {
            throw new DukeDatabaseException();
        }
    }


    private Task readEntry(String entry) {
        String[] fields = entry.split("\\|");
        Task taskToAdd;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        switch (fields[0]) {
        case "T":
            taskToAdd = new Todo(fields[2]);
            break;
        case "E":
            taskToAdd = new Event(fields[2],
                    LocalDateTime.parse(fields[3], formatter),
                    LocalDateTime.parse(fields[4], formatter));
            break;
        default:    // (case "D")
            taskToAdd = new Deadline(fields[2], LocalDateTime.parse(fields[3], formatter));
            break;
        }
        if (Integer.parseInt(fields[1]) == 1) {
            taskToAdd.markAsDone();
        }
        return taskToAdd;
    }


    public void saveData(TaskList taskList) throws IOException {
        FileWriter fw = new FileWriter(this.filePath, false);
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 1; i <= taskList.getLength(); i++) {
            bw.write(taskList.get(i).stringifyTask());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }
}