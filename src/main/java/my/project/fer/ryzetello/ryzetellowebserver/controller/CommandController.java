package my.project.fer.ryzetello.ryzetellowebserver.controller;

import my.project.fer.ryzetello.ryzetellowebserver.model.Command;
import my.project.fer.ryzetello.ryzetellowebserver.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/command")
public class CommandController {

    private final CommandService commandService;

    @Autowired
    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping("/execute")
    public ResponseEntity<Boolean> execute(@RequestBody Command command) {
        boolean success = commandService.execute(command);

        return ResponseEntity.ok(success);
    }

}
