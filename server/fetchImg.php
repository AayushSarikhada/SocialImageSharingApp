<?php
require "DataBase.php";
$db = new DataBase();
$username = null;
$password = null;
$table = "userdata";


if (isset($_POST['username']) ) {

    if ($db->dbConnect()) {
        $username = $_POST['username'];
      
        $username = $db->prepareData($username);
//        
       
        $query = "SELECT * FROM mediafiles WHERE username = '$username'";
        $result = mysqli_query($db->dbConnect(), $query);
        // $raw = mysqli_fetch_assoc($result);
		
		if($result){
			if($result->num_rows==0){
				echo "empty";
			}
			else{
				while($res = mysqli_fetch_assoc($result)){
					
					$data[] = $res;
					}
				
					print(json_encode($data));
					
				   
				}
		}
       
    } else echo "Error: Database connection";

} else echo "All fields are required";
 ?>