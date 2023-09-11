package fr.nil.backedflow.config;


import fr.nil.backedflow.jobs.DailyTaskJob;
import fr.nil.backedflow.repositories.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 This is the application configuration class.
 It configures and provides necessary beans to the application.
 */
@Configuration
@EnableScheduling
@EnableAsync
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * Provides an implementation of the {@link UserDetailsService} interface to be used by Spring Security.
     *
     * @return An instance of {@link UserDetailsService} that retrieves a user from the {@link UserRepository} by email.
     */
    @Bean
    public UserDetailsService userDetailsService()
    {
        return username -> userRepository.findByMail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find user"));
    }
    /**
     * Provides an implementation of the {@link AuthenticationProvider} interface to be used by Spring Security.
     *
     * @return An instance of {@link DaoAuthenticationProvider} that uses the {@link #userDetailsService()} method and a {@link PasswordEncoder}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(getPasswordEncoder());
        return daoAuthenticationProvider;
    }

    /**
     * Provides an implementation of the {@link AuthenticationManager} interface to be used by Spring Security.
     *
     * @param authenticationConfiguration The {@link AuthenticationConfiguration} instance used to configure the authentication manager.
     * @return An instance of {@link AuthenticationManager}.
     * @throws Exception If an error occurs while creating the authentication manager.
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Provides an implementation of the {@link PasswordEncoder} interface to be used by Spring Security.
     *
     * @return An instance of {@link BCryptPasswordEncoder}.
     */

    @Bean
    public PasswordEncoder getPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public MeterRegistryCustomizer<MeterRegistry> commonTags() {
        return r -> r.config().commonTags("application", "backedflow");
    }

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(DailyTaskJob.class)
                .withIdentity("ExpiredFoldersJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail job) {
        return TriggerBuilder.newTrigger()
                .forJob(job)
                .withIdentity("ExpiredFoldersTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
                .build();
    }
}
