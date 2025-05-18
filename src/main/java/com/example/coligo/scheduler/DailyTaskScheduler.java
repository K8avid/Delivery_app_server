package com.example.coligo.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DailyTaskScheduler {

    @Scheduled(cron = "0 0 2 * * ?") // S'exécute tous les jours à 02:00
    public void executeDailyTask() {
        System.out.println("Tâche quotidienne exécutée à : " + LocalDateTime.now());
    }


    // @Scheduled(cron = "0 0 * * * *") // Exécution toutes les heures
    // public void notifySendersPeriodically() {
    //     notifySendersForNoTripParcels();
    // }


    // @Scheduled(fixedRate = 86400000) // En millisecondes : 24 heures = 86400000 ms
    // public void executeTask() {
    //     System.out.println("Tâche exécutée à : " + LocalDateTime.now());
    // }


    // @Scheduled(cron = "0 0 2 * * ?", zone = "Europe/Paris")
    // public void executeDailyTask() {
    //     System.out.println("Tâche quotidienne exécutée à 02:00 heure locale (Europe/Paris).");
    // }

}
