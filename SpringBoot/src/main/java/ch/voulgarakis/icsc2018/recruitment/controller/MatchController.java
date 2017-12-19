package ch.voulgarakis.icsc2018.recruitment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import ch.voulgarakis.icsc2018.recruitment.events.WebsocketSessionTracker;
import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;
import ch.voulgarakis.icsc2018.recruitment.utils.ApplicationEvent;

@RestController
public class MatchController {
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private WebsocketSessionTracker websocketSessionTracker;

    /**
     * Send the result of an application to the websocket topic holding an history of applications.
     */
    public void notify(Applicant applicant, Vacancy vacancy, double fitRatio) {
        // Send to the public topic...
        template.convertAndSend("/topic/applications",
                new ApplicationEvent(applicant.getName(), vacancy.getName(), fitRatio));
        // And now send to all the registered users... (in their own specific queue!)
        websocketSessionTracker.sessions().parallelStream().forEach(session -> template.convertAndSendToUser(session,
                "/queue/applications", new ApplicationEvent(applicant.getName(), vacancy.getName(), fitRatio)));
    }
}
