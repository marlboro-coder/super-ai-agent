package cn.marlboro.ai.superaiagent.tools;

import java.time.LocalDateTime;

public class GetDateTimeTool {

    public String getCurrentDateTime(){
        String currentDateTime = LocalDateTime.now().toString();
        return currentDateTime;
    }
}
