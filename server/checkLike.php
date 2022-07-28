<?php
require "DataBase.php";
$db = new DataBase();
$username = null;
$img_name = null;
$likeStatus = null;
$table = "liketable";
if (isset($_POST['username'])){
    if ($db->dbConnect()) {
        $username = $_POST['username'];
        $img_name = $_POST['img_name'];


            $query = " select * from " . $table . " where username = '" . $username . "'";
            // $query = "INSERT INTO userdata WHERE username ='$username' "
            $result = mysqli_query($db->dbConnect(), $query);
            $row = mysqli_fetch_assoc($result);
            if (mysqli_num_rows($result) != 0) {
                $dbusername = $row['username'];
                $dbimg_name = $row['img_name'];
                if ($dbusername == $username && strcmp($dbimg_name,$img_name) ) {
                    echo "liked";
                }
                else{
                    echo "not found";
                }
                
            }else{
                echo "nottt found";
            }
        
    }else echo "error in database connection...";
} else echo "why";

?>