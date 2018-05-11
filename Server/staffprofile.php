<?php

$con = mysqli_connect('localhost', 'root', '7878', 'test');
$staffID = $_POST["staffID"];

$result =  mysqli_query($con, "select id, profile_img_str, nickname, list, place, address user from user_profile where id = '$staffID'" );
$response = array();

while ($row = mysqli_fetch_array($result)) {
	array_push($response, array("id"=>$row[0], "profile_img_str"=>$row[1], "nickname"=>$row[2], "list"=>$row[3], "place"=>$row[4],"address"=>$row[5]));
}
	echo json_encode(array("response"=>$response));
?>
