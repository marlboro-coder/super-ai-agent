package cn.marlboro.ai.superaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

@Slf4j
public class LoveAppRagCustomAdvisorFactory {

    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore,String status){

        // 定义一个过滤表达式
        // 可以在这里对status进行判断，如果不为null或者不为空才进行过滤表达式构建，如果需要动态处理的话
        Filter.Expression filterExpression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();
        // 定义一个文档检索器，使用向量存储和过滤表达式，以及检索配置
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(3)
                .similarityThreshold(0.7)
                .filterExpression(filterExpression)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(false)
                        .emptyContextPromptTemplate(
                                new PromptTemplate("用户的提问超出了你的认知范围，请礼貌告诉用户你不知道这个问题的答案。")
                        )
                        .build()
                )
                .build();
    }
}
