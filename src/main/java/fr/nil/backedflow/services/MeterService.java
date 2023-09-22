package fr.nil.backedflow.services;


import fr.nil.backedflow.stats.MetricsEnum;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeterService {


    private final MeterRegistry meterRegistry;


    private final AtomicLong lastUploadedFileSize = new AtomicLong(0);
    private Gauge fileSizeGauge;
    private Counter springLoginCounter;
    private Counter googleSSOLoginCounter;
    private Counter userRegistrationCounter;
    private Counter fileUploadCounter;
    private Counter fileDownloadCounter;

    @PostConstruct
    public void init() {
        springLoginCounter = Counter.builder(MetricsEnum.USER_LOGIN_COUNT.getMetricName())
                .description("Number of login attempts using Spring Security")
                .register(meterRegistry);

        googleSSOLoginCounter = Counter.builder(MetricsEnum.USER_SSO_LOGIN_COUNT.getMetricName())
                .description("Number of login attempts using Google SSO")
                .register(meterRegistry);

        userRegistrationCounter = Counter.builder(MetricsEnum.USER_CREATION_COUNT.getMetricName())
                .description("Number of account creations")
                .register(meterRegistry);

        fileUploadCounter = Counter.builder(MetricsEnum.FILE_TRANSFER_UPLOAD_COUNT.getMetricName())
                .description("Number of file uploads")
                .register(meterRegistry);

        fileDownloadCounter = Counter.builder(MetricsEnum.FILE_TRANSFER_DOWNLOAD_COUNT.getMetricName())
                .description("Number of file downloads")
                .register(meterRegistry);

        fileSizeGauge = Gauge.builder(MetricsEnum.FILE_TRANSFER_UPLOAD_SIZE.getMetricName(), lastUploadedFileSize, AtomicLong::get)
                .description("Size of the last uploaded file")
                .register(meterRegistry);

    }

    public void updateFileSizeGauge(long size) {
        lastUploadedFileSize.set(size);
    }
}
