package cn.marlboro.ai.agentimagesearchmcpsever.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ImageSearch {

    private static final String SEARCH_URL = "https://api.unsplash.com/search/photos";

    private final String accessKey;

    public ImageSearch(@Value("${unsplash.access-key}") String accessKey) {
        this.accessKey = accessKey;
    }

    @Tool(description = "Search Unsplash photos and return comma-separated regular image URLs.")
    public String searchPhotos(
            @ToolParam(description = "Photo search terms") String query,
            @ToolParam(description = "Page number, default 1", required = false) Integer page,
            @ToolParam(description = "Results per page, 1-30, default 10", required = false) Integer perPage,
            @ToolParam(description = "Sort order: relevant or latest", required = false) String orderBy,
            @ToolParam(description = "Comma-separated collection IDs", required = false) String collections,
            @ToolParam(description = "Content filter: low or high", required = false) String contentFilter,
            @ToolParam(description = "Color: black_and_white, black, white, yellow, orange, red, purple, magenta, green, teal, or blue", required = false) String color,
            @ToolParam(description = "Orientation: landscape, portrait, or squarish", required = false) String orientation,
            @ToolParam(description = "ISO 639-1 query language; beta parameter", required = false) String lang) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query must not be blank");
        }

        int requestedPage = page == null ? 1 : page;
        int requestedPerPage = perPage == null ? 10 : perPage;
        if (requestedPage < 1) {
            throw new IllegalArgumentException("page must be at least 1");
        }
        if (requestedPerPage < 1 || requestedPerPage > 30) {
            throw new IllegalArgumentException("perPage must be between 1 and 30");
        }

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("query", query);
        params.put("page", requestedPage);
        params.put("per_page", requestedPerPage);
        putIfPresent(params, "order_by", orderBy);
        putIfPresent(params, "collections", collections);
        putIfPresent(params, "content_filter", contentFilter);
        putIfPresent(params, "color", color);
        putIfPresent(params, "orientation", orientation);
        putIfPresent(params, "lang", lang);

        try (HttpResponse response = HttpRequest.get(SEARCH_URL)
                .form(params)
                .header("Authorization", "Client-ID " + accessKey)
                .header("Accept-Version", "v1")
                .execute()) {
            if (!response.isOk()) {
                throw new IllegalStateException(
                        "Unsplash API returned HTTP " + response.getStatus()
                                + ": " + response.body());
            }

            JSONArray results = JSONUtil.parseObj(response.body()).getJSONArray("results");
            if (results == null) {
                return "";
            }

            return IntStream.range(0, results.size())
                    .mapToObj(results::getJSONObject)
                    .filter(photo -> photo != null && photo.getJSONObject("urls") != null)
                    .map(photo -> photo.getJSONObject("urls").getStr("regular"))
                    .filter(url -> url != null && !url.isBlank())
                    .collect(Collectors.joining(","));
        }
    }

    private static void putIfPresent(Map<String, Object> params, String key, String value) {
        if (value != null && !value.isBlank()) {
            params.put(key, value);
        }
    }
}