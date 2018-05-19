<?php
$db = new mysqli('localhost', 'root', '7878', 'test');
	// $db->set_charset('utf8');

$sendid = $_POST["sendid"];//보내는사람 아이디
$sendName = $_POST["sendName"];//보내는사람 이름
$receiveid = $_POST["receiveid"];//받는 사람 아이디
$receivename = $_POST["receivename"];//받는 사람 이름
$message = $_POST["message"];//전송한 메시지
$readchk = $_POST["readchk"];//읽은지 확인
$img = $_POST["img"];//전송한 이미지
date_default_timezone_set('Asia/Seoul');
$date = date('Y-m-d H:i:s');

		$sql = "insert into chat_msg values (?, ?, ?, ?, ?, ?, ?, ?)";
		$stmt = $db->prepare($sql);
		$stmt->bind_param("sssssiss", $sendid, $sendName, $receiveid, $receivename, $message, $readchk, $img, $date);
		$stmt->execute();

// $response = array();
$response["success"] = true;
echo json_encode($response);
?>
