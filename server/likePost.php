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
        $likeStatus = $_POST['likeStatus'];
        $img_name = $_POST['img_name'];

			$sql = "SELECT * FROM liketable WHERE username='$username' AND img_name='$img_name'";
			$result = mysqli_query($db->dbConnect(),$sql);

			if ($result->num_rows == 0) {
			  $query =  "INSERT INTO `liketable` (`username` , `img_name` , `status`) VALUES ('$username','$img_name','$likeStatus')";
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