
<h3>Application : HairThere</h3>

서비스 설명 : 저렴한 헤어 서비스를 받도록 헤어샵 스텝과 고객을 연결해주는 서비스

<hr>

[기본 아이디어]<br>
헤어샵의 스텝들은 디자이너가 되기 위해서 헤어 모델이 필요함.<br>
무료 또는 저렴한 비용으로 커트, 염색, 펌 등을 해주고 샵에서 받을 수 있는 서비스까지 해주며 모델을 구함 (염색, 펌의 경우 약 값만 받음)<br>
→ 고객은 저렴하게 서비스를 받을 수 있고, 스텝들은 디자이너가 되기 위한 과정을 진행할 수 있게 됨<br>
(‘카카오헤어샵’을 참고하며 만들었습니다.)<br>

<hr>

[사용 기술]
-	Language : Java, PHP<br>
-	OS : Android, Ubuntu<br>
-	Server : AWS EC2, Apache<br>
-	Database : Mysql<br>
-	Protocol : Http, Tcp<br>
-	Library / API : OpenCV(NDK) – 얼굴 인식, Picasso, Facebook Login API, Kakao Login API, GoogleMaps API, IAMPORT API, Volley, CardView, Recyclerview, JSON

<hr>

[주요 기능]
1.	실시간 채팅<br>
2.	타임라인과 좋아요 기능(스크롤 페이징)<br>
3.	구글맵을 이용한 위치 확인<br>
4.	SNS 간편 로그인 (페이스북, 카카오톡)<br>
5.	서비스 쿠폰 구매를 위한 결제 기능<br>
6.	얼굴을 가리고 촬영 가능 (모델이 얼굴 공개를 원하지 않는 경우 사용)<br>

<hr>

[상세 설명]
-	실시간 채팅<br>
디자이너들의 목록에서 원하는 사용자를 친구 등록/삭제할 수 있으며, 채팅이 가능합니다.<br>

-	타임라인<br>
디자이너가 시술한 모델을 사진으로 남겨 다른 사용자들에게 보여줍니다. 시술 결과를 보고 문의나 서비스 받고 싶은 사용자를 만들 수 있습니다.<br>

-	구글맵 위치 확인<br>
구글맵을 이용하여 자신이 서비스를 제공하는 곳을 설정해야 합니다. 설정한 장소는 마커로 표시되고 사용자는 마커와 주소를 통해 자신이 갈 수 있는 곳인지 확인할 수 있습니다.<br>

-	결제 기능<br>
서비스를 받기 위해서는 쿠폰을 구매해야 합니다. 선 결제를 통해 NO show 방지와 서비스를 받을 권리를 이끌어 낼 수 있습니다. IAMPORT API를 이용해서 카카오 결제, 삼성 페이, 신용카드 결제 등을 할 수 있습니다.<br>

-	얼굴 가리기 촬영<br>
모델이 얼굴 공개를 원하지 않을 경우 얼굴 가리기 촬영으로 사진을 찍을 수 있습니다. 카메라에서 인식한 얼굴에 동그란 원을 이용하여, 얼굴만 가려주고 머리스타일만 보이는 상태로 촬영이 가능합니다.<br>



[![Watch the video](https://raw.github.com/GabLeRoux/WebMole/master/ressources/WebMole_Youtube_Video.png)](https://youtu.be/fCtYRn-Uo5w)


