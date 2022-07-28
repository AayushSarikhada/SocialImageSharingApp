<?php
require "DataBase.php";
$db = new DataBase();
$username = null;
$img_name = null;
$table = "description_liketable";
if (isset($_POST['username'])){
    if ($db->dbConnect()) {
        $username = $_POST['username'];
        $desc_name = $_POST['desc_name'];

			$sql = "DELETE FROM description_liketable WHERE username='$username' AND description='$desc_name'";
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