## Java大作业 葫芦娃大战妖精

### 设计思想

本次作业是基于JavaFX的多线程网络对战游戏，相对较为复杂。因此设计时也主要尽量按照：网络，图形化和游戏逻辑三个部分来划分类的功能。

#### 网络

程序采取了C/S架构来组织网络通信，由Client发出操作请求，然后Server进行处理，处理完成后将结果广播给所有Client，以尽可能的使多个Client保持同步。为了同时建立多个网络连接，要使用多线程方法，以防止服务器阻塞在一个连接上。

#### 图形化

利用JavaFX框架实现图形化是比较方便的。在表示所有游戏角色的基类中，加入根据其状态改变界面上图片位置的代码，然后让具体的游戏角色继承该类即可。但JavaFX的UI刷新不可过快，否则会出现异常，因此最好要根据游戏逻辑统一地更改界面，以减少不同步的现象。

#### 游戏逻辑

游戏的操作是使用方向键移动角色，为此要加入键盘监听事件，在监听到键盘按键时相应地改变角色的位置。由于游戏中有多名角色，为了提高处理效率，将每个角色都实现为了一个单独的线程。游戏中的角色还会自动地发射子弹，这些子弹也各自实现为一个线程，因此线程的同步是游戏实现的重中之重。

### 具体实现

#### 角色模块

`Creatrue`类是所有游戏角色的子类（抽象类）。为了能够实现多线程，它实现了`Runnable`接口。

```java
public abstract class Creature implements Runnable {
    ...
}
```

在`Creature`类中保存有角色的名字，位置，状态等关键信息。

为了将`Creature`显示在界面上，主要使用了

```java
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
```

这两个类。

在`Creature`对象中，保存有其对应角色的`Image`和`ImageView`。在每次更新角色位置时，相应地将`ImageView`的位置也作相应的改变即可。

```java
Platform.runLater(() -> {
            imageView.setX(x);
            imageView.setY(y);
});
```

在非Javafx模块中改变UI显示，需要将其放在`Platform.runlater()`中，以避免UI变换过快导致的异常。

实现了Creature类后，具体的角色都从它派生出来，实现了代码复用。

#### 网络模块

分别用`Server`和`Client`两个类来实现服务器和客户端。

`Server`类中保存了

```java
private ArrayList<Socket> clientSocket = new ArrayList<>();
private ServerSocket serverSocket;
```

套接字。其中`clientSocket`实现为数组是为了与多个用户建立连接。

服务器收到消息后，其唯一的工作是将消息广播给所有用户，也就是说，解析消息的工作交给了各个客户端来完成，以此来减小服务器的压力。

`Client`类在收到消息后会转交给负责游戏逻辑的`Map`类进行处理，各司其职。

```java
public void listen() throws IOException {
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
        Map.response(inputLine);
    }
}
```

#### 游戏逻辑

游戏逻辑主要通过`Map`类实现。它主要有3个功能。

+ 初始化参数。`Map`中的`init()`方法决定了游戏中角色的数量与位置。同时`Map`中就静态变量`Xmax`,`Ymax`也决定对战方格的数量。
+ 监听键盘事件。`Map`类中添加的键盘监听器。在键盘按下相应按键会调用相应的函数进行处理。同时还加入了一个线程周期性的检查所操控的角色是否死亡，若死亡则自动更换，以提高用户体验。
+ 处理`Client`收到的消息。对于`Server`发给`Client`的消息，它会转交给`Map`进行处理，`Map`进行相应的字符串处理程序后，调用角色自身的方法来进行回应。

因此整个游戏启动的流程可以概括为以下步骤：

启动`Server` -> 启动`Client` -> `Client`与`Server`建立连接 -> 游戏初始化 -> 游戏开始 -> 用户进行操作 -> 监听到键盘事件 -> `Client`向`Server`发送消息 -> `Server`将消息广播给所有用户 ->`Client`将消息交给`Map`进行处理 -> `Map`进行处理，实现了用户的操作 ->重复，直到游戏结束。

### 感想

本次作业比较复杂，做完了之后有一些收获与反思，记录于此。

1. 一开始时程序是按照P2P的方式实现的，即每个用户既实现`Server`也实现`Client`，直接实现端到端通信，但实现后发现用户自己的处理结果和服务器发来的处理结果之间有明显的延迟，因此查阅资料后改成了C/S架构，个人感觉这种实现更好一些。
2. 思路分析很重要，应该先分析再写代码。一开始时我直接将网络的部分内嵌在了游戏逻辑中，虽然能实现，但显得较为混乱，而且更改麻烦。于是我借着更改网络架构的机会将网络从游戏逻辑中分离了出来。现在感觉程序更加清爽了。略微有些不满的是游戏逻辑和图形化没有完全分开，不然抽象层次会更好。
3. 多线程是好东西，但也不能滥用。。。盲目的增加线程数完全不能充分并发，只能使得结果诡异而不可预测。只有有充分的理由才应该去使用并发。

### 运行方法

执行`mvn clean test package`打包。在target文件下运行`java -jar .\finalhw-1.0.jar`。（pom.xml中已配置主类）

#### 对战测试

先输入1打开服务器，再输入2打开一个客户端。

![image](https://github.com/181860153/java20-homework/blob/master/X-FinalProject/%E6%9C%B1%E4%BF%8A%E4%BF%8A-181860153/img/pic1.png)

再打开一个主程序，输入2打开另一个客户端。

![image](https://github.com/181860153/java20-homework/blob/master/X-FinalProject/%E6%9C%B1%E4%BF%8A%E4%BF%8A-181860153/img/pic2.PNG)

然后在客户端界面按空格即可开始游戏。（wasd/方向键控制移动）

#### 单人测试

先输入1打开服务器，再输入2打开一个客户端。

![image](https://github.com/181860153/java20-homework/blob/master/X-FinalProject/%E6%9C%B1%E4%BF%8A%E4%BF%8A-181860153/img/pic3.PNG)

然后按L选择`log.txt`即可。

![image](https://github.com/181860153/java20-homework/blob/master/X-FinalProject/%E6%9C%B1%E4%BF%8A%E4%BF%8A-181860153/img/pic4.PNG)

#### 游戏截图

![image](https://github.com/181860153/java20-homework/blob/master/X-FinalProject/%E6%9C%B1%E4%BF%8A%E4%BF%8A-181860153/img/pic5.PNG)