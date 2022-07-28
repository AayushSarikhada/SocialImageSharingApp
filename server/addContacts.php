<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['DATA'])) {
    $data = $_POST['DATA'];
    $string1 = explode(":",$data);

    if ($db->dbConnect()) {
       for($i = 0;$i<count($string1);$i++){
            $string2 = explode("=",$string1[$i]);
            $table = "contact_info";
            if($db->addContacts($table,$string2[0],$string2[1])){
                echo "Contacts Added Successfully";
            }else{
                echo "Contacts Adding Failed";
            }
       }
    }else echo "Error: Database connection";
        echo $data;
} else echo "NO DATA";

?>
