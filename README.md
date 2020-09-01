# fpv-Remote-Control
一个可以用4G/WIFI图传和遥控飞行器的上位机APP(支持蓝牙遥控)
# 下载

https://github.com/h13-0/fpv-Remote-Control/releases
https://www.coolapk.com/apk/com.h13studio.fpv

# 图传部分
针对图传部分,我只提供 Linux开发板 的配置方法,对于使用'esp-eyes'的请自行按照http/udp协议使用。

截止2020-08-10,无论是小白还是大佬,都推荐用 `Motion` 进行http图传。

## http图传环境搭建
**实测延迟0.2秒**
这里以`Motion`为例,在Linux的Shell环境中:
先尝试:
```
sudo apt-get install motion
```
如果没有这个库的话 需要用源码安装
https://motion-project.github.io
```
make -j4
sudo make install
```

然后去`/etc/motion/motion.conf`里面正确选择cameraID,
输入:
```
sudo nano /etc/motion/motion.conf
```
没有`nano`文本编辑器的可自行安装或用`vim`替换

然后找到下文,配置Camera ID:
```
###########################################################
# Capture device options
############################################################

# Videodevice to be used for capturing  (default /dev/video0)
# for FreeBSD default is /dev/bktr0
videodevice /dev/video0
```

然后配置分辨率 帧率 端口等。
随后在内网环境中,用浏览器打开对应的网址
eg:
```
http://192.168.1.100:8081
```
然后如果能正常显示图传图像,则http图传配置部分完成。

## UDP图传配置
**目前无论是APP端还是Linux端UDP图传均未调试优化完毕,以下方案目前延迟在0.8秒左右**
先说一下截止2020-08-10的udp图传方案吧。
Linux端主动向APP端发送UDP数据,然后APP端读取本地端口的UDP视频流播放。
但是Sever端主动向Client端发数据并不是很好的解决办法,不过这个问题以后再解决,目前主要需要先优化UDP图传的延迟和质量。
### ffmpeg环境搭建
ffmpeg官网:
https://ffmpeg.org/

先试试你是否已经完全安装ffmpeg
```
ffmpeg -f video4linux2 -s 640*480 -i /dev/video0 -vcodec h264 -preset ultrafast -tune zerolatency -r 30 -b:v 1024K -movflags +faststart -f mpegts udp://127.0.0.1:8888 
```
注意, `/dev/video0` 是你摄像头的文件位置。
如果报错,请按照以下步骤执行。如果没有报错,请直接跳到UDP环节的最后一个步骤。

#### 源码安装

建议从Github上下载源码到电脑再传到到Linux板子上并解压
https://github.com/FFmpeg/FFmpeg
,然后:
```
cd ffmpeg
./configure --enable-shared --enable-libx264 --enable-gpl
make #请自行根据Linux开发板性能开启多线程编译
sudo make install
```
如果肉身在墙外或者Linux开发板自带梯子的话,推荐直接使用官方git源
```
git clone https://git.ffmpeg.org/ffmpeg.git
```
来代替github下载步骤。
如果不知道怎么代替的话,建议忽略这一部分。

然后执行:
```
sudo ffmpeg -f video4linux2 -s 640*480 -i /dev/video0 -vcodec h264 -preset ultrafast -tune zerolatency -r 30 -b:v 1024K -movflags +faststart -f mpegts udp://你手机IP:8888 
```

#### 报错&解决方案:
##### libavdevice.so.58:
即报错:
```
ffmpeg: error while loading shared libraries: libavdevice.so.58: cannot open shared object file: No such file or directory
```
则原因为 未将 `libavdevice.so.58` 等依赖文件添加到path中。
输入:
```
ldd ffmpeg
```
查看对应缺失的依赖
```
ldd ffmpeg
	libavdevice.so.58 => not found
	libavfilter.so.7 => not found
	libavformat.so.58 => not found
	libavcodec.so.58 => not found
	libpostproc.so.55 => not found
	libswresample.so.3 => not found
	libswscale.so.5 => not found
	libavutil.so.56 => not found
	libm.so.6 => /lib/arm-linux-gnueabihf/libm.so.6 (0xb6ed8000)
	libpthread.so.0 => /lib/arm-linux-gnueabihf/libpthread.so.0 (0xb6eb4000)
	libc.so.6 => /lib/arm-linux-gnueabihf/libc.so.6 (0xb6dc7000)
	/lib/ld-linux-armhf.so.3 (0xb6f76000)
```
然后
```
ls /usr/local/lib/libavdevice.so.58
```
一般上述文件都在这个目录里。

验证:
```
export LD_LIBRARY_PATH=/usr/local/lib/
```
这一个命令只是暂时性的把依赖目录加入当前Shell的环境变量中,重新打开Shell即失效,较为安全。
然后再次输入
```
ffmpeg
```
如果没有报错,请按照以下步骤执行:
```
sudo nano /etc/ld.so.conf
```
在文件中**添加**路径:
```
/usr/local/lib
sudo ldconfig
```

接下来加入全局环境变量路径:
```
sudo nano /etc/profile
```
**<font color='red'>下列命令具有非常高危险性,请仔细核对后再操作</font>**
在文档中加入:
```
export PATH="/usr/local/ffmpeg/bin:$PATH"
```
**<font color='red'>一定要注意加上最后的 “:$PATH” 不然你会丢失所有环境变量</font>**
然后保存并运行
```
source /etc/profile
```
丢失环境变量后的解决方法:
**一定不要重启,一定不要关闭当前Shell**
```
/usr/bin/vim /etc/profile
```
然后正确修改和保存后,
```
source /etc/profile
```

##### ERROR: libx264 not found:

```
ERROR: libx264 not found
```
则需要安装 `libx264` 编解码器。

则:
```
git clone git@code.videolan.org:videolan/x264.git
./configure --enable-shared --enable-pthread --enable-pic
make
sudo make install
```
然后再次执行回到ffmpeg目录再次从 `./configure --enable-shared --enable-libx264 --enable-gpl` 开始执行即可。

##### can't configure encoder
大概率是你 `configure` 的时候没有设置 `--enable-libx264`

##### make时报错:recompile with -fPIC
出现该现象的大致有两种原因
1.依赖库没有安装好
2.更改ffmpeg的 `./configure` 后未清理上次编译缓存

对于1 请自行排坑
对于2 建议先执行 `make clean` 后再 `make`

#### 对于源码安装的一些建议
并不是所有库都提供了可靠的卸载方式,所以建议将所有 `sudo make install` 替换为 `checkinstall`
他会自动帮你编译好的文件打包为 deb 或者 rpm 包,再用对应包管理器进行安装,方便卸载。
checkinstall安装:
```
apt install auto-apt checkinstall
```
使用:
```
./configure --enable-shared --enable-pthread --enable-pic
make
checkinstall
sudo dpkg -i xxx.deb
```

#### ffmpeg命令的官方文档
https://trac.ffmpeg.org/wiki/StreamingGuide

给几个昨晚我从里面扒到的几个比较有用的设置吧

一个最精简的UDP图传指令是这样的
```
ffmpeg -f video4linux2 -i /dev/video0 -vcodec h264 -acodec copy -f mpegts udp://你手机IP:8888
```
然后可以这样拆分:
`ffmpeg` `-f video4linux2` `-i /dev/video0` `-vcodec h264` `-f mpegts udp://你手机IP:8888`

**重要配置:**
编码器零延迟,应加到 `-vcodec h264` 后
```
-tune zerolatency
```

预设超快速,应加到 `-vcodec h264` 后
```
-preset ultrafast
```

分辨率设置,应加到 `-f video4linux2` 后
```
-s 640*480
```

码率设置,应加到 `-vcodec h264` 后
```
-b:v 1024K
```

帧率设置,应加到 `-vcodec h264` 后
```
-r 30
```

允许快速连接,他会将一些重要的信息移动到文件头,可以让你在完全下载之前就开始播放,
```
-movflags +faststart
```

不过,就算这样折腾完,仍然有0.8秒的延迟...

**中等重要配置:**
I帧设置:
```
-keyint_min 15 -g 15 -sc_threshold 0 
```

最后,命令也就成了这样
```
sudo ffmpeg -f video4linux2 -s 640*480 -i /dev/video0 -vcodec h264 -preset ultrafast -tune zerolatency -r 30 -b:v 1024K -movflags +faststart -f mpegts udp://你手机IP:8888 
```

最后,送你一个 rtp 图传的配置,rtp也是基于udp,如果用rtp的话,接收端配置会复杂一些,并且效果和udp也没有明显区别,故这里不推荐

```
sudo ffmpeg -f video4linux2 -s 320*240 -i /dev/video0 -vcodec h264 -preset ultrafast -tune zerolatency -r 30 -b:v 1024K -movflags +faststart -f rtp rtp://192.168.1.154:8888 -itsscale 1
```

# 网络连接部分
**本APP支持任何形式的网络连接,包括4G,WIFI等**
## 4G连接
这里推荐使用Zerotier进行内网穿透连接而不推荐使用ipv6直连。
### Zerotier
Zerotier官网: 
https://www.zerotier.com/
#### Linux端
```
curl -s https://install.zerotier.com | sudo bash
```
然后创建和加入虚拟局域网。
#### APP接收端
去上文提到的'Zerotier'官网下载对应APP,创建和加入虚拟局域网即可。
### ipv6访问
因为ipv6是动态变化的,并且ipv6的资源在ipv4下无法访问,故不推荐使用。
如果迫不得已一定要使用ipv6,则推荐相应DDNS工具绑定域名。

# 远程控制部分
本APP提供两种远控方案,TCP和蓝牙串口。其中Linux开发板上请使用TCP方式,单片机上可以配合蓝牙串口模块使用(推荐)也可以配合esp8266等使用TCP控制。
## 回传数据格式V1.0
因为考虑到要使用蓝牙传输数据,故这里使用二进制数据包传递。
数据包基础格式:
'固定包头 0x66','ID','value xn'...,'固定包尾 0x70','固定包尾 0x76'
其中 0x66 0x70 0x76 分别为字符 'f' 'p' 'v'的二进制值,即本app包名。
### RockerView摇杆
'f','ID:0x0x','distance','value','value','p','v'
其中 'ID:0x0x' 则表示 '0x00' '0x01' '0x02'等 都有可能是摇杆控件的标识字节
```
eg:
  'f','0x00','0x07','0x01','0x05','p','v'
则表示:
  第 1 个摇杆返回的数据,其:
    'distance' = '0x00' //distance为摇杆距离圆心的距离(暂不支持)
    'value' = '0x0105'  //value为摇杆角度,拆分为低八位和高八位进行传输

eg:
  'f','0x01','0x10','0x00','0xf5','p','v'
则表示:
  第 2 个摇杆返回的数据,其:
    'distance' = '0x10' //distance为摇杆距离圆心的距离(暂不支持)
    'value' = '0x00f5'  //value为摇杆角度,拆分为低八位和高八位进行传输
```
distance特性暂不支持

### Slider滑杆
'f','ID:0x1x','value','p','v'

```
eg:
  'f','0x10','0x55','p','v'
则表示:
  第 1 个滑杆返回的数据,其:
    'value' = '0x55'  //摇杆的有效值为0x55
```
### Button按钮
'f','0x2x','value','p','v'
```
eg:
  'f','0x22','0x01','p','v'
则表示:
  写着 3 的按钮被按下。(因为按钮上的文字是从1开始的)
```
## 接收数据格式
**因为数据收到后将直接展示到UI界面,故请回传字符串,<font color = 'red'>并以"\r\n"结尾</font>**
好了,就这么多。

# 解析库文件
咕咕咕,过两天可能顺便更新一下协议再放出。
