package cn.marlboro.ai.agentimagesearchmcpsever;

import cn.marlboro.ai.agentimagesearchmcpsever.tools.ImageSearch;
import org.springframework.ai.tool.ToolCallbackProvider;

import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AgentImageSearchMcpSeverApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentImageSearchMcpSeverApplication.class, args);
    }


    @Bean
    public ToolCallbackProvider imageSearchTool(ImageSearch imageSearch){
        return MethodToolCallbackProvider.builder()
                .toolObjects(imageSearch)
                .build();
    }

}
