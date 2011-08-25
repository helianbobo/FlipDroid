ps -ef|grep java|grep it.tika.APIServer|awk '{print "kill -9 "$2}'|sh

ps -ef|grep java|grep it.tika.Server|awk '{print "kill -9 "$2}'|sh

#ps -ef|grep java|grep "python tcc.py"|awk '{print "kill -9 "$2}'|sh

#ps -ef|grep java|grep "/user/bin/python manage.py"|awk '{print "kill -9 "$2}'|sh

cd ~/tika-api
. ./run.sh
. ./startTikaThriftService.sh

#cd ~/tika-preload

#screen python tcc.py
#screen -d

#cd callwebui

#screen python manage.py runserver 10.131.31.98:8000 &
#screen -d

#cd ~/tika-api
tail -f nohup.out
