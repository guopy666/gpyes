package com.gpy.gpyes;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @ClassName EsTest
 * @Description
 * @Author guopy
 * @Date 2022/3/25 11:42
 */
@SpringBootTest
public class EsTest {

    private static final String INDEX = "springes_index";

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    // 测试索引的创建
    @Test
    void testCreateIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX);
        CreateIndexResponse indexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse);
    }

}
