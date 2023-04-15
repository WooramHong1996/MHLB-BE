package com.gigajet.mhlb.domain.workspace.controller;

import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceRequestDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.service.WorkspaceService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Workspace", description = "워크스페이스 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @Operation(summary = "워크스페이스 상세정보 리스트 조회 API", description = "가입된 워크스페이스가 없으면 빈 배열이 반환되서 응답됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 상세정보 리스트 조회 성공")
    })
    @GetMapping
    public List<WorkspaceResponseDto.AllList> getWorkspageList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return workspaceService.findWorkspaceList(userDetails.getUser());
    }

    @Operation(summary = "워크스페이스 선택 페이지 워크스페이스 생성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 생성 성공"),
            @ApiResponse(responseCode = "400", description = "E-01, E-02, E-03", content = @Content(schema = @Schema(implementation = SendMessageDto.class))),
            @ApiResponse(responseCode = "500", description = "E-04", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public WorkspaceResponseDto.Response postWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @RequestPart(name = "image", required = false) MultipartFile image,
                                                       @RequestPart("data") WorkspaceRequestDto.Create workspaceDto) throws IOException {
        return workspaceService.createWorkspace(userDetails.getUser(), image, workspaceDto);
    }

    @Operation(summary = "메인 페이지 워크스페이스 정보와 로그인한 회원 권한 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메인 페이지 워크스페이스 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, E-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @GetMapping("/")
    public WorkspaceResponseDto.InfoAndRoll getWorkspaceInfoAndRoll(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                    @Parameter @RequestParam(name = "id") Long workspaceId) {
        return workspaceService.findWorkspaceInfoAndRoll(userDetails.getUser(), workspaceId);
    }

    @Operation(summary = "메인 페이지 워크스페이스 목록 순서 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 목록 순서 변경 성공")
    })
    @PatchMapping("/order")
    public ResponseEntity<SendMessageDto> patchOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @io.swagger.v3.oas.annotations.parameters.RequestBody @RequestBody WorkspaceRequestDto.Orders orders) {
        return workspaceService.changeOrder(userDetails.getUser(), orders);
    }

//    @Operation(summary = "사용중지", description = "사용중지")
//    @GetMapping("/order")
//    public List<WorkspaceResponseDto.OrderList> getOrder(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return workspaceService.findOrder(userDetails.getUser());
//    }

    @Operation(summary = "메인 페이지 워크스페이스에 속한 유저 정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 사용자들 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "W-01, E-11", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @GetMapping("/{workspaceId}/people")
    public List<WorkspaceResponseDto.People> getPeople(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @Parameter @PathVariable Long workspaceId) {
        return workspaceService.findPeople(userDetails.getUser(), workspaceId);
    }
}
