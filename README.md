# AutoLearn
## 职教云刷课平台服务端
由于被告知小程序违规，被迫将小程序转为网站项目，地址为[https://icve.chiyouyun.com/](https://icve.chiyouyun.com/)
由于是直接使用uniapp转的，所以没有匹配PC端，PC端显示会有点问题
### 功能
- 使用职教云账号登录
- 通过直接请求职教云接口达到模拟浏览器刷课的过程
- 作业通过第三方接口获取答案，然后模拟请求参数提交到职教云，不过最近发现接口挂了[这里是接口地址http://p.52dw.net:81/chati](http://p.52dw.net:81/chati)
- 只做了接口，并没有做后台界面，只是通过接口查看基本信息
### 信息查看 
| 标题 | URL |
| -------------: | :--------------------------- |
| 设置公告 | /api/set/msg?pwd=123456&m=公告内容 |
| 查看公告 | /api/msg |
| 查看线程池信息 | /api/threadpool/info |
| 查看系统信息 | /api/server/info?pwd=123456 |
| 获取队列信息 | /api/queue/info |
| 获取某用户任务运行状态 | /api/state/task?id=用户id |
| 允许登录 | /login/open |
| 禁止登录 | /login/close |


- 其中配置项 (stackinfo.pwd: 123456) 用于设置访问密码,如上的pwd参数
### 注意事项
**在VideoUtil类中使用了ffmpeg工具，用于获取视频流的视频时长，需要在本地或服务器安装ffmpeg工具，这里是使用Docker安装的opencoconut/ffmpeg镜像，你也可以修改该工具类中的命令进行本地安装**
