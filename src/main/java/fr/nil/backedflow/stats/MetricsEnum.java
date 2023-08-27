package fr.nil.backedflow.stats;

import lombok.Getter;

@Getter
public enum MetricsEnum {

    USER_CREATION_COUNT("backedflow.user.register.count"),
    USER_LOGIN_COUNT("backedflow.user.login.count"),
    USER_SSO_LOGIN_COUNT("backedflow.user.login.sso.count"),
    USER_VERIFICATION_COUNT("backedflow.user.verification.count"),


    FILE_TRANSFER_UPLOAD_COUNT("backedflow.transfer.file.count"),
    FILE_TRANSFER_UPLOAD_SIZE("backedflow.transfer.upload.file.size"),
    FILE_TRANSFER_DOWNLOAD_COUNT("backedflow.transfer.download.file.count"),
    FILE_TRANSFER_DOWNLOAD_SIZE("backedflow.transfer.download.file.sum"),

    ERROR_HANDLER_COUNT("backedflow.error.handler.count");

    private String metricName;


    MetricsEnum(String s) {
        this.metricName = s;
    }


}
