package com.github.joschi.dropwizard.elasticsearch.config;

import com.yammer.dropwizard.config.ConfigurationException;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.validation.Validator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Unit tests for {@link EsConfiguration}.
 */
public class EsConfigurationTest {
    @Test
    public void defaultConfigShouldBeValid() throws IOException, ConfigurationException {
        ConfigurationFactory<EsConfiguration> factory = ConfigurationFactory.forClass(EsConfiguration.class, new Validator());
        factory.build();
    }

    @Test(expected = ConfigurationException.class)
    public void eitherNodeClientOrServerListMustBeSet() throws IOException, ConfigurationException, URISyntaxException {
        URL configFileUrl = this.getClass().getResource("/invalid.yml");
        File configFile = new File(configFileUrl.toURI());
        ConfigurationFactory<EsConfiguration> factory = ConfigurationFactory.forClass(EsConfiguration.class, new Validator());
        factory.build(configFile);
    }
}
