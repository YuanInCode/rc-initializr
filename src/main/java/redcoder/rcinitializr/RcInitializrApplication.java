package redcoder.rcinitializr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import redcoder.rcinitializr.config.CustomInitializrConfiguration;
import redcoder.rcinitializr.customizer.RcProjectDescriptionCustomizerConfiguration;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import({CustomInitializrConfiguration.class, RcProjectDescriptionCustomizerConfiguration.class})
public class RcInitializrApplication {

	public static void main(String[] args) {
		SpringApplication.run(RcInitializrApplication.class, args);
	}

}
