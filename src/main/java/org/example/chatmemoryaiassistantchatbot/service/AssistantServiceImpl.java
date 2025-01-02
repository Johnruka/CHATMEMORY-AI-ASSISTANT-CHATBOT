package org.example.chatmemoryaiassistantchatbot.service;

import org.springframework.ai.model.Content;
import org.example.chatmemoryaiassistantchatbot.config.ChatConfig;
import org.example.chatmemoryaiassistantchatbot.dao.UserData;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AssistantServiceImpl implements AssistantService {

    private final OpenAiChatModel openAiChatModel;
    private final ChatConfig chatConfig;

    private final ChatMemory chatMemory;

    private final UserData userData;

    @Autowired
    public AssistantServiceImpl(OpenAiChatModel openAiChatModel, ChatConfig chatConfig, ChatMemory chatMemory, UserData userData) {
        this.openAiChatModel = openAiChatModel;
        this.chatConfig = chatConfig;
        this.chatMemory = chatMemory;
        this.userData = userData;
    }


    @Override
    public String chatMemory(String chatId, String input) {
        // Retrieve previous context from ChatMemory.
        List<Message> previousMessages = chatMemory.get(chatId, 100);

        String previousContext = previousMessages.isEmpty() ? "" : previousMessages.stream()
                .map(Content::getContent)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));


        String difficulty = "Beginner";

        String prompt = String.format(chatConfig.teacherAssistantPromptTemplateNew(), input, difficulty);

        if (!previousContext.isEmpty()) {
            prompt = previousContext + "\n" + prompt;
        }

        Prompt chatbotPrompt = new Prompt(prompt, chatConfig.teacherAssistantChatOptions());
        ChatResponse response = openAiChatModel.call(chatbotPrompt);

        Generation result = response.getResult();

        UserMessage userMessage = new UserMessage(input); // it represents the user's input.
        SystemMessage systemMessage = new SystemMessage(result.getOutput().getContent()); // it represents AI's response.
        chatMemory.add(chatId, userMessage);
        chatMemory.add(chatId, systemMessage);

        return result.getOutput().getContent();
    }


    @Bean("operation1")
    @Description("operation1")
    public Function<RequestDto, String> operation1() {
        return request -> {
            System.out.println("operation1 has been executed!");
            System.out.println(request.input());
            userData.register(request.input());
            return "Operation1 is Done!";
        };
    }


    @Bean("operation2")
    @Description("operation1")
    public Function<RequestDto, String> operation2() {
        return request -> {
            System.out.println("operation2 has been executed!");
            System.out.println(userData.getRegisteredNAmes());
            return "Operation2 is Done!";
        };
    }

    public record RequestDto(String input){}
    }

