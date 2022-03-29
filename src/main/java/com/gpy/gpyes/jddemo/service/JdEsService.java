package com.gpy.gpyes.jddemo.service;

import com.alibaba.fastjson.JSON;
import com.gpy.gpyes.jddemo.pojo.Content;
import com.gpy.gpyes.jddemo.utils.HtmlParseUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.AbstractHighlighterBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.Highlighter;
import java.awt.image.TileObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName JdEsService
 * @Description
 * @Author guopy
 * @Date 2022/3/28 17:58
 */
@Service
public class JdEsService {

    public static final String INDEX = "jd_goods";

    @Autowired
    private RestHighLevelClient client;

    // 解析数据放入到 es 中
    public Boolean parseContent(String keyword) throws IOException {
        // 从京东爬取数据
        List<Content> contents = HtmlParseUtils.parseJdGoods(keyword);

        // 把数据放入到 es 中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueSeconds(20));
        for (Content content : contents) {
            bulkRequest.add(
                    new IndexRequest(INDEX)
                    .source(JSON.toJSONString(content), XContentType.JSON)
            );
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }


    public List<Map<String, Object>> searchPage(String keywords, Integer pageNo, Integer pageSize) throws IOException {
        if (pageNo <= 1){
            pageNo = 1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest(INDEX);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 分页
        sourceBuilder.from((pageNo-1)*pageSize)
                .size(pageSize);

        // 构建匹配
        MatchQueryBuilder termQuery = QueryBuilders.matchQuery("title", keywords);
        sourceBuilder.query(termQuery)
                .timeout(TimeValue.timeValueSeconds(60));
        searchRequest.source(sourceBuilder);

        // 构建高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title")
                .preTags("<span style = 'color:red'>")
                .postTags("</span>")
                .requireFieldMatch(false); // 多个高亮显示
        sourceBuilder.highlighter(highlightBuilder);

        // 执行搜索
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        // 处理结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {

            // 解析高亮的字段，匹配后替换为高亮显示
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();// 原来的结果
            // 解析高粱字段
            if (title != null){
                Text[] fragments = title.fragments();
                String newTitle = "";
                for (Text text : fragments) {
                    newTitle += text;
                }
                sourceAsMap.put("title", newTitle);// 把高亮后的字段放回原来的map中
            }

            result.add(hit.getSourceAsMap());
        }
        return result;
    }



}
