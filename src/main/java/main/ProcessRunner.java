package main;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessRunner extends Task<Void> {

    private int exitValue;
    private String _command;
    private Process process;

    public ProcessRunner(String command){
        _command=command;
    }

    @Override
    protected Void call() throws Exception {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("bash","-c",_command);
        process = builder.start();
        exitValue = process.waitFor();
        if (process.waitFor() != 0) {
            BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorString = errorStream.readLine();
            throw new Exception(errorString);
        }
        return null;
    }

    @Override
    protected void cancelled() {
        process.destroy();
    }

    public int getExitValue() {
        return exitValue;
    }
}
