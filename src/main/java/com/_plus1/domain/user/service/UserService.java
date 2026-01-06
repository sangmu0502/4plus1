package com._plus1.domain.user.service;

import com._plus1.common.entity.Like;
import com._plus1.common.entity.Song;
import com._plus1.common.entity.SongArtist;
import com._plus1.common.entity.User;
import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.domain.album.repository.SongArtistRepository;
import com._plus1.domain.like.repository.LikeRepository;
import com._plus1.domain.user.dto.request.UserSignupRequest;
import com._plus1.domain.user.dto.request.UserUpdateRequest;
import com._plus1.domain.user.dto.request.UserWithdrawlRequest;
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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final SongArtistRepository songArtistRepository;
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

        // 이메일 중복 체크
        if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        // 아이디 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        // 연락처 중복 체크
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getEmail(),
                request.getUsername(),
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
        User user = userRepository.findByNicknameAndIsDeletedFalse(nickname)
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
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
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

        if (likes.isEmpty()) {
            return new UserLikeSongsResponse(List.of());
        }

        // 좋아요한 음악 ID들만 추출
        List<Long> songIds = likes.stream()
                .map(like -> like.getSong().getId())
                .toList();

        // 아티스트 정보 한 번에 조회
        List<SongArtist> songArtists = songArtistRepository.findBySongIdInFetchArtistOnly(songIds);

        // 조회를 위해 SongId별 아티스트 이름 리스트로 그룹화
        Map<Long, List<String>> songArtistsMap = songArtists.stream()
                .collect(Collectors.groupingBy(
                        sa -> sa.getSong().getId(),
                        Collectors.mapping(sa -> sa.getArtist().getName(), Collectors.toList())
                ));

        // Response
        List<UserLikeSongsResponse.LikeSongResponse> likeSongList = likes.stream()
                .map(like -> {
                    Song song = like.getSong();
                    List<String> artistNames = songArtistsMap.getOrDefault(song.getId(), List.of());

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

    // 회원 탈퇴 로직
    @Transactional
    public void userWithdraw(Long userId, UserWithdrawlRequest request, User loginUser) {

        // 로그인한 유저와 요청한 ID가 일치하는지 확인
        if (!userId.equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
        }

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호와 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.softDelete();
    }
}
