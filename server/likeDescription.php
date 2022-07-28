<?php
require "DataBase.php";
$db = new DataBase();
$username = null;
$img_name = null;
$likeStatus = null;
$table = "description_liketable";
if (isset($_POST['username'])){
    if ($db->dbConnect()) {
        $username = $_POST['username'];
        $likeStatus = $_POST['likeStatus'];
        $desc_name = $_POST['desc_name'];

			$sql = "SELECT * FROM description_liketable WHERE username='$username' AND description='$desc_name'";
			$result = mysqli_query($db->dbConnect(),$sql);

			if ($result->num_rows == 0) {
			  $query =  "INSERT INTO `description_liketable` (`username` , `description` , `status`) VALUES ('$username','$desc_name','$likeStatus')";
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