package my.project.fer.ryzetello.ryzetellowebserver.model;

import java.util.ArrayList;
import java.util.List;

public class Command {

    private String name;
    private List<String> params = new ArrayList<>();

    public Command(String name) {
        this.name = name;
    }

    public Command(String name, List<String> params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

}
