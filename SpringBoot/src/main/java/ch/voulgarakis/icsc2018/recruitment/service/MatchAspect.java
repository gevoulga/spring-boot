package ch.voulgarakis.icsc2018.recruitment.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.voulgarakis.icsc2018.recruitment.controller.MatchController;
import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;

@Aspect
@Component
public class MatchAspect {
    @Autowired
    private MatchController matchController;

    @AfterReturning(value = "execution(double apply(..)) && args(applicant,vacancy)", returning = "fitRatio")
    public void notificationAdvice(JoinPoint joinPoint, Applicant applicant, Vacancy vacancy, double fitRatio) {
        // Notify about the application if a good fit
        if (fitRatio > 0.75)
            matchController.notify(applicant, vacancy, fitRatio);
    }
}
