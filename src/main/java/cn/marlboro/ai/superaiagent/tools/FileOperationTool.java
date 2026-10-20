package cn.marlboro.ai.superaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.marlboro.ai.superaiagent.constants.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileOperationTool {

    private static final String FILE_DIR = FileConstant.FILE_PATH + "files/";

    @Tool(description = "Read the content of a file")
    public String readFile(@ToolParam(description = "The path of the file to read") String fileName) {
        String filePath = FILE_DIR + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "The path of the file to write") String fileName,
                            @ToolParam(description = "The content to write to the file") String content) {
        String filePath = FILE_DIR + fileName;
        try {
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully: " + filePath;
        } catch (IORuntimeException e) {
            return "Error writing file: " + e.getMessage();
        }
    }
}
