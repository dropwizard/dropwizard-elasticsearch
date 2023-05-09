package io.dropwizard.elasticsearch.provider;

import io.dropwizard.elasticsearch.managed.ManagedEsClient;
import org.elasticsearch.client.Client;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

public class EsProvider implements Factory<Client> {

    private static class Binder extends AbstractBinder {
        private final EsProvider EsProvider;

        public Binder(ManagedEsClient esClient) {
            EsProvider = new EsProvider(esClient);
        }

        @Override
        protected void configure() {
            bindFactory(EsProvider).to(Client.class).in(RequestScoped.class);
        }
    }

    public static Binder binder(ManagedEsClient esClient) {
        return new Binder(esClient);
    }

    private final ManagedEsClient esClient;

    private EsProvider(ManagedEsClient esClient) {
        this.esClient = esClient;
    }

    @Override
    public Client provide() {
        return esClient.getClient();
    }

    @Override
    public void dispose(Client client) {
    }

}
