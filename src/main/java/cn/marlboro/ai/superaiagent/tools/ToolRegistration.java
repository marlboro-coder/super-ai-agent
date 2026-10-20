package cn.marlboro.ai.superaiagent.tools;

import cn.marlboro.ai.superaiagent.utils.AliOssUtil;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工具注册类，用于注册所有自定义工具
 */
@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String ApiKey;

    @Bean
    public ToolCallback[] allTools(AliOssUtil aliOssUtil, ToolCallbackProvider toolCallbackProvider) {

        FileOperationTool fileOperationTool = new FileOperationTool();
        WebScrapingTool webScrapingTool = new WebScrapingTool();
//        PdfGeneratorTool pdfGeneratorTool = new PdfGeneratorTool();
        WebSearchTool searchApiTool = new WebSearchTool(ApiKey);
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        PDFGenerationTool pdfGeneratorTool = new PDFGenerationTool(aliOssUtil);
        TerminateTools terminateTools = new TerminateTools();

        ToolCallback[] localTools = ToolCallbacks.from(
                fileOperationTool,
                webScrapingTool,
//                pdfGeneratorTool,
                searchApiTool,
                resourceDownloadTool,
                pdfGeneratorTool,
                terminateTools
        );
        ToolCallback[] mcpTools = toolCallbackProvider.getToolCallbacks();

        ToolCallback[] merged = new ToolCallback[localTools.length + mcpTools.length];
        System.arraycopy(localTools, 0, merged, 0, localTools.length);
        System.arraycopy(mcpTools, 0, merged, localTools.length, mcpTools.length);
        return merged;
    }
}
