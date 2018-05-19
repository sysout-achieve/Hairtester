<?php
$db = new mysqli('localhost', 'root', '7878', 'test');
	// $db->set_charset('utf8');

$room = $_POST["room"];//보내는사람 아이디
$roomid = $_POST["roomid"];//보내는사람 이름
$recentmsg = $_POST["recentmsg"];//최근 메시지
$readchk = $_POST["readchk"];//읽은지 확인
$host = $_POST["host"];//방 주인
date_default_timezone_set('Asia/Seoul');
$date = date('Y-m-d H:i:s');

$sql = "SELECT * from chatroom where room = '$room' and host = '$host'";
$result = $db->query($sql);
$row_cnt0 = $result->num_rows;
// $row = $result->fetch_assoc();
	if ( $row_cnt0 != 1) {
		$sql1 = "insert into chatroom values (?, ?, ?, ?, ?, ?)";
		$stmt = $db->prepare($sql1);
		$stmt->bind_param("sssiss", $room, $roomid, $recentmsg, $readchk, $host, $date);
		$stmt->execute();
	} else {
		if($recentmsg=="nosave"){
			$sql1 = "UPDATE chatroom SET readchk ='$readchk' where room = '$room' and host = '$host'";
				$result = $db->query($sql1);
		} else {
			$sql2 = "SELECT * from chatroom where room = '$room' and host = '$host'";
			$result2 = $db->query($sql2);
			$row = $result->fetch_assoc();
			if ( $readchk == 0 ){
				$sql1 = "UPDATE chatroom SET recentmsg = '$recentmsg', readchk ='$readchk', time = '$date' where room = '$room' and host = '$host'";
			} else{
				$readchk = $row['readchk']+1;
				$sql1 = "UPDATE chatroom SET recentmsg = '$recentmsg', readchk ='$readchk', time = '$date' where room = '$room' and host = '$host'";
			}
				$result = $db->query($sql1);
		}
	}
// $response = array();
$response["success"] = true;
echo json_encode($response);
?>
