<?php

$con = mysqli_connect('localhost', 'root', '7878', 'test');
$userID = $_POST["userID"];

$result =  mysqli_query($con, "select friendid, friendname from user_friend where myid = '$userID'" );
$response = array();

while ($row = mysqli_fetch_array($result)) {
	array_push($response, array("friendid"=>$row[0], "friendname"=>$row[1]));
}
	echo json_encode(array("response"=>$response));
?>
