package cn.marlboro.ai.superaiagent.app;

import cn.marlboro.ai.superaiagent.advisor.MyLoggerAdvisor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private final String defaultSystem =  "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
                "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
                "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
                "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppCloudAdvisor;

    @Resource
    private VectorStore pgVectorVectorStore;

    public LoveApp(ChatModel dashscopeChatModel) {
        // 实现多轮对话和对话记忆
        InMemoryChatMemoryRepository memoryRepository = new InMemoryChatMemoryRepository();

        // 2. 通过 Builder 构建 MessageWindowChatMemory，并关联存储层
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(memoryRepository)
                .maxMessages(20) // 可选：设置最大消息窗口，默认也是20
                .build();

        // 3. 通过 Builder 构建 MessageChatMemoryAdvisor
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(defaultSystem)
                .defaultAdvisors(memoryAdvisor)
                .build();
    }

    // 处理用户输入，返回模型回复
    public String doChat(String message,String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String response = chatResponse.getResult().getOutput().getText();
        log.info("模型回复内容：{}",response);
        return response;
    }

    // 基于知识库对话
    public String chatWithRag(String message,String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor())
                // 本地知识库
                .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build()) // 1.0.0以后要用builder创建
//                // 云服务知识库
//                .advisors(loveAppCloudAdvisor)
//                // 基于PgVector
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                .call()
                .chatResponse();

        String response = chatResponse.getResult().getOutput().getText();
        log.info("用户提示词：{}",message);
//        log.info("模型回复内容：{}",response);
        return response;
    }

//    // 基于云服务知识库对话
//
//    public String chatWithCloudRag(String message,String chatId){
//        ChatResponse chatResponse = chatClient.prompt()
//                .user(message)
//                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
//                .advisors(new MyLoggerAdvisor())
//                .advisors(loveAppCloudAdvisor)
//                .call()
//                .chatResponse();
//        String response = chatResponse.getResult().getOutput().getText();
//        log.info("模型回复内容：{}",response);
//        return response;
//    }


}
