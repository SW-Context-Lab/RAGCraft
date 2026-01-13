package lab.context.ragcraft.domain.custommodel;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import lab.context.ragcraft.api.custommodel.CustomModelDetailResponse;
import lab.context.ragcraft.api.custommodel.CustomModelListResponse;
import lab.context.ragcraft.domain.source.Source;
import lab.context.ragcraft.domain.source.SourceRepository;
import lab.context.ragcraft.domain.user.User;
import lab.context.ragcraft.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomModelService {

    private final CustomModelRepository customModelRepository;
    private final SourceRepository sourceRepository;
    private final UserRepository userRepository;
    private final EmbeddingModel embeddingModel; // Spring AI가 주입해줌, 내가 application.yaml에  넣은 정보로
    private final ElasticsearchClient elasticsearchClient;
    private final ChatModel chatModel;

    @Transactional
    public CustomModel save(
            Long userId,
            Long sourceId,
            String modelType,
            String displayName,
            String description
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("USER_NOT_FOUND"));

        Source source = sourceRepository.findByIdAndUserId(sourceId, userId)
                .orElseThrow(() -> new IllegalStateException("SOURCE_NOT_FOUND"));

        CustomModel customModel = new CustomModel(
                user,
                source,
                modelType,
                displayName,
                description
        );

        return customModelRepository.save(customModel);
    }


    @Transactional(readOnly = true)
    public List<CustomModelListResponse> getCustomModels(Long userId) {

        // 사용자 존재 검증
        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("USER_NOT_FOUND");
        }

        return customModelRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(model -> new CustomModelListResponse(
                        model.getId(),
                        model.getDisplayName(),
                        model.getDescription()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomModelDetailResponse getCustomModel(
            Long userId,
            Long customModelId
    ) {
        // 사용자 존재 검증
        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("USER_NOT_FOUND");
        }

        CustomModel model = customModelRepository
                .findByIdAndUserId(customModelId, userId)
                .orElseThrow(() -> new IllegalStateException("CUSTOM_MODEL_NOT_FOUND"));

        return new CustomModelDetailResponse(
                model.getId(),
                model.getDisplayName(),
                model.getDescription(),
                model.getModelType(),
                model.getCreatedAt(),
                new CustomModelDetailResponse.SourceInfo(
                        model.getSource().getId(),
                        model.getSource().getDisplayName(),
                        model.getSource().getDescription()
                )
        );
    }

    @Transactional(readOnly = true)
    public String query(
            Long userId,
            Long customModelId,
            String question
    ) throws IOException {
        // 사용자 존재 검증 (기존 컨셉 유지)
        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("USER_NOT_FOUND");
        }

        CustomModel model = customModelRepository
                .findByIdAndUserId(customModelId, userId)
                .orElseThrow(() -> new IllegalStateException("CUSTOM_MODEL_NOT_FOUND"));

        Long sourceId = model.getSource().getId();


        // 질문 임베딩
        // yml에 설정한 gemini-embedding-001 모델을 사용해 질문을 벡터로 변환함
        float[] embed = embeddingModel.embed(question);
        List<Float> queryVector = new ArrayList<>();
        for (float v : embed) queryVector.add(v);


        // vector DB에서 검색 ElasticSearch, 인덱스는 userId, sourceId
        String indexName = "rag-" + userId + "-" + sourceId;
        int k = 5;               // 최종적으로 사용할 청크 개수
        int numCandidates = 100; // 후보로 고려할 벡터 수 (k보다 크게)

        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(indexName)
                .size(k)

                // kNN 쿼리 정의
                .query(q -> q
                        .knn(knn -> knn
                                .field("embedding") // 벡터가 저장된 필드명
                                .queryVector(queryVector)
                                .k(k) // 가장 가까운 k개를 찾음
                                .numCandidates(numCandidates) // 내부 탐색 시 고려할 후보 수, 지금은 "충분히 크게"만 두면 됨
                        )

                )

                // 불필요한 필드는 제외하고
                // RAG에 필요한 content만 가져옴
                .source(src -> src
                        .filter(f -> f
                                .includes("content")
                        )
                )
                .build();

        var response = elasticsearchClient.search(searchRequest, Object.class);
        List<String> contexts = new java.util.ArrayList<>();

        for (var hit : response.hits().hits()) {

            // hit.source()는 Object 타입으로 반환됨
            // 실제로는 ES 문서의 _source(JSON)가 Map 형태로 들어 있음
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> source =
                    (java.util.Map<String, Object>) hit.source();

            // Python에서 저장한 _source 구조:
            // {
            //   "content": "...",
            //   "embedding": [...],
            //   "metadata": {...}
            // }
            // 여기서 RAG에 필요한 content만 꺼냄
            String content = (String) source.get("content");

            contexts.add(content);
            System.out.println("content = " + content);
        }



        // LLM 호출
        // ================================

        // contexts를 하나의 문자열로 합침
        String contextText = String.join("\n\n---\n\n", contexts);

        // 시스템 프롬프트
        String systemPrompt = """
            너는 문서 기반 질의응답 시스템이다.
            주어진 문서 내용에 근거해서만 답변하라.
            문서에 없는 내용은 모른다고 답하라.
            """;

        // 사용자 프롬프트
        String userPrompt = """
            [문서]
            %s
    
            [질문]
            %s
            """.formatted(contextText, question);

            // Prompt 구성
        Prompt prompt = new Prompt(
                    List.of(
                            new SystemMessage(systemPrompt),
                            new UserMessage(userPrompt)
                    )
            );

        // LLM 호출
        String answer = chatModel.call(prompt)
                .getResult()
                .getOutput()
                .getText();
        System.out.println("answer = " + answer);

        return answer;
    }

    @Transactional
    public void deleteCustomModel(
            Long userId,
            Long customModelId
    ){
        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("USER_NOT_FOUND");
        }

        CustomModel model = customModelRepository
                .findByIdAndUserId(customModelId, userId)
                .orElseThrow(() -> new IllegalStateException("CUSTOM_MODEL_NOT_FOUND"));

        customModelRepository.delete(model);
    }
}