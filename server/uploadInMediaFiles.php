<?php
require "DataBase.php";
$db = new DataBase();
$username = null;
$media = null;
$table = "mediafiles";
if (isset($_POST['username'])){
    if ($db->dbConnect()) {
        $username = $_POST['username'];
        
        if($_FILES['upload']){

            $on = $_FILES["upload"]["name"];
            $sn = $_FILES["upload"]["tmp_name"];
            $dn = "images/".$on;
            move_uploaded_file($sn,$dn);

            $query =  "INSERT INTO `mediafiles` (`username` , `photos`) VALUES ('$username','$on')";
            //  $query .= "INSERT INTO mediafiles (username,photos) VALUES ('$username','$on') ";
            // $query = "INSERT INTO userdata WHERE username ='$username' "
            $result = mysqli_query($db->dbConnect(), $query);


            if($result == true){
                echo "uploded successfully";
            }
            else{
                echo "error in uploading";
            }
        }
    }else echo "error in database connection...";
} else echo "why";

?>