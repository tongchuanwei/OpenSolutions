> ![@老徐](http://oyiztpjzn.bkt.clouddn.com/avatar.png)老徐
>
> Sunday, 7 March 2018
>
> 本文假设你对 React 已经比较熟悉！参考[React Redux Quick Start](../react-redux-quick-start/react-redux-quick-start.md)
>

# React WebSocket Polling

> React 程序使用 WebSocket 获取实时推送数据

# 基础

## 实时数据推送

在Web或移动项目中，服务器向客户端实时推送消息是一种常见的业务需求。

**实现方式**

1. Polling：轮询（俗称“拉”），即定期重新请求数据。
2. Long-Polling：长轮询，是 Polling 技术的改进，即在保持住一个请求，在这个请求内不断发送数据。
3. WebSocket Polling：是 Long-Polling 技术的改进，即通过HTTP协议握手建立连接后直接进行双向TCP通讯。

**应用场景**：

- 聊天室
- 股票价格变化、K线图
- 消息提醒

## WebSocket Polling

> 简单点说就是：通过HTTP协议进行握手建立连接后直接进行双向TCP通讯

WebSocket是HTML5新增加的一种通信协议，目前流行的浏览器都支持这个协议，例如 Chrome，Safari，Firefox，Opera，IE等等，对该协议支持最早的应该是Chrome，从Chrome12就已经开始支持，随着协议草案的不断变化，各个浏览器对协议的实现也在不停的更新。该协议还是草案，没有成为标准，不过成为标准应该只是时间问题了。&emsp;

## SockJS

> 包装了 WebSocket 之后的高级 API，我们通常不会直接使用 WebSocket 而是使用 SockJS

应对不支持 WebSocket 的场景，如果 WebSocket 不可用，则使用其他协议模拟 WebSocket API。

# 实验

## 编程环境

Mac下安装 `node.js`推荐：

```bash
$ brew install node
$ node -v
v6.11.4
```

> Windows 等其他环境可以直接下载安装包或自行百度
>
> https://nodejs.org/en/download/

Mac下安装 `yarn`推荐：

```bash
$ brew install yarn
$ yarn -v
1.5.1
```

> Windows 等其他环境可以直接下载安装包或自行百度
>
> https://yarnpkg.com/lang/en/docs/install/

安装 `create-react-app` 

```bash
$ yarn global add create-react-app
```

之后我们就可以通过 create-react-app 来快速创建 react 项目

## HelloWorld

> 这个程序功能很简单：当客户端通过 Websocket 连接到服务端之后服务端即开始推送数据到客户端

### Websocket 服务端

```bash
$ cd react/websocket-helloworld
$ mkdir server
$ cd server
```

安装依赖包

```bash
$ yarn add socket.io express
```

创建文件 app.js

```js
var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

app.get('/', function (req, res) {
  res.sendFile(__dirname + '/public/index.html');
});

io.of('/my-namespace').on('connection', (client) => {
  client.on('subscribeToTimer', (interval) => {
    console.log('client is subscribing to timer with interval ', interval);
    setInterval(() => {
      client.emit('timer', new Date());
    }, interval);
  });
});

const port = 8000;
io.listen(port);
console.log('listening on port ', port);
```

> 指定 websocket 的 endpoint 即入口地址为 `/my-namespace`
>
> 然后监听事件 `subscribeToTimer`
>
> 收到事件 `subscribeToTimer`后开始像客户端定时发送消息`timer`，消息的数据内容是当前时间

创建文件 public/index.html，这里只是普通的页面展示和 websocket 无关

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>React Websocket Demo</title>
  </head>
  <body>
    <h1>React Websocket Demo</h1>
  </body>
</html>
```

运行

```bash
$ node app.js
```

### Websocket 客户端

```bash
$ cd react/websocket-helloworld
$ mkdir client
$ cd client
```

创建 React 项目环境

```bash
$ yarn create react-app client
$ cd client
$ rm -rf src/*
```

创建文件 src/index.js

```jsx
import React from 'react';
import ReactDOM from 'react-dom';

import { subscribeToTimer } from './api/subscribeToTimer';

class App extends React.Component {
  constructor(props) {
    super(props);
    subscribeToTimer((err, timestamp) => this.setState({
      timestamp
    }));
  }
  
  state = { timestamp: 'no timestamp yet' }

  render() {
    return (
      <h1>This is the timer value: {this.state.timestamp}</h1>
    );
  }
}

ReactDOM.render(<App />, document.getElementById('root'));
```

> 在组件进行构造时即调用 subscribeToTimer 开始请求数据
>
> 当收到数据后回调，用 this.setState 改变 React 组件状态从而刷新界面

创建文件 src/api/subscribeToTimer.js

```jsx
import io from 'socket.io-client';

const socket = io.connect('http://localhost:8000/my-namespace');

function subscribeToTimer(cb) {
  socket.on('timer', timestamp => cb(null, timestamp));
  socket.emit('subscribeToTimer', 1000);
}

export { subscribeToTimer };
```

> 指定 websocket 的服务器地址为`http://localhost:8000/`
>
> 指定 websocket 的 endpoint 即入口地址为 `/my-namespace`
>
> subscribeToTimer 用于向服务器发一个消息请求数据
>
> 当请求到数据以后调用回调函数 cb

启动

```bash
$ yarn start
```

即可看到自动打开浏览器 http://localhost:3000/ 并显示如下内容，其中时间部分会不断收到服务端数据进行刷新

```
This is the timer value: 2018-03-07T12:40:35.613Z
```