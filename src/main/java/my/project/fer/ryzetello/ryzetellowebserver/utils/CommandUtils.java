package my.project.fer.ryzetello.ryzetellowebserver.utils;

import my.project.fer.ryzetello.ryzetellowebserver.model.Command;

public class CommandUtils {

    public static String construct(Command command) {
        StringBuilder stringBuilder = new StringBuilder(command.getName());

        if (!command.getParams().isEmpty()) {
            for (String param : command.getParams()) {
                stringBuilder.append(String.format(" %s", param));
            }
        }

        return stringBuilder.toString();
    }

}
