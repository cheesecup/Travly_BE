![떠나볼까 브로셔](https://github.com/cheesecup/Travly_BE/assets/92617830/4b40f78a-3a44-4147-b578-c7737bd8aa9e)

# 프로젝트 소개
- 소개 : 국내 다양한 여행지를 추천하고 여행 계획을 세우며, 다양한 경험을 공유하는 공간
- 기간 : 2024.03.26 ~ 2024.05.06(6주)
- 팀원 : FE(2명) + BE(4명) + 디자이너(1명)

# 백엔드 팀원 소개
| 이름 | 역할 | 담당 기능 | 깃헙 주소 |
|------|-----|------------|-----------|
| 김정원 | BE(BE팀장) | 검색 기능, 랭킹 기능, 추천 기능, 성능 분석 | https://github.com/burning23185 |
| 김대용 | BE(팀원) | 여행 후기/이미지/해시태그 CRUD, 좋아요/스크랩, 댓글 | https://github.com/cheesecup |
| 유성찬 | BE(팀원) | 여행 플랜/투표 CRUD, CI/CD, 무중단배포 | https://github.com/it-is-wanthefull |
| 황승미 | BE(팀원) | 소셜 로그인, 사용자 정보 CRUD, JWT 인증, 알림 | https://github.com/Seungmi97 |

# 기술 스택
- Java 17
- Spring Boot 3.2.4
- Spring Data JPA
- Spring Security
- MySQL
- Redis
- Elasticsearch
- AWS EC2
- AWS S3
- Git Actions

# 서비스 아키텍처
![최종_프로젝트_아키텍처](https://github.com/cheesecup/Travly_BE/assets/92617830/7efdc01e-7504-4c45-9fae-c76aa2564e64)

# 주요기능
- 여행후기
  - 여행후기에 대한 게시글 CRUD
  - 게시글 이미지 업로드(최대5장)
  - 좋아요 및 스크랩 기능 제공
  - 게시글 숨기기 기능 제공
- 여행플랜
  - 여행계획에 대한 CRUD
  - 좋아요 및 스크랩 기능 제공
  - 게시글 숨기기 기능 제공
- 플랜투표
  - 작성한 여행플랜에 대한 유저 투표 기능 제공
- 게시글 검색
  - 게시글 제목/내용에 대한 검색 기능 제공
  - 해시태그를 이용한 검색 기능 제공
  - 여행지역을 이용한 검색 기능 제공
- 인기 게시글 노출
  - 좋아요를 기반으로한 인기 게시글  TOP10 제공
