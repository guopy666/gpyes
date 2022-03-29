package com.gpy.gpyes.jddemo.utils;

import com.gpy.gpyes.jddemo.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName HtmlParseUtils
 * @Description 页面解析工具
 * @Author guopy
 * @Date 2022/3/28 16:03
 */
public class HtmlParseUtils {
    public static void main(String[] args) throws IOException {
        String url = "https://search.jd.com/Search?keyword=java";
        // 解析网页得到浏览器的 document 对象
        Document document = Jsoup.parse(new URL(url), 30000);
        // System.out.println(document);
        // 根据网页获取对应的标签元素和值
        Elements li = document.getElementsByTag("li");
        for (Element element : li) {
            String price = element.getElementsByClass("p-price").eq(0).text();
            String name = element.getElementsByClass("p-name").eq(0).text();
            String img = element.getElementsByTag("img").eq(0).attr("src");
            String realImg = element.getElementsByTag("img").eq(0).attr("data-lazy-img");
            System.out.println("================");
            System.out.println(img);
            System.out.println(price);
            System.out.println(name);
            System.out.println(realImg);
        }
        parseJdGoods("香水").forEach(System.out::println);

    }


    public static List<Content> parseJdGoods(String keywords) throws IOException {
        String url = "https://search.jd.com/Search?keyword=";
        // 解析网页得到浏览器的 document 对象
        Document document = Jsoup.parse(new URL(url + keywords), 30000);
        // System.out.println(document);
        // 根据网页获取对应的标签元素和值
        Element jGoodsList = document.getElementById("J_goodsList");
        Elements elements = jGoodsList.getElementsByTag("li");

        Content content;
        List<Content> list = new ArrayList<>();
        for (Element element : elements) {
            String price = element.getElementsByClass("p-price").eq(0).text();
            String name = element.getElementsByClass("p-name").eq(0).text();
            // 因为网站懒加载的原因，img.src 并不能获取图片地址，需要通过data-lazy-img（这个标签会随着目标网站的更新发生变化）
            String img = element.getElementsByTag("img").eq(0).attr("src");
            String realImg = element.getElementsByTag("img").eq(0).attr("data-lazy-img");
            content = new Content(name, realImg, price);
            list.add(content);
        }
        return list;
    }
}
