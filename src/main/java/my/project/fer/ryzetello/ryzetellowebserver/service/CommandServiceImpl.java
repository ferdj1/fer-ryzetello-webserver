package my.project.fer.ryzetello.ryzetellowebserver.service;

import my.project.fer.ryzetello.ryzetellowebserver.model.Command;
import my.project.fer.ryzetello.ryzetellowebserver.udp.UdpSenderService;
import my.project.fer.ryzetello.ryzetellowebserver.utils.CommandUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandServiceImpl implements CommandService {

    private final UdpSenderService udpCommunicationService;

    @Autowired
    public CommandServiceImpl(UdpSenderService udpCommunicationService) {
        this.udpCommunicationService = udpCommunicationService;
    }

    @Override
    public boolean execute(Command command) {
        String commandStr = CommandUtils.construct(command);

        udpCommunicationService.sendMessage("localhost", 12345, commandStr);

        return true;
    }

}
