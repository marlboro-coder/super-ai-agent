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

    @Bean
    VectorStore loveAppDocuments(EmbeddingModel embeddingModel) {
        List<Document> documentList = loveAppDocumentReader.readDocument();
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        vectorStore.add(documentList);
        return vectorStore;
    }
}
