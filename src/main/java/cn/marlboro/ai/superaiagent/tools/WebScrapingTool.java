package cn.marlboro.ai.superaiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

public class WebScrapingTool {

    @Tool
    public String webScrapy(@ToolParam() String url){
        try {
            Document elements = Jsoup.connect(url).get();
            return elements.html();
        } catch (IOException e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}
