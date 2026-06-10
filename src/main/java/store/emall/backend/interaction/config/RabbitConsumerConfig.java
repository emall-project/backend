package store.emall.backend.interaction.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.emall.backend.common.exchange.ExchangeTypes;
import store.emall.backend.interaction.interaction.EventQueue;
import store.emall.backend.interaction.jobs.JobRoutingKeys;
import store.emall.backend.interaction.jobs.JobQueue;

@Configuration
public class RabbitConsumerConfig {

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(ExchangeTypes.EVENT_EXCHANGE_NAME, true, false);
    }

    @Bean
    public TopicExchange jobExchange() {
        return new TopicExchange(ExchangeTypes.JOB_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Declarables interactionQueuesAndBindings(TopicExchange eventsExchange, TopicExchange jobExchange) {
        // ======== Event Queues ============
        Queue catalogEventQueue = new Queue(EventQueue.CATALOG, true);
        Queue orderHubEventQueue = new Queue(EventQueue.ORDER_HUB, true);
        Queue campaignsEventQueue = new Queue(EventQueue.CAMPAIGNS, true);

        Binding catalogBinding = BindingBuilder
                .bind(catalogEventQueue)
                .to(eventsExchange)
                .with("catalog.#");

        Binding orderBinding = BindingBuilder
                .bind(orderHubEventQueue)
                .to(eventsExchange)
                .with("order.#");

        Binding campaignsBinding = BindingBuilder
                .bind(campaignsEventQueue)
                .to(eventsExchange)
                .with("campaign.#");


        // ======== Job Queues ============
        Queue catalogProductCreatedQueue = new Queue(JobQueue.CATALOG_PRODUCT_CREATED, true);
        Queue catalogProductUpdatedQueue = new Queue(JobQueue.CATALOG_PRODUCT_UPDATED, true);
        Queue catalogProductDeletedQueue = new Queue(JobQueue.CATALOG_PRODUCT_DELETED, true);

        Binding catalogProductCreatedBinding = BindingBuilder
                .bind(catalogProductCreatedQueue)
                .to(jobExchange)
                .with(JobRoutingKeys.CATALOG_PRODUCT_CREATED);

        Binding catalogProductUpdatedBinding = BindingBuilder
                .bind(catalogProductUpdatedQueue)
                .to(jobExchange)
                .with(JobRoutingKeys.CATALOG_PRODUCT_UPDATED);

        Binding catalogProductDeletedBinding = BindingBuilder
                .bind(catalogProductDeletedQueue)
                .to(jobExchange)
                .with(JobRoutingKeys.CATALOG_PRODUCT_DELETED);

        return new Declarables(
                // === event ===
                // queue
                catalogEventQueue,
                orderHubEventQueue,
                campaignsEventQueue,
                // binding
                catalogBinding,
                orderBinding,
                campaignsBinding,

                // === job ===
                // queue
                catalogProductCreatedQueue,
                catalogProductUpdatedQueue,
                catalogProductDeletedQueue,

                // binding
                catalogProductCreatedBinding,
                catalogProductUpdatedBinding,
                catalogProductDeletedBinding
        );
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter jacksonJsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonJsonMessageConverter);

        // add dead-letter exchange strategy here.
        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}