<?php
$db = new mysqli('localhost', 'root', '7878', 'test');

$userID = $_POST["userID"];

$sql = "select * from user_profile where id = '$userID' ";
$result = $db->query($sql);
$row_cnt0= $result->num_rows;
 if($row_cnt0 == 1){
	 $row = $result->fetch_assoc();
	 if($row['profile_img_str']== null || $row['profile_img_str']==11){
		 $row['profile_img_str']="default";
	 }
	 if($row['kind_sex']== null ){
		 $row['kind_sex']="default";
	 }
	 if( $row['kind_user']== null ){
		 $row['kind_user']="default";
	 }
	 if($row['experience']== null ){
		 $row['experience']="default";
	 }
	 if($row['list']== null ){
		 $row['list']="default";
	 }
	 if($row['place']== null ){
		$row['place']="default";
	}
	 if( $row['address']== null ){
		  $row['address']="default";
	 }
	 if( $row['checked_designer'] == null ){
		 $row['checked_designer']="0";
	 }

	 $response["userID"] = $row['id'];
	 $response["profile_img_string"] = $row['profile_img_str'];
	 $response["nickName"] = $row['nickname'];
	 $response["userAge"] = $row['userAge'];
	 $response["kind_sex"] = $row['kind_sex'];
	 $response["kind_user"] = $row['kind_user'];
	 $response["experience"] = $row['experience'];
	 $response["list"] = $row['list'];
	 $response["place"] = $row['place'];
	 $response["address_str"] = $row['address'];
	 $response["checked_designer"] = $row['checked_designer'];
	 $response["success"] = true;
 } else {
	 $response["success"] = false;
 }

 	echo json_encode($response);

// 여긴 남기자

// $result = mysqli_query($con, "select * from USER" );
// $response = array();

// while ($row = mysqli_fetch_array($result)) {
// 	array_push($response, array("userID"=>$row[0], "userPassword"=>$row[1], "userName"=>$row[2], "userAge"=>$row[3]));
// }
// 	echo json_encode(array("response"=>$response));

?>
