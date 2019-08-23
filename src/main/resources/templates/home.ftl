<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<title>LayIM 3.x PC版本地演示</title>

<link rel="stylesheet" href="/layui/css/layui.css">
    <script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
<style>
html{background-color: #333;}
</style>
</head>
<body>

<button id="bt1">添加所有用户为好友</button>



<script src="/layui/layui.js"></script>
<script>
var userid = ${Session.UserInfo.id};
if(!/^http(s*):\/\//.test(location.href)){
  alert('请部署到localhost上查看该演示');
}
var websocket = null;

$(function () {
    $("#bt1").click(function () {
        $.post("/user/friend/add/all",{},function (data) {
            if(data.code==0){
                alert("未登录");
            }
            if(data.code==200){
                alert("添加成功");
            }
        })
    });
})


function start(){

layui.use('layim', function(layim){
  
  //演示自动回复
  var autoReplay = [
    '您好，我现在有事不在，一会再和您联系。', 
    '你没发错吧？face[微笑] ',
    '洗澡中，请勿打扰，偷窥请购票，个体四十，团体八折，订票电话：一般人我不告诉他！face[哈哈] ',
    '你好，我是主人的美女秘书，有什么事就跟我说吧，等他回来我会转告他的。face[心] face[心] face[心] ',
    'face[威武] face[威武] face[威武] face[威武] ',
    '<（@￣︶￣@）>',
    '你要和我说话？你真的要和我说话？你确定自己想说吗？你一定非说不可吗？那你说吧，这是自动回复。',
    'face[黑线]  你慢慢说，别急……',
    '(*^__^*) face[嘻嘻] ，是贤心吗？'
  ];
  
  //基础配置
  layim.config({

    //初始化接口
    init: {
      url: "/user/"+userid+"/init.json"
      ,data: {}
    }
    
    //或采用以下方式初始化接口
    /*
    ,init: {
      mine: {
        "username": "LayIM体验者" //我的昵称
        ,"id": "100000123" //我的ID
        ,"status": "online" //在线状态 online：在线、hide：隐身
        ,"remark": "在深邃的编码世界，做一枚轻盈的纸飞机" //我的签名
        ,"avatar": "a.jpg" //我的头像
      }
      ,friend: []
      ,group: []
    }
    */
    

    //查看群员接口
    ,members: {
      url: 'json/getMembers.json'
      ,data: {}
    }
    
    //上传图片接口
    ,uploadImage: {
      url: '/upload/image' //（返回的数据格式见下文）
      ,type: '' //默认post
    } 
    
    //上传文件接口
    ,uploadFile: {
      url: '/upload/file' //（返回的数据格式见下文）
      ,type: '' //默认post
    }
    
    ,isAudio: true //开启聊天工具栏音频
    ,isVideo: true //开启聊天工具栏视频
    
    //扩展工具栏
    ,tool: [{
      alias: 'code'
      ,title: '代码'
      ,icon: '&#xe64e;'
    }]
    
    //,brief: true //是否简约模式（若开启则不显示主面板）
    
    //,title: 'WebIM' //自定义主面板最小化时的标题
    //,right: '100px' //主面板相对浏览器右侧距离
    //,minRight: '90px' //聊天面板最小化时相对浏览器右侧距离
    ,initSkin: '5.jpg' //1-5 设置初始背景
    //,skin: ['aaa.jpg'] //新增皮肤
    //,isfriend: false //是否开启好友
    //,isgroup: false //是否开启群组
    //,min: true //是否始终最小化主面板，默认false
    ,notice: true //是否开启桌面消息提醒，默认false
    //,voice: false //声音提醒，默认开启，声音文件为：default.mp3
    
    ,msgbox: layui.cache.dir + 'css/modules/layim/html/msgbox.html' //消息盒子页面地址，若不开启，剔除该项即可
    ,find: layui.cache.dir + 'css/modules/layim/html/find.html' //发现页面地址，若不开启，剔除该项即可
    ,chatLog: layui.cache.dir + 'css/modules/layim/html/chatlog.html' //聊天记录页面地址，若不开启，剔除该项即可
    
  });

  /*
  layim.chat({
    name: '在线客服-小苍'
    ,type: 'kefu'
    ,avatar: 'http://tva3.sinaimg.cn/crop.0.0.180.180.180/7f5f6861jw1e8qgp5bmzyj2050050aa8.jpg'
    ,id: -1
  });
  layim.chat({
    name: '在线客服-心心'
    ,type: 'kefu'
    ,avatar: 'http://tva1.sinaimg.cn/crop.219.144.555.555.180/0068iARejw8esk724mra6j30rs0rstap.jpg'
    ,id: -2
  });
  layim.setChatMin();*/

  //监听在线状态的切换事件
  layim.on('online', function(data){
    //console.log(data);
  });
  
  //监听签名修改
  layim.on('sign', function(value){
      $.ajax({
          type: "POST",
          url: "/user/update/sign",
          data: {"sign":value},
          success: function(data){
              console.log(data);
          }
      });
  });

  //监听自定义工具栏点击，以添加代码为例
  layim.on('tool(code)', function(insert){
    layer.prompt({
      title: '插入代码'
      ,formType: 2
      ,shade: 0
    }, function(text, index){
      layer.close(index);
      insert('[pre class=layui-code]' + text + '[/pre]'); //将内容插入到编辑器
    });
  });
  
  //监听layim建立就绪
  layim.on('ready', function(res){

    //console.log(res.mine);
    
    layim.msgbox(5); //模拟消息盒子有新消息，实际使用时，一般是动态获得
  
    //添加好友（如果检测到该socket）
    layim.addList({
      type: 'group'
      ,avatar: "http://tva3.sinaimg.cn/crop.64.106.361.361.50/7181dbb3jw8evfbtem8edj20ci0dpq3a.jpg"
      ,groupname: 'Angular开发'
      ,id: "12333333"
      ,members: 0
    });
    layim.addList({
      type: 'friend'
      ,avatar: "http://tp2.sinaimg.cn/2386568184/180/40050524279/0"
      ,username: '冲田杏梨'
      ,groupid: 2
      ,id: "1233333312121212"
      ,remark: "本人冲田杏梨将结束AV女优的工作"
    });
    
    setTimeout(function(){
      //接受消息（如果检测到该socket）
      layim.getMessage({
        username: "Hi"
        ,avatar: "http://qzapp.qlogo.cn/qzapp/100280987/56ADC83E78CEC046F8DF2C5D0DD63CDE/100"
        ,id: "10000111"
        ,type: "friend"
        ,content: "临时："+ new Date().getTime()
      });
      
      /*layim.getMessage({
        username: "贤心"
        ,avatar: "http://tp1.sinaimg.cn/1571889140/180/40030060651/1"
        ,id: "100001"
        ,type: "friend"
        ,content: "嗨，你好！欢迎体验LayIM。演示标记："+ new Date().getTime()
      });*/
      
    }, 3000);
  });

  //监听发送消息
  layim.on('sendMessage', function(data){
    var To = data.to;
    //console.log(data);
    
    if(To.type === 'friend'){
      //layim.setChatStatus('<span style="color:#FF5722;">对方正在输入。。。</span>');
    }
      console.log(data);
    var mine=data.mine;
    var to=data.to;
      websocket.send(JSON.stringify({
          type:"chat",
          from:data.mine.id,
          to:data.to.id,
          data:{
              username: mine.username 	//消息来源用户名
              ,avatar: mine.avatar		//消息来源用户头像
              ,id: mine.id 				//消息的来源ID（如果是私聊，则是用户id，如果是群聊，则是群组id）
              ,type: to.type 			//聊天窗口来源类型，从发送消息传递的to里面获取
              ,content: mine.content 	//消息内容
              //,mine: false 			//是否我发送的消息，如果为true，则会显示在右方
              ,fromid: mine.id 			//消息的发送者id（比如群组中的某个消息发送者），可用于自动解决浏览器多窗口时的一些问题
              //,timestamp: new Date().getTime() 	//服务端时间戳毫秒数。注意：如果你返回的是标准的 unix 时间戳，记得要 *1000
          }
      }));

      /*//演示自动回复
      setTimeout(function(){
        var obj = {};
        if(To.type === 'group'){
          obj = {
            username: '模拟群员'+(Math.random()*100|0)
            ,avatar: layui.cache.dir + 'images/face/'+ (Math.random()*72|0) + '.gif'
            ,id: To.id
            ,type: To.type
            ,content: autoReplay[Math.random()*9|0]
          }
        } else {
          obj = {
            username: To.name
            ,avatar: To.avatar
            ,id: To.id
            ,type: To.type
            ,content: autoReplay[Math.random()*9|0]
          }
          layim.setChatStatus('<span style="color:#FF5722;">在线</span>');
        }
        layim.getMessage(obj);
      }, 1000);*/
  });

  //监听查看群员
  layim.on('members', function(data){
    //console.log(data);
  });
  
  //监听聊天窗口的切换
  layim.on('chatChange', function(res){
    var type = res.data.type;
    console.log(res.data.id)
    if(type === 'friend'){
      //模拟标注好友状态
      //layim.setChatStatus('<span style="color:#FF5722;">在线</span>');
    } else if(type === 'group'){
      //模拟系统消息
      layim.getMessage({
        system: true
        ,id: res.data.id
        ,type: "group"
        ,content: '模拟群员'+(Math.random()*100|0) + '加入群聊'
      });
    }
  });



    //判断当前浏览器是否支持WebSocket
    if('WebSocket' in window){
        websocket = new WebSocket("ws://192.168.0.102:8080/websocket/${Session.UserInfo.id}");
        //websocket = new WebSocket("ws://47.94.218.62:8080/websocket/${Session.UserInfo.id}");
    }
    else{
        alert('Not support websocket')
    }

    //连接发生错误的回调方法
    websocket.onerror = function(){
        console.log("error");
    };

    //连接成功建立的回调方法
    websocket.onopen = function(event){
        console.log("open");
        console.log(event);
    }

    //接收到消息的回调方法
    websocket.onmessage = function(event){
        console.log("收到消息："+event.data);
        var result = JSON.parse(event.data);
        if(result.type=="chat"){
            console.log(result);
            result.data.mine=(result.from==userid);
            layim.getMessage(result.data);
        }
        if (result.type == "offline") {
            layim.setFriendStatus(result.userid, 'offline');
        }
        if (result.type == "online") {
            layim.setFriendStatus(result.userid, 'online');
        }
        if (result.type == "updateSign") {
            $(".layim-friend"+result.userid).find("p").text(result.sign);
        }
    }

    //连接关闭的回调方法
    websocket.onclose = function(){
        console.log("close");
    }

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function(){
        websocket.close();
    }



    //发送消息
    function send(){
        var message = document.getElementById('text').value;
        websocket.send(message);
    }
  
  

});


}

start();


$(function () {

})


</script>
</body>
</html>
