echo "build start"

call .\gradlew.bat :build

echo "build done"

echo "upload start"

call sftp -i C:\Users\weird\Desktop\portfolio-ec2-keyfair.pem -b upload_sftp.bat ubuntu@baekpt.site

echo "upload done"

echo "connect to the EC2"

call ssh -i C:\Users\weird\Desktop\portfolio-ec2-keyfair.pem ubuntu@baekpt.site ./jar_reload.sh
