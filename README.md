# 🕊 **EarlyBirdy**
EarlyBirdy 어플리케이션은 미라클 모닝 챌린지를 도와주는 어플리케이션입니다. 주요 기획 의도는 사용자들이 아침 일찍 일어나 독서, 운동 등을 통해 개인적인 성장을 추구할 수 있도록 목표를 설정할 수 있게 하고, 이를 달성할 때마다 경험치를 얻어 순위 경쟁에 도전하여 성취감을 얻고, 아침 루틴에 동기부여가 될 수 있는 새로운 미라클 모닝 챌린지 어플리케이션 서비스를 제공하고자 합니다.
</br>

# 📃 **Description**
## 서비스 주요 기능
![img_main](https://github.com/AndroidEarlyBirdy/EarlyBirdy/assets/86705733/c85757ca-97db-492f-b87a-c0b6a66e0fb1)
![img_exp](https://github.com/AndroidEarlyBirdy/EarlyBirdy/assets/86705733/b2541a4c-3f9b-43ba-923b-6772f31141d8)


## 서비스 전체 기능
* 출석 기능: 정해진 알림 시간에 맞추어 시간 경과에 따라  출석 버튼을 누르면 경험치를 차등 지급하고 출석 여부를 보여주는 기능
* 목표 설정 기능: 목표를 설정하고 달성도에 따라 원형 프로그래스 바로 달성도를 보여주는 기능
* 알림 설정 기능: 알림을 설정하여 정해진 시간에 알림을 울리는 기능
* 날씨 기능: 오늘의 날씨를 확인 가능
* 기온 기능: 오늘의 기온을 확인 가능
* 명언 기능: 동기 부여가 될 수 있는 명언을 랜덤으로 보여주는 기능
* 랭킹 기능: 사용자가 얻은 경험치에 따라 랭킹을 보여주는 기능
* 게시판 기능: 게시글을 작성하여 사람들과 소통할 수 있는 기능
* 댓글 기능: 게시글에 댓글을 달아 게시글 내에서 소통할 수 있는 기능
* 달성도 현황 기능: 출석 기능과 달성된 목표의 수에 따라 해달 날짜 당일의 색상이 바뀌는 기능
* 경험치 기능: 출석 기능과 목표 설정 기능에 따라 차등 지급되는 기능
* 레벨 시스템: 경험치를 받아 레벨을 올릴 수 있는 기능
* 프로필 테두리 기능: 레벨에 따라 프로필의 겉 테두리가 바뀌는 기능
* 문의 기능: 불편 사항이나 기타 사항들을 문의할 수 있음
* 신고 기능: 불편한 게시글을 신고할 수 있음

</br>

# 📝 **Design**
## 전체 시스템 구조
## 사용한 라이브러리
|라이브러리|설명|
|---|---|
|Firebase firestore|DBMS|
|Firebase Authentication|사용자 인증을 위한 백엔드 서비스 라이브러리|
|Firebase Storage|파이어베이스 이미지 저장소|
|Glide|이미지 로딩 라이브러리|
|ViewModel|수명주기 고려 데이터를 저장, 관리 라이브러리|
|ShapableImageView|이미지 뷰 라이브러리|
|Lottie|안드로이드 애니메이션 라이브러리|
|WeatherStack|날씨 API|
|material-calendarview|캘린더 라이브러리|

## 아키텍쳐 구조
![img_architecture](https://github.com/AndroidEarlyBirdy/EarlyBirdy/assets/86705733/32c5e151-1e1c-49f3-88c9-b18985cb86e0)


## 사용한 라이선스
|라이선|설명|주소|
|---|---|---|
|Circle ProgresBar|프로그래스 바 외부 라이브러리|https://github.com/dinuscxj/CircleProgressBar|
|MaterialCalendar|캘린더 외부 라이브러리|https://github.com/prolificinteractive/material-calendarview|
|WeatherStack|날씨 API|https://weatherstack.com/|
|Profile Picture|프로필 사진 |<a href="https://www.flaticon.com/kr/free-icons/" title="사람들 아이콘">사람들 아이콘  제작자: Freepik - Flaticon</a>|
|Glide|이미지 로딩 라이브러리|https://bumptech.github.io/glide/|
|파이어베이스|DBMS, 사용자 인증, 이미지 저장소|https://firebase.google.com/?hl=ko|

</br>

#  👪 **Team Member**
<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://github.com/LeeChoongHwan"><img src="https://avatars.githubusercontent.com/u/102038187?v=4" width="100px;"><br /><sub><b>AOS: 이충환 팀장</b></sub></a><br /></a></td>
      <td align="center"><a href="https://github.com/LeeDonghee0917"><img src="https://avatars.githubusercontent.com/u/86705733?v=4" width="100px;"><br /><sub><b>AOS: 이동희 부팀장</b></sub></a><br /></a></td>
      <td align="center"><a href="https://github.com/minji05"><img src="https://avatars.githubusercontent.com/u/65258441?v=4" width="100px;"><br /><sub><b>AOS: 신민지 팀원</b></sub></a><br /></a></td>
      <td align="center"><a href="https://github.com/Odin5din"><img src=https://avatars.githubusercontent.com/u/133902344?v=4" width="100px;"><br /><sub><b>AOS: 김지견 팀원</b></sub></a><br /></a></td>
      <td align="center"><a href="https://github.com/SeungYoonPark"><img src="https://avatars.githubusercontent.com/u/139108875?v=4" width="100px;"><br /><sub><b>AOS: 박승윤 팀원</b></sub></a><br /></a></td>
     <tr/>
  </tbody>
</table>
</br>

# 👩‍💻 **기술 스택**
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white"><img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=Android&logoColor=white"><img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=white">

<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"><img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"><img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"><img src="https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white">

</br>
