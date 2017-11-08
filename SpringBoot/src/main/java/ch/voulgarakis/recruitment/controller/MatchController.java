package ch.voulgarakis.recruitment.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import ch.voulgarakis.recruitment.model.Applicant;
import ch.voulgarakis.recruitment.model.Vacancy;
import ch.voulgarakis.recruitment.utils.ApplicationResult;

@RestController
@MessageMapping("/with")
public class MatchController {
    /**
     * Send the result of an application to the websocket topic holding an history of applications.
     */
    @SendTo("/topic/applications")
    public ApplicationResult notify(Applicant applicant, Vacancy vacancy, boolean match) {
        return new ApplicationResult(applicant.getName(), vacancy.getName(), match);
    }
}
