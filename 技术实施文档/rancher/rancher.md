> ![@老徐](http://oyiztpjzn.bkt.clouddn.com/avatar.png)老徐
>
> Sunday, 8 April 2018

# Rancher

> Rancher是一个开源的企业级全栈化容器部署及管理平台。
>
> https://www.cnrancher.com

Rancher为容器提供一揽子基础架构服务：CNI兼容的网络服务、存储服务、主机管理、负载均衡、防护墙……

Rancher让上述服务跨越公有云、私有云、虚拟机、物理机环境运行，真正实现一键式应用部署和管理。

> 已有超过8000万次下载，15000+生产环境的应用。

# 搭建

> 虚拟机搭建 Rancher 环境

环境说明：

* Mac
* 虚拟机采用 VirtualBox
* 容器引擎采用 Docker 17.12.1-ce
* 容器云平台采用 Rancher 1.6.15

最终效果：

* 可以在 Rancher 云平台中部署 Docker 应用

## 预备

### 安装 Docker 环境

> 请首先下载并安装 Docker Toolbox，Docker Toolbox中包含了VirtualBox，VirtualBox可以也单独安装最新的版本。

- Mac OS X 10.12.6:
- [VirtualBox - v5.2.2](https://www.virtualbox.org/wiki/Downloads)：虚拟机


- [Docker Toolbox - v1.10.3](https://docs.docker.com/toolbox/toolbox_install_mac/)：Docker 工具箱，包含了 VirtualBox

可选：更改 VirtualBox 虚拟机的存储位置（免得占用太多主存储器的空间）

```bash
$ export MACHINE_STORAGE_PATH="//Volumes/Cloud/Virtual Machines/docker/"
```

### 下载虚拟机镜像

下载创建虚拟机用的镜像文件`~/Downloads/boot2docker.iso`，因为后面会多次用到

```bash
https://github.com/boot2docker/boot2docker/releases/download/v17.12.1-ce/boot2docker.iso
```

> 这里采用docker-17.12.1-ce，不要用过新的版本，rancher 可能不支持

## 创建 Rancher 管理服务器

### 创建虚拟机

创建虚拟机 `rancher` 并登录

```bash
$ docker-machine create -d virtualbox --virtualbox-boot2docker-url \
~/Downloads/boot2docker.iso rancher
$ docker-machine ip rancher
192.168.99.88
$ docker-machine ssh rancher
```

> 这里的 IP 192.168.99.88 就是 rancher 管理服务器的地址
>
> docker-machine ssh rancher 表示登录到虚拟机

由于众所周知的原因，Docker 下载镜像需要使用镜像服务器

在虚拟机内运行修改Docker的启动配置，加上`registry-mirrors`

配置（在虚拟机中执行）

```bash
$ sudo vi /etc/docker/daemon.json
```

```
{
  "registry-mirrors" : ["https://xxxxx.mirror.aliyuncs.com"]
}
```

> https://xxxxx.mirror.aliyuncs.com 是你自己私有的阿里云镜像地址，请去下列地址获得
>
> https://cr.console.aliyun.com/

重启（在虚拟机中执行）

```bash
$ sudo reboot now
```

重新进入虚拟机

```bash
$ docker-machine ssh rancher
```

启动 Rancher Server（在虚拟机中执行）

```bash
$ docker run -d --restart=unless-stopped --name rancher -p 8888:8080 rancher/server
```

> Rancher Server 本身是一个 Docker 容器

启动时间有点长，请耐心等待，可以用日志查看一下进度（在虚拟机中执行）

```bash
$ docker logs -f rancher
```

浏览器访问

```
http://192.168.99.88:8888
```

> 如果不能访问，可能是还没有启动完毕，请等待一段时间

首次登录，还没有配置访问权限，为了安全起见，首先点击上面的 ACCESS CONTROL 来新建一个本地账号和密码。

## 创建 Rancher 节点

Rancher 管理服务器连接和控制 Rancher 节点（Agent Host），要求这些 Host 上安装了 Docker 并且启动了  `rancher/agent` 容器

这里创建一个节点`rancher01`

```bash
$ docker-machine create \
-d virtualbox --virtualbox-boot2docker-url \
~/Downloads/boot2docker.iso rancher01
$ docker-machine ssh rancher01
```

由于众所周知的原因，Docker 下载镜像需要使用镜像服务器

在虚拟机内运行修改Docker的启动配置，加上`registry-mirrors`

配置（在虚拟机中执行）

```bash
$ sudo vi /etc/docker/daemon.json
```

```
{
  "registry-mirrors" : ["https://xxxxx.mirror.aliyuncs.com"]
}
```

> https://xxxxx.mirror.aliyuncs.com 是你自己私有的阿里云镜像地址，请去下列地址获得
>
> https://cr.console.aliyun.com/

重启（在虚拟机中执行）

```bash
$ sudo reboot now
```

重新进入虚拟机

```bash
$ docker-machine ssh rancher01
```

从 Rancher Server Web 界面点击 `Infrastructure` -> `Hosts` 并且复制步骤 5 的命令行代码，类似如下：

```bash
sudo docker run --rm --privileged -v /var/run/docker.sock:/var/run/docker.sock -v /var/lib/rancher:/var/lib/rancher rancher/agent:v1.2.10 http://192.168.99.88:8080/v1/scripts/16E1281C7B7053B1CA74:1514678400000:nx3k1p2p2AvJnWR4WHCTBfznFZ8
```

> 上述代码即用于 `rancher/agent` 主机注册到服务器的代码
>
> **特别注意**：
>
> * 步骤 4 的 IP 地址最好指定一下，否则可能出现两个节点自动检测到的 ip 一样，导致后面的 ipsec 不正常
>

在虚拟机内运行上述代码

```bash
$ sudo docker run --rm --privileged -v /var/run/docker.sock:/var/run/docker.sock -v /var/lib/rancher:/var/lib/rancher rancher/agent:v1.2.10 http://192.168.99.88:8080/v1/scripts/16E1281C7B7053B1CA74:1514678400000:nx3k1p2p2AvJnWR4WHCTBfznFZ8
```

> 同样的操作创建多个Rancher节点

从 Rancher Server Web 界面点击 `Infrastructure` -> `Hosts` 即可看到新节点已经加入进来！

## 检查是否正常

如果一切正常则：

> * 每个 rancher 节点上 healthycheck、ipsec 等服务都应该运行正常，即全不是绿色的
> * 每个 Host 的 IP 地址唯一
> * 可以看到各个节点分配的IP 地址 10.42.x.x
> * 进入任意一个节点后可以 ping 通另外一个节点的 10.42.x.x 地址

如果healthycheck、ipsec 服务不正常，老在重启

> 则有可能是增加节点的时候没有指定 IP，导致 rancher 自动检测出来的 IP 是错误的！！！
>
> 请检查头部的 IP 地址是否重复了，是否正确

### 尝试创建一个应用（Stack）

> 直接启动一个 nginx 镜像，看是否能正常访问

从 Rancher Server Web 界面点击 Stacks->Add Stack

> Rancher 中的应用叫做 Stack，一个 Stack 可以创建多个服务 

* 点击 Add Stack

* 输入 name = nginx

* 点击 Create

* 点击 Add Service

* 输入 name = nginx，Select Image = nginx

  > 这里的 Select Image 即 docker 镜像的名称，你可以跑其他 docker 服务

* 点击 Port Map，暴露端口 8080:80

* 点击 Create

* 等待启动完成

启动完成后，点应用nginx进去，再点击某个服务nginx，点 Ports 这个Tab页，上会显示该服务的IP地址，直接点击这个IP地址即可访问这个服务

| Port | Host IP                                  |
| ---- | ---------------------------------------- |
| 8080 | [192.168.99.101](http://192.168.99.101/) |

结果

```
Welcome to nginx!

If you see this page, the nginx web server is successfully installed and working. Further configuration is required.

For online documentation and support please refer to nginx.org.
Commercial support is available at nginx.com.

Thank you for using nginx.
```

## 测试 LB 功能

> Rancher 的 Load Balancer 其实质是一个 Haproxy

还是刚才的服务，在 Add Service 那个地方点击 Add Load Balancer添加一个 LB 服务

* 名字随便取，比如 nginx-lb

* Target 里选刚才那个 Nginx 服务

* 左边的 Port 是对外的端口，输入 8888，右边的 Port 是原服务的监听端口，输入80

  > 注意：这里不需要原服务暴露端口，只需要原始的监听端口即可

- 点击 Create
- 等待启动完成

启动完成后，点应用nginx进去，再点击某个服务nginx-lb，点 Ports 这个Tab页，上会显示该服务的IP地址，直接点击这个IP地址即可访问这个服务

| Port | Host IP                                  |
| ---- | ---------------------------------------- |
| 8888 | [192.168.99.101](http://192.168.99.101/) |

同时，为了测试LB功能，我们把刚才的 Nginx 服务去掉端口映射并增加到2个节点

* 点服务进去
* 右上角点 Upgrade
* 去掉端口映射
* 点 Upgrade 保存
* 点左边的 Scale ，变成两个节点
* 好了，等待一切调整完毕