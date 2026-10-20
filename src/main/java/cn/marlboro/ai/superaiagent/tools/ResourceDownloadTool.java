package cn.marlboro.ai.superaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.marlboro.ai.superaiagent.constants.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

public class ResourceDownloadTool {

    private static  final String FILE_DIR = FileConstant.FILE_PATH + "downloads/";


    @Tool(description = "Download a resource from a URL")
    public String resourceDownload(@ToolParam(description = "The URL of the resource to download") String url,
                                   @ToolParam(description = "The name of the file to save") String fileName) {
        String filePath = FILE_DIR + fileName;
        try {
            FileUtil.mkdir(FILE_DIR);
            HttpUtil.downloadFile(url, new File(filePath));
            return "Resource downloaded successfully: " + filePath;
        } catch (Exception e) {
            return "Error downloading resource: " + e.getMessage();
        }
    }
}
