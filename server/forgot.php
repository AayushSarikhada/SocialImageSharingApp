<?php
require "DataBase.php";
require 'PHPMailer/PHPMailerAutoload.php';
require_once 'PHPMailer/class.phpmailer.php';
require_once 'PHPMailer/class.smtp.php';
$db = new DataBase();
// $email = "thisisjaivik@gmail.com";

$email = $_POST['email'];
$sql = "SELECT * FROM userdata WHERE email = '$email'";

$query = mysqli_query($db->dbConnect(),$sql);
if(mysqli_num_rows($query)==1){
    $mail = new PHPMailer(true);
    try {
        //Server settings
        // $mail->SMTPDebug = SMTP::DEBUG_SERVER;                      //Enable verbose debug output
        $mail->isSMTP();                                            //Send using SMTP
        $mail->Host       = 'smtp.gmail.com';                     //Set the SMTP server to send through
        $mail->SMTPAuth   = true;      
        //Enter your sender email                             //Enable SMTP authentication
        $mail->Username   = 'kotadiyajaivik0123@gmail.com';                     //SMTP username
        $mail->Password   = 'error@1337';                               //SMTP password
        $mail->SMTPSecure = 'tls';            //Enable implicit TLS encryption
        $mail->Port       = 587;                                    //TCP port to connect to; use 587 if you have set `SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS`
    
        //Recipients
        $mail->setFrom('kotadiyajaivik0123@gmail.com', 'Jaivik');
        $mail->addAddress($email);     //Add a recipient
               //Name is optional
        $mail->addReplyTo('kotadiyajaivik0123@gmail.com', 'Jaivik');
        // $mail->addCC('cc@example.com');
        // $mail->addBCC('bcc@example.com');
    
        //Attachments
        // $mail->addAttachment('/var/tmp/file.tar.gz');         //Add attachments
        // $mail->addAttachment('/tmp/image.jpg', 'new.jpg');    //Optional name
    
        //Content
        $mail->isHTML(true);                                  //Set email format to HTML
        $mail->Subject = 'Forget password';
        $mail->Body    = "Click here the link below:- 
        http://172.20.10.3/newsapp/resetPasswordForm.php?email='$email'";
        // $mail->AltBody = 'This is the body in plain text for non-HTML mail clients';
    
        if($mail->send());
        echo 'Message has been sent';

    } catch (Exception $e) {
        echo "Message could not be sent. Mailer Error: {$mail->ErrorInfo}";
    }
}
else{
    echo "invalid email"; 
}
?>