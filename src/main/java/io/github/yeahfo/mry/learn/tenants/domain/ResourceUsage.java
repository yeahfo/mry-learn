package io.github.yeahfo.mry.learn.tenants.domain;

import lombok.Builder;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import static java.time.Instant.now;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ofPattern;

@Builder
public record ResourceUsage(
        int appCount,//已创建应用总数
        int memberCount,//已创建成员总数
        int departmentCount,//已创建的部门数
        float storage,//已使用的存储占用量(GB)
        int plateCount,//已创建码牌总数
        SmsSentCount smsSentCount,//本月短信发送量

        Map< String, Integer > qrCountPerApp,//每个应用对应的QR数量，appId -> qr count
        Map< String, Integer > groupCountPerApp,//每个应用对应的group数量，appId -> group count
        Map< String, Integer > submissionCountPerApp//每个应用的提交数量，appId -> submission count
) {

    private static class SmsSentCount {
        private static final DateTimeFormatter MONTH_FORMATTER = ofPattern( "yyyy-MM" ).withZone( systemDefault( ) );
        private String month;
        private int count;

        private int getSmsSentCountForCurrentMonth( ) {
            return isAtCurrentMonth( ) ? count : 0;
        }

        private void increaseSentCountForCurrentMonth( ) {
            if ( isAtCurrentMonth( ) ) {
                count++;
            } else {
                this.month = currentMonth( );
                this.count = 1;
            }
        }

        private boolean isAtCurrentMonth( ) {
            return Objects.equals( month, currentMonth( ) );
        }

        private String currentMonth( ) {
            return MONTH_FORMATTER.format( now( ) );
        }
    }
}
