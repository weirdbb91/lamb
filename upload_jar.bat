echo "upload start"

call sftp -i C:\Users\weird\Desktop\portfolio-ec2-keyfair.pem -b upload_sftp.bat ubuntu@baekpt.site

echo "upload done"