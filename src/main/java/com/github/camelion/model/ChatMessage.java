package com.github.camelion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * @author Dmitry Poluyanov
 * @since 23.05.17
 */
@Setting(settingPath = "/elastic/settings.json")
@Document(indexName = "messages", type = "message")
public class ChatMessage {
    @Id
    private String id;

    @Field(type = FieldType.String, analyzer = "russian", searchAnalyzer = "russian")
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
