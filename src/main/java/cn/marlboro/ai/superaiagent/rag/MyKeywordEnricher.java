package cn.marlboro.ai.superaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyKeywordEnricher {
    @Resource
    private ChatModel dashscopeChatModel;

    public List<Document> enrichDocument(List<Document> documents){
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(this.dashscopeChatModel, 5);
        return keywordMetadataEnricher.apply(documents);
    }
}
