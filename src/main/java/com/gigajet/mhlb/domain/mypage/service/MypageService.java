package com.gigajet.mhlb.domain.mypage.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.S3Handler;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.mypage.dto.MypageRequestDto;
import com.gigajet.mhlb.domain.mypage.dto.MypageResponseDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final WorkspaceUserRepository workspaceUserRepository;

    private final S3Handler s3Handler;

    @Transactional(readOnly = true)
    public MypageResponseDto.Info userInfo(User user) {

        return MypageResponseDto.Info.builder()
                .user(user)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MypageResponseDto.AllList> workspaceInfo(User user) {

        List<MypageResponseDto.AllList> allLists = new ArrayList<>();

        List<WorkspaceUser> workspaceUsers = workspaceUserRepository.findByUserAndIsShow(user, 1);

        for (WorkspaceUser workspaceUser : workspaceUsers) {
            allLists.add(new MypageResponseDto.AllList(workspaceUser.getWorkspace()));
        }
        return allLists;
    }

    public MypageResponseDto.Image updateImage(User user, MultipartFile userImage) throws IOException {
        // 디폴트 이미지면 삭제 불가하게
        s3Handler.delete(user.getImage());
        String imageUrl = s3Handler.upload(userImage);
        userRepository.updateImage(imageUrl, user.getId());

        return new MypageResponseDto.Image(imageUrl);
    }

    public MypageResponseDto.Name updateName(User user, MypageRequestDto.Name nameRequest) {
        userRepository.updateUserName(nameRequest.getUserName(), user.getId());

        return new MypageResponseDto.Name(nameRequest.getUserName());
    }

    public MypageResponseDto.Description updateDesc(User user, MypageRequestDto.Description descRequest) {
        userRepository.updateDescription(descRequest.getUserDesc(), user.getId());

        return new MypageResponseDto.Description(descRequest.getUserDesc());
    }

    public MypageResponseDto.Job updateJob(User user, MypageRequestDto.Job jobRequest) {
        userRepository.updateJob(jobRequest.getUserJob(), user.getId());

        return new MypageResponseDto.Job(jobRequest.getUserJob());
    }

    @Transactional
    public ResponseEntity<SendMessageDto> deleteWorkspace(User user, long workspaceId) {
        Optional<WorkspaceUser> workspaceUser = workspaceUserRepository.findByUserAndWorkspaceId(user, workspaceId);
        if (workspaceUser.isEmpty()) {
            throw new CustomException(ErrorCode.WRONG_WORKSPACE_ID);
        }
        workspaceUserRepository.deleteByUser_IdAndWorkspace_Id(user.getId(), workspaceId);
        return ResponseEntity.ok(SendMessageDto.of(SuccessCode.DELETE_SUCCESS));
    }
}
