package com.travelland.service.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripComment;
import com.travelland.dto.trip.TripCommentDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.trip.TripCommentRepository;
import com.travelland.repository.trip.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripCommentService {

    private final TripCommentRepository tripCommentRepository;
    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;

    /**
     * 여행후기 댓글 등록
     * @param requestDto 등록할 댓글 정보
     * @param tripId 댓글이 등록될 여행후기 id
     * @param email 로그인한 회원 이메일
     * @return 등록된 댓글 id
     */
    @Transactional
    public TripCommentDto.Id createTripComment(TripCommentDto.Create requestDto, Long tripId, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        TripComment tripComment = tripCommentRepository.save(new TripComment(requestDto, member, trip));

        return new TripCommentDto.Id(tripComment.getId());
    }

    /**
     * 여행후기 댓글 목록 조회
     * @param tripId 댓글이 등록된 여행후기 id
     * @param page 조회할 페이지 번호
     * @param size 한 페이지에 보여지는 댓글 수
     * @return 조회된 댓글 목록
     */
    public List<TripCommentDto.GetList> getTripCommentList(Long tripId, int page, int size) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return tripCommentRepository.getTripCommentList(trip, page, size).stream()
                .map(tripComment -> new TripCommentDto.GetList(
                        tripComment.getContent(),
                        tripComment.getMember().getNickname(),
                        tripComment.getMember().getProfileImage())
                ).toList();
    }

    /**
     * 여행후기 댓글 삭제
     * @param tripId 등록된 댓글의 여행후기 id
     * @param tripCommentId 삭제할 댓글 id
     * @param email 로그인한 회원 이메일
     */
    @Transactional
    public void deleteTripComment(Long tripId, Long tripCommentId, String email) {
        if (!tripRepository.existsById(tripId))
            throw new CustomException(ErrorCode.POST_NOT_FOUND);

        TripComment tripComment = tripCommentRepository.findById(tripCommentId).orElseThrow(() -> new CustomException(ErrorCode.POST_COMMENT_NOT_FOUND));

        if (!tripComment.getMember().getEmail().equals(email))
            throw new CustomException(ErrorCode.POST_COMMENT_DELETE_NOT_PERMISSION);

        tripComment.delete();
    }

    /**
     * 여행후기 댓글 수정
     * @param tripId 수정할 댓글의 여행후기 id
     * @param tripCommentId 수정할 댓글 id
     * @param requestDto 수정할 댓글 정보
     * @param email 로그인한 회원 이메일
     * @return 수정된 댓글 id
     */
    @Transactional
    public TripCommentDto.Id updateTripComment(Long tripId, Long tripCommentId, TripCommentDto.Update requestDto, String email) {
        if (!tripRepository.existsById(tripId))
            throw new CustomException(ErrorCode.POST_NOT_FOUND);

        TripComment tripComment = tripCommentRepository.findById(tripCommentId).orElseThrow(() -> new CustomException(ErrorCode.POST_COMMENT_NOT_FOUND));

        if (!tripComment.getMember().getEmail().equals(email))
            throw new CustomException(ErrorCode.POST_COMMENT_DELETE_NOT_PERMISSION);

        tripComment.update(requestDto);

        return new TripCommentDto.Id(tripCommentId);
    }
}
