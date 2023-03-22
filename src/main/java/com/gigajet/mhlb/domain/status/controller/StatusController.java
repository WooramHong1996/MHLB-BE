package com.gigajet.mhlb.domain.status.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.status.service.StatusService;
import com.gigajet.mhlb.domain.status.sse.StatusSseHandler;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/status")
public class StatusController {
    private final StatusService statusService;
    private final StatusSseHandler sseHandler;

    @PostMapping
    public ResponseEntity<SendMessageDto> stausUpdate(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestBody StatusRequestDto statusRequestDto) throws IOException {
        StatusResponseDto dto = statusService.statusUpdate(userDetails.getUser(), statusRequestDto);
        List<Long> workspaceList = statusService.getWorkspaceList(userDetails.getUser());
        for (Long id : workspaceList) {
            sseHandler.statusChanged(id, dto);
        }
        return SendMessageDto.toResponseEntity(SuccessCode.STATUS_CHANGED);
    }

    @GetMapping
    public StatusResponseDto getMyStatus(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return statusService.myStatus(userDetails.getUser());
    }

    @GetMapping("/{id}")//id워크스페이스에 속한 유저들의 상태를 전부 가져옴
    public List getWorkspacePeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return statusService.getWorkspacePeople(userDetails.getUser(), id);
    }

    @GetMapping(value = "/{id}/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)//sse 시작 요청
    public ResponseEntity<SseEmitter> connect(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) throws IOException {
        statusService.checkUser(userDetails.getUser(), id);

//        SseEmitter emitter = new SseEmitter();



//        try {
//            emitter.send(SseEmitter.event()
//                    .name("connect")
//                    .data(emitter.toString()));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        return ResponseEntity.ok(sseHandler.add(id));
    }
}
