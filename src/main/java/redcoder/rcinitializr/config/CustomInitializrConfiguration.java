package redcoder.rcinitializr.config;

import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InitializrProperties;
import io.spring.initializr.web.support.DefaultInitializrMetadataProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 合并自定义的InitializrProperties
 *
 * @author redcoder54
 * @since 2021-08-13
 */
@Configuration
@EnableConfigurationProperties(CustomInitializrProperties.class)
public class CustomInitializrConfiguration {

    @Bean
    public InitializrMetadataProvider customInitializrMetadataProvider(InitializrProperties initializrProperties,
                                                                       CustomInitializrProperties customInitializrProperties) {
        InitializrMetadata metadata = InitializrMetadataBuilder
                .fromInitializrProperties(customInitializrProperties.initializr)
                .withInitializrProperties(initializrProperties, true)
                .build();
        return new DefaultInitializrMetadataProvider(metadata, current -> current);
    }

}

@ConfigurationProperties("custom")
class CustomInitializrProperties {

    @NestedConfigurationProperty
    InitializrProperties initializr;

    public InitializrProperties getInitializr() {
        return initializr;
    }

    public void setInitializr(InitializrProperties initializr) {
        this.initializr = initializr;
    }
}
