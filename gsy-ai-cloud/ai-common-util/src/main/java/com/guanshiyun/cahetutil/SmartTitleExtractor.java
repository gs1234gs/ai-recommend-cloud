package com.guanshiyun.cahetutil;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 智能对话标题提取工具类
 * 从多轮聊天内容中提取核心主题（4~8个字）
 */
public class SmartTitleExtractor {

    // 正则：匹配中英文和数字
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{IsHan}\\w]+");

    // 常见助词、无意义词（动态过滤）
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "我", "你", "他", "她", "的", "了", "呢", "吧", "啊", "是", "不是", "可以",
            "请问", "帮我", "一下", "想", "能", "要", "做", "在", "和", "或者", "然后",
            "如何", "怎么", "什么", "这个", "那个", "有点", "非常", "一个", "一下",
            "是否", "请", "帮", "给", "告诉", "分析", "说明"
    ));

    /**
     * 从多轮聊天记录中提取标题
     * @param messages 聊天消息列表（建议包含前几轮）
     * @return 智能生成的标题，4-8个字
     */
    public static String extractTitle(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            return "新会话";
        }

        // 拼接前几轮对话
        String joined = String.join(" ", messages);
        String clean = joined.replaceAll("[\\p{Punct}\\p{S}]+", " ");

        // 匹配中英文单词
        List<String> tokens = new ArrayList<>();
        var matcher = WORD_PATTERN.matcher(clean);
        while (matcher.find()) {
            String word = matcher.group().trim();
            if (word.length() >= 2 && !STOP_WORDS.contains(word)) {
                tokens.add(word);
            }
        }

        // 统计词频
        Map<String, Long> freq = tokens.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        if (freq.isEmpty()) {
            return "新会话";
        }

        // 高频词排序
        List<String> sorted = freq.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 拼接前几个高频关键词
        String title = String.join("", sorted.subList(0, Math.min(3, sorted.size())));

        // 截断控制长度 4-8 字
        if (title.length() < 4 && sorted.size() > 3) {
            title += sorted.get(3);
        }
        if (title.length() > 8) {
            title = title.substring(0, 8);
        }

        return title;
    }

    /**
     * 从单条消息提取（用于首轮）
     */
    public static String extractFromSingle(String message) {
        return extractTitle(Collections.singletonList(message));
    }
}
