<?php
require_once('db_connect.php');

$userid = $_POST['Id'];
$userpw = $_POST['Pw'];
$sql = "insert into user_info (id, password) values ('$userid','$userpw')";
$result =  $db-> mysqli_query("insert into user_info (u_id,u_pw) values ('$userid','$userpw')");

  if($result){
    echo 'success';
  }
  else{
    echo 'failure';
  }

$db->mysqli_close();

?>
