#启动
nohup java -jar -Xms50m -Xmx215m Rem-1.0.jar >>rem.log 2>>rem-error.log &
#不输出正常日志
nohup java -jar -Xms50m -Xmx215m Rem-1.0.jar >/dev/null 2>>rem-error.log &
#nohup java -jar -Xms50m -Xmx215m Rem_db-1.0.jar >>remdb.log 2>>remdb-error.log &
#关闭
lsof -i:7890
kill -9 pid

#依赖,需要离线安装  sudo su -
jdk
dmidecode
net-tools/ifconfig.netstat
sysstat/iostat
ethtool

nvidia-smi