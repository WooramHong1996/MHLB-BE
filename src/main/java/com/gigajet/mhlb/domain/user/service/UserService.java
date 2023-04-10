package com.gigajet.mhlb.domain.user.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.AESUtil;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.user.social.SocialType;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceInvite;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceInviteRepository;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WorkspaceInviteRepository workspaceInviteRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final AESUtil aesUtil;
    private final JwtUtil jwtUtil;

    @Value("${user.default.image}")
    private String defaultImage;

    @Transactional(readOnly = true)
    public ResponseEntity<SendMessageDto> duplicateEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        return SendMessageDto.toResponseEntity(SuccessCode.CHECKUP_EMAIL);
    }

    public ResponseEntity<SendMessageDto> validateEmail(String email) {
        userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.UNREGISTER_USER));

        return SendMessageDto.toResponseEntity(SuccessCode.VALID_EMAIL);
    }

    @Transactional
    public User register(UserRequestDto.Register registerDto) {
        Optional<User> optionalUser = userRepository.findByEmail(registerDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String password = aesUtil.encrypt(registerDto.getPassword());

        User user = new User(registerDto, password, defaultImage);

        userRepository.save(user);

        return user;
    }

    @Transactional
    public User register(UserRequestDto.Register registerDto, Map<Object, Object> map) {
        WorkspaceInvite workspaceInvite = workspaceInviteRepository.findById(Long.parseLong((String) map.get("inviteId"))).orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITATION));

        if (!registerDto.getEmail().equals(map.get("email"))) {
            throw new CustomException(ErrorCode.NOT_SAME_EMAIL);
        }

        Optional<User> optionalUser = userRepository.findByEmail(registerDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        Workspace workspace = workspaceInvite.getWorkspace();
        if (workspace.getIsShow() == 0) {
            throw new CustomException(ErrorCode.WRONG_WORKSPACE_ID);
        }

        String password = aesUtil.encrypt(registerDto.getPassword());

        User user = new User(registerDto, password, defaultImage);
        userRepository.save(user);

        workspaceUserRepository.save(new WorkspaceUser(user, workspace));
        workspaceInviteRepository.delete(workspaceInvite);

        return user;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<SendMessageDto> login(UserRequestDto.Login loginDto, HttpServletResponse response) {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new CustomException(ErrorCode.UNREGISTER_USER));
        if (user.getType() == SocialType.GOOGLE) {
            throw new CustomException(ErrorCode.SOCIAL_USER);
        }

        if (!aesUtil.encrypt(loginDto.getPassword()).equals(user.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        response.setHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(loginDto.getEmail()));

        return SendMessageDto.toResponseEntity(SuccessCode.LOGIN_SUCCESS);
    }

}
