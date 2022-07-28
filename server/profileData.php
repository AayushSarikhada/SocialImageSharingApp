<?php
require "DataBase.php";
$db = new DataBase();
$username = null;
$password = null;
$table = "userdata";

if (isset($_POST['username']) && isset($_POST['password'])) {
// if(array_key_exists($_POST['username'])){
    if ($db->dbConnect()) {
        $username = $_POST['username'];
        $password = $_POST['password'];
        // $username = $db->prepareData($username);
        // $password = $db->prepareData($password);
        $query = "select * from " . $table . " where username = '" . $username . "'";
        $result = mysqli_query($db->dbConnect(), $query);
        $json_data = array();
        while($row = mysqli_fetch_assoc($result)){
                // $dbusername = $row['username'];
                // if($dbusername == $username){
                   
                    $json_data = $row;
                // }
        }
        echo json_encode(array($json_data));
    } else echo "Error: Database connection";
// }else echo "array key does not exists"
} else echo "All fields are required";
?>
