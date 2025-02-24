package redcoder.rcinitializr.configuration;

import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InitializrProperties;
import io.spring.initializr.web.support.DefaultInitializrMetadataProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 */
@Configuration
@EnableConfigurationProperties(RcInitializrProperties.class)
public class RcInitializrConfiguration {

    @Bean
    public InitializrMetadataProvider customInitializrMetadataProvider(InitializrProperties initializrProperties,
                                                                       RcInitializrProperties rcInitializrProperties) {
        InitializrMetadata metadata = InitializrMetadataBuilder
                .fromInitializrProperties(rcInitializrProperties.initializr)
                .withInitializrProperties(initializrProperties, true)
                .build();
        return new DefaultInitializrMetadataProvider(metadata, current -> current);
    }

}

