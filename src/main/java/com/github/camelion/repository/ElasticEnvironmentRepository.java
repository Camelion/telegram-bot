package com.github.camelion.repository;

import com.github.camelion.model.ChatMessage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Dmitry Poluyanov
 * @since 23.05.17
 */
public interface ElasticEnvironmentRepository extends ElasticsearchRepository<ChatMessage, String> {
}
