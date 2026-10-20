package cn.marlboro.ai.superaiagent.agent;

import cn.hutool.core.util.StrUtil;
import cn.marlboro.ai.superaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public abstract class BaseAgent {
    // 状态
    private AgentState state = AgentState.IDLE;

    // 核心属性
    private String name;

    // 提示
    private String systemPrompt;
    private String nextStepPrompt;

    // 执行控制
    private int maxSteps = 10;
    private int currentStep = 0;

    // 执行单个步骤的抽象方法
    public abstract String step();

    // llm
    private ChatClient chatClient;

    // 上下文信息
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     * @param userPrompt 用户提示词
     * @return  运行结果
     */
    public String run(String userPrompt){
        if(this.state != AgentState.IDLE){
            throw new RuntimeException("cannot run agent from state:" + this.state);
        }
        if(StrUtil.isBlank(userPrompt)){
            throw new RuntimeException("cannot run agent with empty prompt");
        }
        // 更改状态
        this.state = AgentState.RUNNING;
        messageList.add(new UserMessage(userPrompt));
        List<String> results = new ArrayList<>();
        try {
            for(int i = 0; i <= maxSteps && state != AgentState.FINISHED; i++){
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("executing step" + stepNumber + "/" + maxSteps);
                String stepResult = step();
                String result = "step" + stepNumber + ":" + stepResult;
                results.add(result);
            }
            if (currentStep >= maxSteps){
                state = AgentState.FINISHED;
                results.add("Terminated:reached max steps (" + maxSteps + ")" );
            }
            return String.join("\n",results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("error executing agent:",e);
            return "执行错误：" + e.getMessage();
        } finally {
            this.cleanup();
        }
    }
    protected void cleanup(){
        /*
        子类重写清理资源方法
         */
    }


}
