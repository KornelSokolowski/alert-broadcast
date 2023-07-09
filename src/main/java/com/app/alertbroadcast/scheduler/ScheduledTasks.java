package com.app.alertbroadcast.scheduler;

import com.app.alertbroadcast.service.ExportProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    @Value("${export.enable}")
    private boolean exportEnabled;
    private final ExportProcessingService exportProcessingService;

    @Scheduled(cron = "${export.cron}")
    public void runExport(){
        if(exportEnabled){
            log.info("Export started");
            exportProcessingService.startExport();
            log.info("Export finished");
        }
    }
}
