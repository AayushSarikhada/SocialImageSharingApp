<?php
require "DataBase.php";
$db = new DataBase();
$username = null;
$table = "liketable";
// $data[] = null;
if (isset($_POST['username'])){
    if ($db->dbConnect()) {
        $username = $_POST['username'];
        


            $query = " select * from " . $table . " where username = '" . $username . "'";
            // $query = "INSERT INTO userdata WHERE username ='$username' "
            $result = mysqli_query($db->dbConnect(), $query);
            if($result){
                if($result->num_rows==0){
                    echo "empty";
                }else{
                    while($row = mysqli_fetch_assoc($result)){
                        if($row != null){
                            $data[] = $row;
                        }
                            
                    }
                    print(json_encode($data));
                }
                
            }
            
        
    }else echo "error in database connection...";
} else echo "all fields are required";

?>