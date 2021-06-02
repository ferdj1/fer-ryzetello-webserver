package my.project.fer.ryzetello.ryzetellowebserver.service;

import my.project.fer.ryzetello.ryzetellowebserver.model.Command;
import my.project.fer.ryzetello.ryzetellowebserver.model.Drone;
import my.project.fer.ryzetello.ryzetellowebserver.tcp.TcpSenderService;
import my.project.fer.ryzetello.ryzetellowebserver.udp.UdpSenderService;
import my.project.fer.ryzetello.ryzetellowebserver.utils.CommandUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandServiceImpl implements CommandService {

    private final UdpSenderService udpSenderService;
    private final TcpSenderService tcpSenderService;
    private final DroneService droneService;

    @Autowired
    public CommandServiceImpl(UdpSenderService udpSenderService,
        TcpSenderService tcpSenderService, DroneService droneService) {
        this.udpSenderService = udpSenderService;
        this.tcpSenderService = tcpSenderService;
        this.droneService = droneService;
    }

    @Override
    public boolean execute(Command command) {
        String commandStr = CommandUtils.construct(command);

        // Load client data
        if (droneService.existsById(command.getDroneId())) {
            final Drone drone = droneService.getDrone(command.getDroneId());

            final String clientHost = drone.getHost();
            final int clientPort = drone.getPort();
            //udpSenderService.sendMessage(clientHost, clientPort, commandStr);
            tcpSenderService.sendMessage(clientHost, clientPort, commandStr);

            return true;
        }

        return false;
    }

}
