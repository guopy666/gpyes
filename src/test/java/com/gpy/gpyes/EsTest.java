package com.gpy.gpyes;

import com.alibaba.fastjson.JSON;
import com.gpy.gpyes.pojo.User;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

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

    //************** 测试索引相关 api 开始***************
    // 测试索引的创建
    @Test
    void testCreateIndex() throws IOException {
        // 1. 创建索引请求
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX);
        // 2. 客户端执行请求，并获得响应
        CreateIndexResponse indexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse);
    }

    // 测试获取索引
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest(INDEX);
        GetIndexResponse response = restHighLevelClient.indices().get(request, RequestOptions.DEFAULT);
        // 判断是否存在
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
        System.out.println(response);
    }

    // 测试删除索引
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(INDEX);
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
        // 删除之后再次执行会报错
        // [springes_index] ElasticsearchStatusException[Elasticsearch exception [type=index_not_found_exception, reason=no such index [springes_index]]]
    }

    //************** 测试索引相关 api 结束***************

    //************** 测试文档相关 api 开始***************

    // 测试文档的添加
    @Test
    void testAddDoc() throws IOException {
        User user = new User();
        user.setAge(13).setName("不正经绅士");
        IndexRequest request = new IndexRequest(INDEX);
        request.id("1")
                .timeout(TimeValue.timeValueSeconds(1));

        // 将数据放入请求  json格式
        request.source(JSON.toJSONString(user), XContentType.JSON);
        // 发送请求
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
        System.out.println(response);
    }

    // 测试文档是否存在
    @Test
    void testIsExist() throws IOException {
        GetRequest request = new GetRequest(INDEX, "1");
        // 不获取返回的 _source 的上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none");

        boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    // 测试获文档内容
    @Test
    void testGetDoc() throws IOException {
        GetRequest request = new GetRequest(INDEX, "1");
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);

        String sourceAsString = response.getSourceAsString();// 获取文档内容，以字符串形式返回

        System.out.println(sourceAsString);

    }

    // 测试文档修改
    @Test
    void testUpdateDoc() throws IOException {
        UpdateRequest request = new UpdateRequest(INDEX, "1");

        User user = new User("不正经绅士版本2", 20);
        request.timeout(TimeValue.timeValueSeconds(1))
                .doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        RestStatus status = response.status();
        System.out.println(status);
    }

    // 测试文档删除
    @Test
    void testDeleteDoc() throws IOException {
        DeleteRequest request = new DeleteRequest(INDEX, "1");
        request.timeout(TimeValue.timeValueSeconds(1));
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    // 测试批量添加文档
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest request = new BulkRequest();
        request.timeout(TimeValue.timeValueSeconds(1));

        List<Object> list = Lists.newArrayList();
        list.add(new User("buzhengjignshenshi1", 3));
        list.add(new User("buzhengjignshenshi2", 3));
        list.add(new User("buzhengjignshenshi3", 3));
        list.add(new User("buzhengjignshenshi4", 3));
        list.add(new User("buzhengjignshenshi5", 3));
        list.add(new User("buzhengjignshenshi6", 3));
        list.add(new User("buzhengjignshenshi7", 3));

        // 代码为批量添加，批量修改，删除同样的方式
        for (int i = 0; i < list.size(); i++) {
            request.add(
                    new IndexRequest(INDEX)
                            .id("" + (i + 1))// 如果不设置id，会有默认随机id
                            .source(JSON.toJSONString(list.get(i)), XContentType.JSON)
            );
        }
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response);
        System.out.println(response.hasFailures());// 是否失败，false表示添加成功
    }

    // 测试查询文档
    @Test
    void testSearch() throws IOException {
        SearchRequest request = new SearchRequest(INDEX);
        // 构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 查询条件
        // 精确匹配
        // TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "buzhengjignshenshi1");
        // 匹配所有
        // QueryBuilders.matchAllQuery();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "buzhengjignshenshi1");
        searchSourceBuilder.query(termQueryBuilder)
                .timeout(TimeValue.timeValueSeconds(10));
        request.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println(response);
        System.out.println(JSON.toJSONString(response.getHits()));
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    //************** 测试文档相关 api 结束***************

}
