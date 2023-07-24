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
                .allowedOrigins("http://localhost:4200")
                .allowedOrigins("http://192.168.1.18:4200")
                .allowedOrigins("http://api.fuel-finder.studio")
                .allowedOrigins("http://fuel-finder.studio")
                .allowedOrigins("https://fuel-finder.studio")
                .exposedHeaders("Access-Control-Allow-Origin")
                .allowCredentials(true)
                .maxAge(3600*5)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD");

    }

}