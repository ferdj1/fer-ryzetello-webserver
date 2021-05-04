package my.project.fer.ryzetello.ryzetellowebserver.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Command {

    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    private UUID droneId;
    private String name;
    private List<String> params = new ArrayList<>();

    public Command() {
    }

    public Command(UUID droneId, String name) {
        this.droneId = droneId;
        this.name = name;
    }

    public Command(UUID droneId, String name, List<String> params) {
        this.name = name;
        this.params = params;
    }

    public UUID getDroneId() {
        return droneId;
    }

    public void setDroneId(UUID droneId) {
        this.droneId = droneId;
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
