package cn.marlboro.ai.superaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.marlboro.ai.superaiagent.agent.model.AgentState;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReactAgent {

    private ChatResponse toolCallChatResponse;

    private ToolCallback[] availableTools;

    private final ToolCallingManager toolCallingManager;
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools){
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(availableTools)
                .internalToolExecutionEnabled(false)
                .build();
    }

    @Override
    public boolean think() {
        if(getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()){
            UserMessage nextStepMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(nextStepMessage);
        }
        // 获取到最新的上下文
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);

        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();
            // 记录响应，用于act
            this.toolCallChatResponse = chatResponse;

            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 输出模型的调用工具的文本回复，还没开始调工具
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();

            log.info(getName() + "的思考：" + result);
            log.info(getName() + "选择了" + toolCalls.size() + "个工具");

            String toolCallInfo = toolCalls.stream()
                    .map(toolCall -> StrUtil.format("工具名称：{}，参数：{}", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);

            if(toolCalls.isEmpty()){
                // 不需要调用工具才将助手message添加到上下文
                getMessageList().add(assistantMessage);
                return false;
            } else{
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题：" + e.getMessage());
            // 告诉ai遇到了错误
            getMessageList().add(new AssistantMessage("处理时遇到了错误：" + e.getMessage()));
            return false;
        }
    }

    @Override
    public String act() {

        if(!toolCallChatResponse.hasToolCalls()){
            return "没有工具调用";
        }
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 记录消息上下文,conversionHistory已经包含了助手消息和工具调用返回的结果
        setMessageList(toolExecutionResult.conversationHistory());
        // 当前工具调用的结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        String results = toolResponseMessage.getResponses().stream()
                .map(toolResponse -> "工具：" + toolResponse.name() + "完成了它的任务！结果：" + toolResponse.responseData())
                .collect(Collectors.joining("\n"));
        // 判断是否调用了终止工具
        boolean isTerminated = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));
        if(isTerminated){
            setState(AgentState.FINISHED);
        }
        log.info(results);
        return results;
    }
}
