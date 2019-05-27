package io.dropwizard.elasticsearch.config;


import org.junit.Test;

import java.io.IOException;

import javax.validation.Validation;
import javax.validation.Validator;

import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.jackson.Jackson;

/**
 * Unit tests for {@link SnifferConfiguration}.
 */
public class SnifferConfigurationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ConfigurationFactory<SnifferConfiguration> configFactory =
            new DefaultConfigurationFactoryFactory<SnifferConfiguration>()
                    .create(SnifferConfiguration.class, validator, Jackson.newObjectMapper(), "dw");

    @Test
    public void defaultConfigShouldBeValid() throws IOException, ConfigurationException {
        configFactory.build();
    }
}
