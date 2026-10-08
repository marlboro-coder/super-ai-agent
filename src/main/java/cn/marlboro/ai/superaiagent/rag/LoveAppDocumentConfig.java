package cn.marlboro.ai.superaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LoveAppDocumentConfig {

    @Resource
    private LoveAppDocumentReader loveAppDocumentReader;

    @Resource
    private MyTokenSplitter myTokenSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    // 配置向量存储，用于存储和检索文档向量
    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel embeddingModel) {
        List<Document> documentList = loveAppDocumentReader.readDocument();
//        // 按token切分
//        List<Document> splitDocumentList = myTokenSplitter.splitCustomized(documentList);

        // 自动填充元数据信息
        List<Document> enrichDocument = myKeywordEnricher.enrichDocument(documentList);

        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        vectorStore.add(enrichDocument); // add方法会将文档转为向量存储在本地内存中，不推荐在生产环境中使用，因为会占用大量内存和CPU资源
        return vectorStore;
    }
}
