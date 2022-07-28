<?php
require "DataBase.php";
$db = new DataBase();
if ($db->dbConnect()) {
    $query = "SELECT * FROM mediafiles";
    $raw = mysqli_query($db->dbConnect(),$query);
    while($res = mysqli_fetch_assoc($raw)){
        $data[] = $res;
    }
    print(json_encode($data));
}else echo "database connection error";
?>