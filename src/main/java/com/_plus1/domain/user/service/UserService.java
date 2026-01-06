package com._plus1.domain.user.service;

import com._plus1.common.entity.Like;
import com._plus1.common.entity.Song;
import com._plus1.common.entity.User;
import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.domain.like.repository.LikeRepository;
import com._plus1.domain.user.dto.request.UserSignupRequest;
import com._plus1.domain.user.dto.request.UserUpdateRequest;
import com._plus1.domain.user.dto.response.UserGetProfileResponse;
import com._plus1.domain.user.dto.response.UserLikeSongsResponse;
import com._plus1.domain.user.dto.response.UserSignupResponse;
import com._plus1.domain.user.dto.response.UserUpdateProfileResponse;
import com._plus1.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final PasswordEncoder passwordEncoder;

    // 작성자 : 이상무
    // 유저 정보가 필요해서 임시로 불러올 유저 메서드 생성
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        return userRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("더미 유저가 존재하지 않습니다."));
    }


    // 회원가입
    @Transactional
    public UserSignupResponse signup(UserSignupRequest request) {

        // 이미 가입된 이메일인 경우 예외 발생
        if (userRepository.existsByEmail(request.getEmail())) {
            log.info("이미 가입된 이메일입니다. {}", ErrorCode.DUPLICATE_EMAIL);
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getUserName(),
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getPhoneNumber()
        );

        User savedUser = userRepository.save(user);

        return new UserSignupResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getPhoneNumber(),
                savedUser.getCreatedAt()
        );

    }

    // 사용자 프로필 조회 로직
    @Transactional(readOnly = true)
    public UserGetProfileResponse getProfile(String nickname) {

        // 로그인할 때 사용한 닉네임으로 유저 조회
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserGetProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCreatedAt()
        );
    }

    // 사용자 프로필 수정 로직
    @Transactional
    public UserUpdateProfileResponse updateProfile(Long userId, UserUpdateRequest request) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 프로필 수정
        user.update(
                request.getUserName(),
                request.getEmail(),
                request.getNickname(),
                request.getPhoneNumber()
        );

        return new UserUpdateProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // 사용자 좋아요 음악 조회 로직
    @Transactional(readOnly = true)
    public UserLikeSongsResponse getUserLikeSongs(Long userId) {

        // 해당 유저가 좋아요한 목록 조회
        List<Like> likes = likeRepository.findAllByUserIdWithSongAndArtist(userId);

        List<UserLikeSongsResponse.LikeSongResponse> likeSongList = likes.stream()
                .map(like -> {
                    Song song = like.getSong();

                    if (song == null) {
                        throw new CustomException(ErrorCode.SONG_NOT_FOUND);
                    }

                    // 아티스트 이름 추출
                    List<String> artistNames = song.getSongArtists().stream()
                            .map(songArtist -> songArtist.getArtist().getName())
                            .toList();

                    return new UserLikeSongsResponse.LikeSongResponse(
                            song.getId(),
                            like.getId(),
                            song.getTitle(),
                            artistNames
                    );
                })
                .toList();

        return new UserLikeSongsResponse(likeSongList);
    }
}
