<?php

$db = new mysqli('localhost', 'root', '7878', 'test');
$sendid = $_POST["sendid"];//보내는사람 아이디
// $sendName = $_POST["sendname"];//보내는사람 이름
$receiveid = $_POST["receiveid"];//받는 사람 아이디
// $receivename = $_POST["receivename"];//받는 사람 이름
$start_length = $_POST["start_length"];
$query1 = "select * from chat_msg where sendid = '$sendid' and receiveid = '$receiveid' || sendid = '$receiveid' and receiveid = '$sendid' order by time asc" ;
// $cnt = $result->num_rows;
// $cnt_last = $cnt-10;
$result1 = $db->query($query1);
$cnt = $result1->num_rows;

$cnt_last = $cnt-20-$start_length;
if($cnt_last >= 1){
	$query = "select * from chat_msg where sendid = '$sendid' and receiveid = '$receiveid' || sendid = '$receiveid' and receiveid = '$sendid' order by time asc limit $cnt_last, 10" ;

} else {
	$query = "select * from chat_msg where sendid = '$sendid' and receiveid = '$receiveid' || sendid = '$receiveid' and receiveid = '$sendid' order by time asc limit 0, $cnt" ;
	$start_length = -1;
}
	$result = $db->query($query);

// $result =  mysqli_query($con, "select * from chat_msg where sendid = '$sendid' and receiveid = '$receiveid' || sendid = '$receiveid' and receiveid = '$sendid' order by time asc limit '$cnt_last', ''$cnt';" );
$response = array();

while ($row = mysqli_fetch_array($result)) {
	array_push($response, array("sendid"=>$row[0], "sendname"=>$row[1], "receiveid"=>$row[2], "receivename"=>$row[3],	"message"=>$row[4], "readchk"=>$row[5], "img"=>$row[6], "time"=>$row[7]));
}
	echo json_encode(array("response"=>$response));
?>
<!-- $con = mysqli_connect('localhost', 'root', '7878', 'test');
$sendid = $_POST["sendid"];//보내는사람 아이디
$sendName = $_POST["sendname"];//보내는사람 이름
$receiveid = $_POST["receiveid"];//받는 사람 아이디
$receivename = $_POST["receivename"];//받는 사람 이름
$result =  mysqli_query($con, "select * from chat_msg where sendid = '$sendid' and receiveid = '$receiveid' || sendid = '$receiveid' and receiveid = '$sendid' order by time asc;" );
$response = array();

while ($row = mysqli_fetch_array($result)) {
	array_push($response, array("sendid"=>$row[0], "sendname"=>$row[1], "receiveid"=>$row[2], "receivename"=>$row[3], "message"=>$row[4], "readchk"=>$row[5], "img"=>$row[6], "time"=>$row[7]));
}
	echo json_encode(array("response"=>$response)); -->
