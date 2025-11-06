package com.guanshiyun.cahetutil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 聊天关键词提取工具
 * 用于从聊天内容中提取主题或标题关键字。
 */
public class ChatKeywordExtractor {

    // 常见停用词（可自行扩展）
    private static final Set<String> STOP_WORDS = Set.of(
            "我", "你", "他", "她", "的", "了", "吗", "啊", "吧", "呢", "是", "不是", "怎么", "什么",
            "这个", "那个", "还有", "有点", "请问", "一下", "可以", "能", "要", "给", "做", "在", "和",
            "我们", "他们", "如果", "或者", "以及", "已经", "非常", "真的", "就是", "然后"
    );

    // 英文停用词
    private static final Set<String> EN_STOP_WORDS = Set.of(
            "the", "a", "an", "is", "am", "are", "was", "were", "to", "of", "and", "in", "that", "it",
            "this", "for", "on", "as", "with", "can", "do", "be", "or", "if", "by", "from"
    );

    private static final Pattern NON_WORD = Pattern.compile("[^\\p{IsHan}\\p{L}\\p{N}]");

    /**
     * 提取聊天标题关键词
     * @param content 聊天内容
     * @return 提取出的关键词标题（长度控制在 10 字以内）
     */
    public static String extractTitle(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "新会话";
        }

        // 统一处理
        String clean = NON_WORD.matcher(content).replaceAll(" ").toLowerCase();

        // 按空格或标点分词
        String[] words = clean.split("\\s+");

        // 统计词频
        Map<String, Long> freq = Arrays.stream(words)
                .filter(w -> !w.isBlank())
                .filter(w -> !STOP_WORDS.contains(w))
                .filter(w -> !EN_STOP_WORDS.contains(w))
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        if (freq.isEmpty()) {
            return content.length() > 10 ? content.substring(0, 10) : content;
        }

        // 排序取前几个高频词
        List<String> topWords = freq.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 拼接成标题
        String title = String.join(" ", topWords);

        // 限制标题长度
        return title.length() > 15 ? title.substring(0, 15) : title;
    }
