package cn.marlboro.ai.superaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LoveAppDocumentReader {
    private final ResourcePatternResolver resourcePatternResolver;
    public LoveAppDocumentReader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }
    // 预处理并切分文档，返回文档列表
    public List<Document> readDocument() {
        List<Document> documentList = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();

                // 获取文档status
                String status = fileName.substring(fileName.length() - 6,fileName.length() - 4);
                // 配置Markdown文档读取器
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", fileName)
                        .withAdditionalMetadata("status", status)
                        .build();
                // 读取Markdown文档并初步切分，后续可以使用更复杂的切分逻辑，通过DocumentTransformer实现
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                documentList.addAll(reader.get());
            }
        } catch (IOException e) {
            log.error("readDocument error", e);
        }
        return documentList;
    }
}
