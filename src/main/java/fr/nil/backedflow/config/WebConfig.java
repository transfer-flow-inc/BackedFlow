package fr.nil.backedflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    /**
     * Configure cross-origin resource sharing (CORS) for the API.
     * @param registry the CORS configuration registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("https://azure.transfer-flow.studio")
                .allowedOrigins("https://azure-api.transfer-flow.studio")
                .allowedOrigins("https://api.transfer-flow.studio")
                .exposedHeaders("Access-Control-Allow-Origin")
                .allowCredentials(true)
                .maxAge(((long) 3600 * 5))
                .allowedMethods("GET", "POST", "PATCH", "OPTIONS", "PUT", "DELETE", "HEAD");

    }

}