package com.example.searchservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Date;
import java.util.Map;

@Document(indexName = "#{@environment.getProperty('elasticsearch.index.name')}")
@Setting(settingPath = "elasticsearch-settings.json")
@Getter
@Setter
public class SearchableDocument {

    /**
     * Unique identifier for the document.
     */
    @Id
    private String id;

    /**
     * Title of the document with text analysis for better search.
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    /**
     * Content of the document with text analysis for better search.
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    /**
     * Author of the document.
     */
    @Field(type = FieldType.Keyword)
    private String author;

    /**
     * Date when the document was created.
     */
    @Field(type = FieldType.Date)
    private Date createdDate;

    /**
     * Date when the document was last updated.
     */
    @Field(type = FieldType.Date)
    private Date lastUpdatedDate;

    /**
     * Category or type of the document.
     */
    @Field(type = FieldType.Keyword)
    private String category;

    /**
     * Tags associated with the document for faceted search.
     */
    @Field(type = FieldType.Keyword)
    private String[] tags;

    /**
     * Dynamic fields that can store additional document properties.
     */
    @Field(type = FieldType.Object)
    private Map<String, Object> metadata;

}
