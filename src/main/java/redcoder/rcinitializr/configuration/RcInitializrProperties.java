package redcoder.rcinitializr.configuration;

import io.spring.initializr.metadata.InitializrProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("rc")
public class RcInitializrProperties {

    @NestedConfigurationProperty
    InitializrProperties initializr;

    public InitializrProperties getInitializr() {
        return initializr;
    }

    public void setInitializr(InitializrProperties initializr) {
        this.initializr = initializr;
    }
}
