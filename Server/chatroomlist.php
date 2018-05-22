<?php

$con = mysqli_connect('localhost', 'root', '7878', 'test');
$userID = $_POST["userID"];
$result =  mysqli_query($con, "SELECT room, roomin, recentmsg, readchk, host, time from chatroom where host = '$userID'  order by time desc" );
$response = array();

while ($row = mysqli_fetch_array($result)) {
	array_push($response, array("room"=>$row[0], "roomin"=>$row[1], "recentmsg"=>$row[2], "readchk"=>$row[3], "host"=>$row[4],"time"=>$row[5]));
}
	echo json_encode(array("response"=>$response));
?>
