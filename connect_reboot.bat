
echo "EC2 connect"

call ssh -i C:\Users\weird\Desktop\portfolio-ec2-keyfair.pem ubuntu@baekpt.site ./jar_reload.sh

echo "reboot done"