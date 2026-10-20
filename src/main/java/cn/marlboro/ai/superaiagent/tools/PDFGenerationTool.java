package cn.marlboro.ai.superaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.marlboro.ai.superaiagent.constants.FileConstant;
import cn.marlboro.ai.superaiagent.utils.AliOssUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PDFGenerationTool {

//    @Tool(description = "Generate a PDF file with given content",returnDirect = true)
//    public String generatePDF(
//            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
//            @ToolParam(description = "Content to be included in the PDF") String content) {
//        String fileDir = FileConstant.FILE_PATH + "/pdf";
//        String filePath = fileDir + "/" + fileName;
//        try {
//            // 创建目录
//            FileUtil.mkdir(fileDir);
//            // 创建 PdfWriter 和 PdfDocument 对象
//            try (PdfWriter writer = new PdfWriter(filePath);
//                 PdfDocument pdf = new PdfDocument(writer);
//                 Document document = new Document(pdf)) {
//                // 自定义字体（需要人工下载字体文件到特定目录）
////                String fontPath = Paths.get("src/main/resources/static/fonts/simsun.ttf")
////                        .toAbsolutePath().toString();
////                PdfFont font = PdfFontFactory.createFont(fontPath,
////                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
//                // 使用内置中文字体
//                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
//                document.setFont(font);
//                // 创建段落
//                Paragraph paragraph = new Paragraph(content);
//                // 添加段落并关闭文档
//                document.add(paragraph);
//            }
//            return "PDF generated successfully to: " + filePath;
//        } catch (IOException e) {
//            return "Error generating PDF: " + e.getMessage();
//        }
//    }

    private final AliOssUtil aliOssUtil;

    public PDFGenerationTool(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    @Tool(description = "Generate a PDF, save it locally, upload it to OSS, and return its link",
            returnDirect = true)
    public String generatePDF(
            @ToolParam(description = "Name of the PDF file to save locally") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        String fileDir = FileConstant.FILE_PATH + "/pdf";
        Path filePath = Path.of(fileDir, fileName);

        try {
            // 保留本地 PDF 文件
            FileUtil.mkdir(fileDir);

            try (PdfWriter writer = new PdfWriter(filePath.toString());
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);
                document.add(new Paragraph(content));
            }

            // 本地文件生成完成后再上传 OSS
            byte[] pdfBytes = Files.readAllBytes(filePath);
            String objectName = "pdf/" + UUID.randomUUID() + ".pdf";
            String url = aliOssUtil.upload(pdfBytes, objectName);

            return "PDF 已生成并上传，OSS 链接：" + url
                    + "；本地文件：" + filePath.toAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException("生成或读取 PDF 失败：" + filePath, e);
        }
    }
}

