<?php
require "DataBase.php";
$db = new DataBase();
$username = null;
$media = null;
$table = "mediafiles";
if (isset($_POST['username'])){
    if ($db->dbConnect()) {
        $username = $_POST['username'];
        
        if(isset($_POST['DESC'])){
            $desc = $_POST['DESC'];
            // $query =  "INSERT INTO `mediafiles` ('username','description') VALUES($username,$desc)";
            $query = "INSERT INTO `mediafiles` (`username` , `description`) VALUES ('$username','$desc')";
            $result = mysqli_query($db->dbConnect(),$query);

            if($result == true)
                echo "upload successful";
            else
                echo "upload failed";

        
        }else echo "problem with desc parameter";
    }else echo "error in database connection...";
} else echo "why";

?>