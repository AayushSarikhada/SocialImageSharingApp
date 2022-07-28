<?php
require "DataBase.php";
$db = new DataBase();
$username = null;

$table = "mediafiles";
if (isset($_POST['username'])){
    if ($db->dbConnect()) {
        $username = $_POST['username'];
        
        if(isset($_POST['DESC']) && isset($_POST['IMG_NAME'])){
            $desc = $_POST['DESC'];
            $img_name = $_POST['IMG_NAME'];
            $query =  "UPDATE mediafiles SET description='".$desc."'WHERE username='".$username."' AND photos='".$img_name."'";
            $result = mysqli_query($db->dbConnect(),$query);

            echo $result;
            // if($result == true)
            //     echo "upload successful";
            // else
            //     echo "upload failed";

        
        }else echo "problem with desc parameter";
    }else echo "error in database connection...";
} else echo "all fields required";

?>