<?php
require "DataBase.php";
$db = new DataBase();
$username = null;
$table = "adminTable";

if (isset($_GET['username'])) {
// if(array_key_exists($_POST['username'])){
    if ($db->dbConnect()) {
        $username = $_GET['username'];
       
        $query = "SELECT * FROM ".$table." WHERE username = '" . $username . "'";
        $result = mysqli_query($db->dbConnect(), $query);
        
        if($row = mysqli_fetch_assoc($result)){
                echo "true";
        }else{
            echo "false";
        }
        
    } else echo "Error: Database connection";
// }else echo "array key does not exists"
} else echo "All fields are required";
?>
