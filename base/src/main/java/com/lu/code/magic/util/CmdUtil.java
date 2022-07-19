package com.lu.code.magic.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class CmdUtil {

    private static String LINE_SEP = "\n";

    public static void run(String... commands) {
        exec(commands, false, false);
    }

    public static CommandResult exec(String command) {
        return exec(new String[]{command}, false, true);
    }

    public static CommandResult exec(String[] commands, boolean isRooted, boolean isNeedResultMsg) {
        int pExitCode = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(pExitCode, "", "", commands);
        }

        Process process = null;
        OutputStream outStream = null;
        InputStream errorStream = null;
        InputStream successStream = null;
        String successMsg = null;
        String errorMsg = null;
        try {
            process = Runtime.getRuntime().exec(isRooted ? "su" : "sh");
            outStream = process.getOutputStream();
            for (String command : commands) {
                if (command == null) continue;
                IOUtil.writeByString(command + "\n", outStream);
                outStream.flush();
            }
            //退出
            IOUtil.writeByString("exit\n", outStream);
            outStream.flush();

            //等待结果
            pExitCode = process.waitFor();
            if (isNeedResultMsg) {
                successStream = process.getInputStream();
                errorStream = process.getErrorStream();

                successMsg = IOUtil.readToString(successStream);
                errorMsg = IOUtil.readToString(errorStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(outStream, successStream, errorStream);
            if (process != null) {
                process.destroy();
            }
        }
        successMsg = (successMsg == null) ? "" : successMsg;
        errorMsg = (errorMsg == null) ? "" : errorMsg;
        return new CommandResult(pExitCode, successMsg, errorMsg, commands);
    }


    public static class CommandResult {
        public int code;
        public String successMsg;
        public String errorMsg;
        public String[] commands;

        public CommandResult(int code, String successMsg, String errorMsg, String[] commands) {
            this.code = code;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
            this.commands = commands;
        }

        @Override
        public String toString() {
            return "CommandResult{" +
                    "code=" + code +
                    ", successMsg='" + successMsg + '\'' +
                    ", errorMsg='" + errorMsg + '\'' +
                    ", commands=" + Arrays.toString(commands) +
                    '}';
        }
    }
}
