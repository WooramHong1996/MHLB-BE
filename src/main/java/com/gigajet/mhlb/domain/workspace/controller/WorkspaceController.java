package com.gigajet.mhlb.domain.workspace.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.mail.service.MailService;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceRequestDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.service.WorkspaceService;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceInvite;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Workspace", description = "워크스페이스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @Operation(summary = "워크스페이스 상세정보 리스트", description = "워크스페이스 선택 페이지 워크스페이스 상세정보 리스트 조회 API")
    @GetMapping
    public List workspaceAllList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return workspaceService.workspaceAllList(userDetails.getUser());
    }

    @Operation(summary = "워크스페이스 생성", description = "워크스페이스 선택 페이지 워크스페이스 생성 API")
    @PostMapping
    public WorkspaceResponseDto.Response workspaceCreate(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                         @RequestPart(name = "image", required = false) MultipartFile image,
                                                         @RequestPart("data") WorkspaceRequestDto.Create workspaceDto) throws IOException {
        return workspaceService.workspaceCreate(userDetails.getUser(), image, workspaceDto);
    }

    @Operation(summary = "이거 안쓰는거임", description = "이거 안쓰는거임")
    @GetMapping("/{id}/inbox")
    public List inboxGet(@AuthenticationPrincipal UserDetailsImpl userDetails,
                         @PathVariable Long id,
                         @RequestParam(defaultValue = "3") Integer size) {
        return workspaceService.inboxGet(userDetails.getUser(), id, size);
    }

    @Operation(summary = "워크스페이스 정보, 회원 권한", description = "메인 페이지 워크스페이스 정보와 로그인한 회원 권한 조회 API")
    @GetMapping("/")
    public WorkspaceResponseDto.InfoAndRoll infoAndRoll(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @RequestParam Long id) {
        return workspaceService.infoAndRoll(userDetails.getUser(), id);
    }

    @Operation(summary = "워크스페이스 목록 순서 변경", description = "메인 페이지 워크스페이스 목록 순서 변경 API")
    @PatchMapping("/order")
    public ResponseEntity<SendMessageDto> changeOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestBody WorkspaceRequestDto.Orders orders) {
        return workspaceService.changeOrder(userDetails.getUser(), orders);
    }

    @Operation(summary = "사용중지", description = "사용중지")
    @GetMapping("/order")
    public List<WorkspaceResponseDto.OrderList> getOrder(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return workspaceService.getOrder(userDetails.getUser());
    }

    @Operation(summary = "워크스페이스에 속한 유저 정보", description = "메인 페이지 워크스페이스에 속한 유저 정보 조회 API")
    @GetMapping("/{id}/people")
    public List<WorkspaceResponseDto.People> getPeople(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return workspaceService.getPeople(userDetails.getUser(), id);
    }
}
