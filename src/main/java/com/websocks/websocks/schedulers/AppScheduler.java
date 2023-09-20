package com.websocks.websocks.schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.websocks.websocks.services.FileService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppScheduler {

    @Autowired
    FileService fileService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearCache() {
        fileService.deleteAll();
        log.info("Old files purged successfully!");
    }
}
