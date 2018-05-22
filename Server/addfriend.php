<?php
$db = new mysqli('localhost', 'root', '7878', 'test');
	// $db->set_charset('utf8');

$userID = $_POST["userID"];
$friendid = $_POST["friendid"];
$friendname = $_POST["friendname"];
$check = $_POST["check"];

$sql = "SELECT * from user_friend where myid = '$userID' and friendid = '$friendid'";
$result = $db->query($sql);
$row_cnt0= $result->num_rows;
// $row = $result->fetch_assoc();
if( $check == 1 ) {
		if ( $row_cnt0 == 1) {
		 $sql1 = "DELETE FROM user_friend where myid = '$userID' and friendid = '$friendid' ";
		 $result = $db->query($sql1);

	 } else {
		 $sql1 = "insert into user_friend values (?, ?, ?)";
		 $stmt = $db->prepare($sql1);
		 $stmt->bind_param("sss", $userID, $friendid, $friendname);
		 $stmt->execute();
	 }
}


// $response = array();
$response["success"] = true;
$response["add"] = $row_cnt0;
echo json_encode($response);

?>
