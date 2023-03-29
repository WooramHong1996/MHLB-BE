package com.gigajet.mhlb.domain.status.sse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class StatusSseHandler {
    //워크스페이스 id별로 구분해야함
    private final ConcurrentHashMap<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(Long workspaceId) throws IOException {
        List<SseEmitter> emitterList = emitters.get(workspaceId);

        if (emitterList == null) {
            emitterList = new ArrayList<>();
            emitters.put(workspaceId, emitterList);
        }

        SseEmitter emitter = new SseEmitter();

        emitterList.add(emitter);

        emitter.onCompletion(() -> {//emitter 종료시 로직
            List<SseEmitter> list = emitters.get(workspaceId);
            list.remove(emitter);
            if (list.isEmpty()) {
                emitters.remove(workspaceId);
            }
        });

        emitter.onTimeout(() -> new IllegalAccessError("타임아웃 시간 안정해놔서 안터짐...아마"));

        emitter.send(SseEmitter.event()
                .name("connect")
                .data("connect success"));
        return emitter;
    }

    public void statusChanged(Long workspaceId, StatusResponseDto dto) {
        List<SseEmitter> emitterList = emitters.get(workspaceId);

        if (emitterList == null) {
            return;
        }

        for (SseEmitter emitter : emitterList) {
            try {
                emitter.send(SseEmitter.event()
                        .name("status changed")
                        .data(dto));
            } catch (IOException e) {
                List<SseEmitter> list = emitters.get(workspaceId);

                list.remove(emitter);

                if (list.isEmpty()) {
                    emitters.remove(workspaceId);
                }
            }
        }
    }
}
