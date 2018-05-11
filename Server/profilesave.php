<?php
$db = new mysqli('localhost', 'root', '7878', 'test');
	// $db->set_charset('utf8');

$userID = $_POST["userID"];
$profile_img_string = $_POST["profile_img_string"];
$nickName = $_POST["nick_str"];
$userAge = $_POST["age_str"];
$kind_sex = $_POST["kind_sex"];
$kind_user = $_POST["kind_user"];
$experience = $_POST["experience_str"];
$list = $_POST["list"];
$place = $_POST["place"];
$address_str = $_POST["address_str"];
$checked_designer = $_POST["checked_designer"];

$sql = "select * from user_profile where id = '$userID' ";
$result = $db->query($sql);
$row_cnt0= $result->num_rows;
// $row = $result->fetch_assoc();
 if($row_cnt0 == 1){
	 //프로필 이미지, 나이는 수정 안함
	$sql1 = "update user_profile set nickname=?, kind_sex= ?, kind_user=?, experience=?, list=?, place=?, address=?, checked_designer=? where id = ?";
	$stmt = $db->prepare($sql1);
	$stmt->bind_param("sssssssis",  $nickName, $kind_sex, $kind_user, $experience, $list, $place, $address_str, $checked_designer, $userID);
	$stmt->execute();
} else {
	$sql1 = "insert into user_profile values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	$stmt = $db->prepare($sql1);
	$stmt->bind_param("ssssssssssi", $userID, $profile_img_string, $nickName, $userAge, $kind_sex, $kind_user, $experience, $list, $place, $address_str, $checked_designer);
	$stmt->execute();
}

// $response = array();
$response["success"] = true;
echo json_encode($response);

?>
<!--
$statement = mysqli_prepare($db, "update user_profile set profile_img_string=?, nickname=?, userAge=?, kind_sex= ?, kind_user=?, experience=?, list=?, place=?, address=?, checked_designer=? where id = ?");
mysqli_stmt_bind_param($statement, "sssssssssis",  $profile_img_string, $nickName, $userAge, $kind_sex, $kind_user, $experience, $list, $place, $address_str, $checked_designer, $userID);
mysqli_stmt_execute($statement); -->
