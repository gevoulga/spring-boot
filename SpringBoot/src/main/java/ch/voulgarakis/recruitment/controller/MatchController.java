package ch.voulgarakis.recruitment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import ch.voulgarakis.recruitment.model.Applicant;
import ch.voulgarakis.recruitment.model.Vacancy;
import ch.voulgarakis.recruitment.utils.ApplicationResult;

@RestController
public class MatchController {
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Send the result of an application to the websocket topic holding an history of applications.
     */
    // @SendTo("/topic/applications")
    public void notify(Applicant applicant, Vacancy vacancy, boolean match) {
        template.convertAndSend("/topic/applications",
                new ApplicationResult(applicant.getName(), vacancy.getName(), match));
        // return new ApplicationResult(applicant.getName(), vacancy.getName(), match);
    }
}
