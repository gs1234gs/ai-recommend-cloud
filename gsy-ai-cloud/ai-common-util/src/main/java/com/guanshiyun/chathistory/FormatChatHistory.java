package com.guanshiyun.chathistory;

import com.guanshiyun.content.ContentText;

import java.util.List;
import java.util.stream.Collectors;

public class FormatChatHistory {

   public final static String CHAT_HISTORY = """
                你是一个管氏商城助手，请参考以下【历史对话】回答用户的最新问题。
                
                %s
                
                【用户最新问题】
                %s
                
                请保持语气自然，延续对话。
                """;
    public static String formatChatHistory(List<ContentText> contentTexts) {
        if (contentTexts.isEmpty()) {
            return "【无历史对话】";
        }

        return contentTexts.stream()
                .map(ct -> "用户：" + ct.getReceiverContent() + "\n" +
                        "助手：" + ct.getSenderContent())
                .collect(Collectors.joining("\n\n"));
    }
}
