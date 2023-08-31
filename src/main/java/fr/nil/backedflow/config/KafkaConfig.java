package fr.nil.backedflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.util.Map;

@Configuration
@Profile("apitesting")
public class KafkaConfig {

    @Bean
    public EmbeddedKafkaBroker kafkaEmbedded() {
        return new EmbeddedKafkaBroker(1, true, "accountCreationTopic", "transferNotificationTopic")
                .brokerProperties(Map.of(
                        "listeners", "PLAINTEXT://localhost:9092",
                        "port", "9092"
                ));
    }
}