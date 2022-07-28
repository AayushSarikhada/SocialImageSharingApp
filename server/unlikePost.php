<?php
require "DataBase.php";
$db = new DataBase();
$username = null;
$img_name = null;
$table = "liketable";
if (isset($_POST['username'])){
    if ($db->dbConnect()) {
        $username = $_POST['username'];
        $img_name = $_POST['img_name'];

			$sql = "DELETE FROM liketable WHERE username='$username' AND img_name='$img_name'";
			$result = mysqli_query($db->dbConnect(),$sql);

            if($result == true){
                echo "successful";
            }
            else{
                echo "fail";
            }
			          
    }else echo "error in database connection...";
} else echo "why";

?>