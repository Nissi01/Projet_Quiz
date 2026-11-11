error id: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/config/WebConfig.java:org/springframework/web/servlet/config/annotation/WebMvcConfigurer#
file://<WORKSPACE>/backend/src/main/java/com/studyquiz/config/WebConfig.java
empty definition using pc, found symbol in pc: org/springframework/web/servlet/config/annotation/WebMvcConfigurer#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 278
uri: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/config/WebConfig.java
text:
```scala
package com.studyquiz.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.@@WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final String origin;
    public WebConfig(@Value("${app.cors-origin}") String origin) { this.origin = origin; }
    @Override public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins(origin).allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE").allowedHeaders("Content-Type", "Authorization");
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/springframework/web/servlet/config/annotation/WebMvcConfigurer#