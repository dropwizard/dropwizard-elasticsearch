package io.dropwizard.elasticsearch.config;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.validation.Validation;
import javax.validation.Validator;

import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.jackson.Jackson;


/**
 * Unit tests for {@link BasicAuthenticationConfiguration}.
 */
public class CredentialConfigurationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ConfigurationFactory<BasicAuthenticationConfiguration> configFactory =
            new DefaultConfigurationFactoryFactory<BasicAuthenticationConfiguration>()
                    .create(BasicAuthenticationConfiguration.class, validator, Jackson.newObjectMapper(), "dw");

    @Test
    public void defaultConfigShouldBeValid() throws IOException, ConfigurationException {
        configFactory.build();
    }

    @Test(expected = ConfigurationException.class)
    public void userAndPasswordMustBeSet() throws IOException, ConfigurationException, URISyntaxException {
        URL configFileUrl = this.getClass().getResource("/invalid.yml");
        File configFile = new File(configFileUrl.toURI());
        configFactory.build(configFile);
    }
}
