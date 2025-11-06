package com.guanshiyun;


import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.util.Map;
@SpringBootTest
public class ReReading implements BaseAdvisor {
    private static final String MODEL_NAME = """
           {re2}
           Read the question again: {re2}
            """;

    @Override
    public AdvisedRequest before(AdvisedRequest request) {
        String contents = request.toPrompt().getContents();
        PromptTemplate template = promptTemplate(MODEL_NAME);
        Prompt re2 = template.create(Map.of("re2", contents));
        return AdvisedRequest.from(request)
                .userText(re2.getContents())
                .adviseContext(request.adviseContext())
                .build();
    }

    @Override
    public AdvisedResponse after(AdvisedResponse advisedResponse) {

        return advisedResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public PromptTemplate promptTemplate( String modelName) {
        return new PromptTemplate(modelName);
    }
    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }
}
