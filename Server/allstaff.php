<?php
// 여긴 남기자
$con = mysqli_connect('localhost', 'root', '7878', 'test');
$result =  mysqli_query($con, "select id, profile_img_str,nickname, list, place user from user_profile where checked_designer = 1;" );
$response = array();

while ($row = mysqli_fetch_array($result)) {
	array_push($response, array("id"=>$row[0], "profile_img_str"=>$row[1], "nickname"=>$row[2], "list"=>$row[3], "place"=>$row[4]));
}
	echo json_encode(array("response"=>$response));
?>
