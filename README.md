Dropwizard Elasticsearch
========================

[![Build Status](https://travis-ci.org/dropwizard/dropwizard-elasticsearch.svg?branch=master)](https://travis-ci.org/dropwizard/dropwizard-elasticsearch)
[![Coverage Status](https://img.shields.io/coveralls/dropwizard/dropwizard-elasticsearch.svg)](https://coveralls.io/r/dropwizard/dropwizard-elasticsearch)
[![Maven Central](https://img.shields.io/maven-central/v/io.dropwizard.modules/dropwizard-elasticsearch.svg)](http://mvnrepository.com/artifact/io.dropwizard.modules/dropwizard-elasticsearch)

A set of classes for using [Elasticsearch][1] (version 7.1.0 and higher) in a [Dropwizard][2] application.

The package provides a [lifecycle-managed][3] client class (`ManagedEsClient`), a configuration class with the most
common options (`EsConfiguration`), and some [health checks][4] which can instantly be used in any Dropwizard application.

[1]: http://www.elasticsearch.org/
[2]: http://dropwizard.io/1.3.0/docs
[3]: http://dropwizard.io/1.3.0/docs/manual/core.html#managed-objects
[4]: http://dropwizard.io/1.3.0/docs/manual/core.html#health-checks


Usage
-----

Just add `EsConfiguration` to your [Configuration](http://dropwizard.io/1.2.0/docs/manual/core.html#configuration) class and
create an `ManagedEsClient` instance in the run method of your service.

You can also add one of the existing health checks to your [Environment](http://dropwizard.io/1.2.0/docs/manual/core.html#environments)
in the same method. At least the usage of `EsClusterHealthCheck` is strongly advised.


    public class DemoApplication extends Application<DemoConfiguration> {
        // [...]
        @Override
        public void run(DemoConfiguration config, Environment environment) {
            final ManagedEsClient managedClient = new ManagedEsClient(configuration.getEsConfiguration());
            environment.lifecycle().manage(managedClient);
            environment.healthChecks().register("ES cluster health", new EsClusterHealthCheck(managedClient.getClient()));
            // [...]
        }
    }


Configuration
-------------

The following configuration settings are supported by `EsConfiguration`:

* `servers`: A list of servers for usage with the created TransportClient if `nodeClient` is `false`

An example configuration file for creating a High Level Rest Client could like this:

    servers:
      - http://127.0.0.1:9200
      - http://127.0.0.1:9201
      - http://127.0.0.1:9202


Support
-------

Please file bug reports and feature requests in [GitHub issues](https://github.com/dropwizard/dropwizard-elasticsearch/issues).


Acknowledgements
----------------

Thanks to Alexander Reelsen (@spinscale) for his [Dropwizard Blog Sample](https://github.com/spinscale/dropwizard-blog-sample)
which sparked the idea for this project.


License
-------

Copyright (c) 2013-2017 Jochen Schalanda

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
