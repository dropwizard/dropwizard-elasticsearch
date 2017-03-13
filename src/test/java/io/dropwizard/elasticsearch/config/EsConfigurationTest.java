package io.dropwizard.elasticsearch.config;

import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Unit tests for {@link EsConfiguration}.
 */
public class EsConfigurationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ConfigurationFactory<EsConfiguration> configFactory =
            new DefaultConfigurationFactoryFactory<EsConfiguration>()
                    .create(EsConfiguration.class, validator, Jackson.newObjectMapper(), "dw");

    @Test(expected = ConfigurationException.class)
    public void defaultConfigInvalid() throws IOException, ConfigurationException {
        configFactory.build();
    }

    @Test(expected = ConfigurationException.class)
    public void eitherNodeClientOrServerListMustBeSet() throws IOException, ConfigurationException, URISyntaxException {
        URL configFileUrl = this.getClass().getResource("/invalid.yml");
        File configFile = new File(configFileUrl.toURI());
        configFactory.build(configFile);
    }
}
