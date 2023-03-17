package com.gigajet.mhlb.domain.mypage.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.mypage.dto.ChangeMypageDto;
import com.gigajet.mhlb.domain.mypage.dto.MypageDto;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final WorkspaceUserRepository workspaceUserRepository;

    @Transactional(readOnly = true)
    public MypageDto.UserResponse userInfo(User user) {

        return MypageDto.UserResponse.builder()
                .user(user)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MypageDto.AllList> workspaceInfo(User user) {

        List<MypageDto.AllList> allLists = new ArrayList<>();

        List<WorkspaceUser> workspaceUsers = workspaceUserRepository.findByUser(user);

        for (WorkspaceUser workspaceUser : workspaceUsers) {
            allLists.add(new MypageDto.AllList(workspaceUser.getWorkspace()));
        }
        return allLists;
    }

    public MypageDto.ImageResponse updateImage(User user, MultipartFile image) {
        User users = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.DUPLICATE_EMAIL));
        //나중에 만들기
        return new MypageDto.ImageResponse(users);
    }

    public MypageDto.NameResponse updateName(User user, ChangeMypageDto.NameRequest nameRequest) {
        User users = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.DUPLICATE_EMAIL)
        );
        users.updateName(nameRequest);
        return new MypageDto.NameResponse(users);
    }

    public MypageDto.DescResponse updateDesc(User user, ChangeMypageDto.DescRequest descRequest) {
        User users = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.DUPLICATE_EMAIL)
        );
        users.updateDesc(descRequest);
        return new MypageDto.DescResponse(users);
    }

    public MypageDto.JobResponse updateJob(User user, ChangeMypageDto.JobRequest jobRequest) {
        User users = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.DUPLICATE_EMAIL)
        );
        users.updateJob(jobRequest);
        return new MypageDto.JobResponse(users);
    }
}
