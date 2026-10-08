package cn.marlboro.ai.superaiagent.demo.invoke.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MultiQueryExpanderDemo {

    private final ChatClient.Builder chatClientBuilder;

    public MultiQueryExpanderDemo(ChatModel dashscopeChatModel) {
        this.chatClientBuilder = ChatClient.builder(dashscopeChatModel);
    }

    public List<Query> expand(String prompt) {
        MultiQueryExpander multiQueryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(3) // 设置要生成的查询数量
                .build();
        List<Query> queries = multiQueryExpander.expand(new Query(prompt));

        return queries;
    }
}
