# 桥接模式

亦称：Bridge

##  意图

**桥接模式**是一种结构型设计模式， 可将一个大类或一系列紧密相关的类拆分为抽象和实现两个独立的层次结构， 从而能在开发时分别使用。

![桥接设计模式](https://refactoringguru.cn/images/patterns/content/bridge/bridge.png?id=bd543d4fb32e11647767)

##  问题

*抽象？* *实现？* 听上去挺吓人？ 让我们慢慢来， 先考虑一个简单的例子。

假如你有一个几何 `形状`Shape类， 从它能扩展出两个子类：  `圆形`Circle和 `方形`Square 。 你希望对这样的类层次结构进行扩展以使其包含颜色， 所以你打算创建名为 `红色`Red和 `蓝色`Blue的形状子类。 但是， 由于你已有两个子类， 所以总共需要创建四个类才能覆盖所有组合， 例如 `蓝色圆形`Blue­Circle和 `红色方形`Red­Square 。

![桥接模式解决的问题](https://refactoringguru.cn/images/patterns/diagrams/bridge/problem-zh.png?id=b629ac68586280f371d1)

所有组合类的数量将以几何级数增长。

在层次结构中新增形状和颜色将导致代码复杂程度指数增长。 例如添加三角形状， 你需要新增两个子类， 也就是每种颜色一个； 此后新增一种新颜色需要新增三个子类， 即每种形状一个。 如此以往， 情况会越来越糟糕。

##  解决方案

问题的根本原因是我们试图在两个独立的维度——形状与颜色——上扩展形状类。 这在处理类继承时是很常见的问题。

桥接模式通过将继承改为组合的方式来解决这个问题。 具体来说， 就是抽取其中一个维度并使之成为独立的类层次， 这样就可以在初始类中引用这个新层次的对象， 从而使得一个类不必拥有所有的状态和行为。

![桥接模式的解决方案](https://refactoringguru.cn/images/patterns/diagrams/bridge/solution-zh.png?id=1e12aa93e72a643f325f)

将一个类层次转化为多个相关的类层次， 避免单个类层次的失控。

根据该方法， 我们可以将颜色相关的代码抽取到拥有 `红色`和 `蓝色`两个子类的颜色类中， 然后在 `形状`类中添加一个指向某一颜色对象的引用成员变量。 现在， 形状类可以将所有与颜色相关的工作委派给连入的颜色对象。 这样的引用就成为了 `形状`和 `颜色`之间的桥梁。 此后， 新增颜色将不再需要修改形状的类层次， 反之亦然。

#### 抽象部分和实现部分

设计模式*四人组*的著作 在桥接定义中提出了*抽象部分*和*实现部分*两个术语。 我觉得这些术语过于学术了， 反而让模式看上去比实际情况更加复杂。 在介绍过形状和颜色的简单例子后， 我们来看看四人组著作中让人望而生畏的词语的含义。

*抽象部分* （也被称为*接口*） 是一些实体的高阶控制层。 该层自身不完成任何具体的工作， 它需要将工作委派给*实现部分*层 （也被称为*平台*）。

注意， 这里提到的内容与编程语言中的*接口*或*抽象类*无关。 它们并不是一回事。

在实际的程序中， 抽象部分是图形用户界面 （GUI）， 而实现部分则是底层操作系统代码 （API）， GUI 层调用 API 层来对用户的各种操作做出响应。

一般来说， 你可以在两个独立方向上扩展这种应用：

- 开发多个不同的 GUI （例如面向普通用户和管理员进行分别配置）
- 支持多个不同的 API （例如， 能够在 Windows、 Linux 和 macOS 上运行该程序）。

在最糟糕的情况下， 程序可能会是一团乱麻， 其中包含数百种条件语句， 连接着代码各处不同种类的 GUI 和各种 API。

![在模块化代码中驾驭变化要容易得多](https://refactoringguru.cn/images/patterns/content/bridge/bridge-3-zh.png?id=4cd9192e85ecb4bd2524)

在庞杂的代码中， 即使是很小的改动都非常难以完成， 因为你必须要在*整体上*对代码有充分的理解。 而在较小且定义明确的模块中， 进行修改则要容易得多。

你可以将特定接口-平台的组合代码抽取到独立的类中， 以在混乱中建立一些秩序。 但是， 你很快会发现这种类的数量*很多*。 类层次将以指数形式增长， 因为每次添加一个新的 GUI 或支持一种新的 API 都需要创建更多的类。

让我们试着用桥接模式来解决这个问题。 该模式建议将类拆分为两个类层次结构：

- 抽象部分： 程序的 GUI 层。
- 实现部分： 操作系统的 API。

![跨平台结构](https://refactoringguru.cn/images/patterns/content/bridge/bridge-2-zh.png?id=d5aca46e6cc7bcc88c1e)

创建跨平台应用程序的一种方法

抽象对象控制程序的外观， 并将真实工作委派给连入的实现对象。 不同的实现只要遵循相同的接口就可以互换， 使同一 GUI 可在 Windows 和 Linux 下运行。

最后的结果是： 你无需改动与 API 相关的类就可以修改 GUI 类。 此外如果想支持一个新的操作系统， 只需在实现部分层次中创建一个子类即可。

##  桥接模式结构

![桥接设计模式](https://refactoringguru.cn/images/patterns/diagrams/bridge/structure-zh.png?id=8f6df21bea5074e798d6)

1. **抽象部分** （Abstraction） 提供高层控制逻辑， 依赖于完成底层实际工作的实现对象。

2. **实现部分** （Implementation） 为所有具体实现声明通用接口。 抽象部分仅能通过在这里声明的方法与实现对象交互。

   抽象部分可以列出和实现部分一样的方法， 但是抽象部分通常声明一些复杂行为， 这些行为依赖于多种由实现部分声明的原语操作。

3. **具体实现** （Concrete Implementations） 中包括特定于平台的代码。

4. **精确抽象** （Refined Abstraction） 提供控制逻辑的变体。 与其父类一样， 它们通过通用实现接口与不同的实现进行交互。

5. 通常情况下， **客户端** （Client） 仅关心如何与抽象部分合作。 但是， 客户端需要将抽象对象与一个实现对象连接起来。

##  伪代码

示例演示了**桥接**模式如何拆分程序中同时管理设备及其遥控器的庞杂代码。  `设备`Device类作为实现部分， 而 `遥控器`Remote类则作为抽象部分。

![桥接模式示例的结构](https://refactoringguru.cn/images/patterns/diagrams/bridge/example-zh.png?id=57c1135dacb5e0c69dbf)

最初类层次结构被拆分为两个部分： 设备和遥控器。

遥控器基类声明了一个指向设备对象的引用成员变量。 所有遥控器通过通用设备接口与设备进行交互， 使得同一个遥控器可以支持不同类型的设备。

你可以开发独立于设备类的遥控器类， 只需新建一个遥控器子类即可。 例如， 基础遥控器可能只有两个按钮， 但你可在其基础上扩展新功能， 比如额外的一节电池或一块触摸屏。

客户端代码通过遥控器构造函数将特定种类的遥控器与设备对象连接起来。

```
// “抽象部分”定义了两个类层次结构中“控制”部分的接口。它管理着一个指向“实
// 现部分”层次结构中对象的引用，并会将所有真实工作委派给该对象。
class RemoteControl is
    protected field device: Device
    constructor RemoteControl(device: Device) is
        this.device = device
    method togglePower() is
        if (device.isEnabled()) then
            device.disable()
        else
            device.enable()
    method volumeDown() is
        device.setVolume(device.getVolume() - 10)
    method volumeUp() is
        device.setVolume(device.getVolume() + 10)
    method channelDown() is
        device.setChannel(device.getChannel() - 1)
    method channelUp() is
        device.setChannel(device.getChannel() + 1)


// 你可以独立于设备类的方式从抽象层中扩展类。
class AdvancedRemoteControl extends RemoteControl is
    method mute() is
        device.setVolume(0)


// “实现部分”接口声明了在所有具体实现类中通用的方法。它不需要与抽象接口相
// 匹配。实际上，这两个接口可以完全不一样。通常实现接口只提供原语操作，而
// 抽象接口则会基于这些操作定义较高层次的操作。
interface Device is
    method isEnabled()
    method enable()
    method disable()
    method getVolume()
    method setVolume(percent)
    method getChannel()
    method setChannel(channel)


// 所有设备都遵循相同的接口。
class Tv implements Device is
    // ...

class Radio implements Device is
    // ...


// 客户端代码中的某个位置。
tv = new Tv()
remote = new RemoteControl(tv)
remote.togglePower()

radio = new Radio()
remote = new AdvancedRemoteControl(radio)
```

##  桥接模式适合应用场景

 如果你想要拆分或重组一个具有多重功能的庞杂类 （例如能与多个数据库服务器进行交互的类）， 可以使用桥接模式。

 类的代码行数越多， 弄清其运作方式就越困难， 对其进行修改所花费的时间就越长。 一个功能上的变化可能需要在整个类范围内进行修改， 而且常常会产生错误， 甚至还会有一些严重的副作用。

桥接模式可以将庞杂类拆分为几个类层次结构。 此后， 你可以修改任意一个类层次结构而不会影响到其他类层次结构。 这种方法可以简化代码的维护工作， 并将修改已有代码的风险降到最低。

 如果你希望在几个独立维度上扩展一个类， 可使用该模式。

 桥接建议将每个维度抽取为独立的类层次。 初始类将相关工作委派给属于对应类层次的对象， 无需自己完成所有工作。

 如果你需要在运行时切换不同实现方法， 可使用桥接模式。

 当然并不是说一定要实现这一点， 桥接模式可替换抽象部分中的实现对象， 具体操作就和给成员变量赋新值一样简单。

顺便提一句， 最后一点是很多人混淆桥接模式和[策略](https://refactoringguru.cn/design-patterns/strategy)模式的主要原因。 记住， 设计模式并不仅是一种对类进行组织的方式， 它还能用于沟通意图和解决问题。

##  实现方式

1. 明确类中独立的维度。 独立的概念可能是： 抽象/平台， 域/基础设施， 前端/后端或接口/实现。
2. 了解客户端的业务需求， 并在抽象基类中定义它们。
3. 确定在所有平台上都可执行的业务。 并在通用实现接口中声明抽象部分所需的业务。
4. 为你域内的所有平台创建实现类， 但需确保它们遵循实现部分的接口。
5. 在抽象类中添加指向实现类型的引用成员变量。 抽象部分会将大部分工作委派给该成员变量所指向的实现对象。
6. 如果你的高层逻辑有多个变体， 则可通过扩展抽象基类为每个变体创建一个精确抽象。
7. 客户端代码必须将实现对象传递给抽象部分的构造函数才能使其能够相互关联。 此后， 客户端只需与抽象对象进行交互， 无需和实现对象打交道。

##  桥接模式优缺点

-  你可以创建与平台无关的类和程序。
-  客户端代码仅与高层抽象部分进行互动， 不会接触到平台的详细信息。
-  *开闭原则*。 你可以新增抽象部分和实现部分， 且它们之间不会相互影响。
-  *单一职责原则*。 抽象部分专注于处理高层逻辑， 实现部分处理平台细节。

-  对高内聚的类使用该模式可能会让代码更加复杂。

##  与其他模式的关系

- [桥接模式](https://refactoringguru.cn/design-patterns/bridge)通常会于开发前期进行设计， 使你能够将程序的各个部分独立开来以便开发。 另一方面， [适配器模式](https://refactoringguru.cn/design-patterns/adapter)通常在已有程序中使用， 让相互不兼容的类能很好地合作。
- [桥接](https://refactoringguru.cn/design-patterns/bridge)、 [状态模式](https://refactoringguru.cn/design-patterns/state)和[策略模式](https://refactoringguru.cn/design-patterns/strategy) （在某种程度上包括[适配器](https://refactoringguru.cn/design-patterns/adapter)） 模式的接口非常相似。 实际上， 它们都基于[组合模式](https://refactoringguru.cn/design-patterns/composite)——即将工作委派给其他对象， 不过也各自解决了不同的问题。 模式并不只是以特定方式组织代码的配方， 你还可以使用它们来和其他开发者讨论模式所解决的问题。
- 你可以将[抽象工厂模式](https://refactoringguru.cn/design-patterns/abstract-factory)和[桥接](https://refactoringguru.cn/design-patterns/bridge)搭配使用。 如果由*桥接*定义的抽象只能与特定实现合作， 这一模式搭配就非常有用。 在这种情况下， *抽象工厂*可以对这些关系进行封装， 并且对客户端代码隐藏其复杂性。
- 你可以结合使用[生成器模式](https://refactoringguru.cn/design-patterns/builder)和[桥接模式](https://refactoringguru.cn/design-patterns/bridge)： *主管*类负责抽象工作， 各种不同的*生成器*负责*实现*工作。



# **Bridge** in Java

**Bridge** is a structural design pattern that divides business logic or huge class into separate class hierarchies that can be developed independently.

One of these hierarchies (often called the Abstraction) will get a reference to an object of the second hierarchy (Implementation). The abstraction will be able to delegate some (sometimes, most) of its calls to the implementations object. Since all implementations will have a common interface, they’d be interchangeable inside the abstraction.

[ Learn more about Bridge ](https://refactoring.guru/design-patterns/bridge)

## Usage of the pattern in Java

**Complexity:** 

**Popularity:** 

**Usage examples:** The Bridge pattern is especially useful when dealing with cross-platform apps, supporting multiple types of database servers or working with several API providers of a certain kind (for example, cloud platforms, social networks, etc.)

**Identification:** Bridge can be recognized by a clear distinction between some controlling entity and several different platforms that it relies on.



## Bridge between devices and remote controls

This example shows separation between the classes of remotes and devices that they control.

Remotes act as abstractions, and devices are their implementations. Thanks to the common interfaces, the same remotes can work with different devices and vice versa.

The Bridge pattern allows changing or even creating new classes without touching the code of the opposite hierarchy.

##  **devices**

####  **devices/Device.java:** Common interface of all devices

```
package refactoring_guru.bridge.example.devices;

public interface Device {
    boolean isEnabled();

    void enable();

    void disable();

    int getVolume();

    void setVolume(int percent);

    int getChannel();

    void setChannel(int channel);

    void printStatus();
}
```

####  **devices/Radio.java:** Radio

```
package refactoring_guru.bridge.example.devices;

public class Radio implements Device {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;

    @Override
    public boolean isEnabled() {
        return on;
    }

    @Override
    public void enable() {
        on = true;
    }

    @Override
    public void disable() {
        on = false;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public void setVolume(int volume) {
        if (volume > 100) {
            this.volume = 100;
        } else if (volume < 0) {
            this.volume = 0;
        } else {
            this.volume = volume;
        }
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public void setChannel(int channel) {
        this.channel = channel;
    }

    @Override
    public void printStatus() {
        System.out.println("------------------------------------");
        System.out.println("| I'm radio.");
        System.out.println("| I'm " + (on ? "enabled" : "disabled"));
        System.out.println("| Current volume is " + volume + "%");
        System.out.println("| Current channel is " + channel);
        System.out.println("------------------------------------\n");
    }
}
```

####  **devices/Tv.java:** TV

```java
package refactoring_guru.bridge.example.devices;

public class Tv implements Device {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;

    @Override
    public boolean isEnabled() {
        return on;
    }

    @Override
    public void enable() {
        on = true;
    }

    @Override
    public void disable() {
        on = false;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public void setVolume(int volume) {
        if (volume > 100) {
            this.volume = 100;
        } else if (volume < 0) {
            this.volume = 0;
        } else {
            this.volume = volume;
        }
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public void setChannel(int channel) {
        this.channel = channel;
    }

    @Override
    public void printStatus() {
        System.out.println("------------------------------------");
        System.out.println("| I'm TV set.");
        System.out.println("| I'm " + (on ? "enabled" : "disabled"));
        System.out.println("| Current volume is " + volume + "%");
        System.out.println("| Current channel is " + channel);
        System.out.println("------------------------------------\n");
    }
}
```

##  **remotes**

####  **remotes/Remote.java:** Common interface for all remotes

```java
package refactoring_guru.bridge.example.remotes;

public interface Remote {
    void power();

    void volumeDown();

    void volumeUp();

    void channelDown();

    void channelUp();
}
```

####  **remotes/BasicRemote.java:** Basic remote control

```java
package refactoring_guru.bridge.example.remotes;

import refactoring_guru.bridge.example.devices.Device;

public class BasicRemote implements Remote {
    protected Device device;

    public BasicRemote() {}

    public BasicRemote(Device device) {
        this.device = device;
    }

    @Override
    public void power() {
        System.out.println("Remote: power toggle");
        if (device.isEnabled()) {
            device.disable();
        } else {
            device.enable();
        }
    }

    @Override
    public void volumeDown() {
        System.out.println("Remote: volume down");
        device.setVolume(device.getVolume() - 10);
    }

    @Override
    public void volumeUp() {
        System.out.println("Remote: volume up");
        device.setVolume(device.getVolume() + 10);
    }

    @Override
    public void channelDown() {
        System.out.println("Remote: channel down");
        device.setChannel(device.getChannel() - 1);
    }

    @Override
    public void channelUp() {
        System.out.println("Remote: channel up");
        device.setChannel(device.getChannel() + 1);
    }
}
```

####  **remotes/AdvancedRemote.java:** Advanced remote control

```java
package refactoring_guru.bridge.example.remotes;

import refactoring_guru.bridge.example.devices.Device;

public class AdvancedRemote extends BasicRemote {

    public AdvancedRemote(Device device) {
        super.device = device;
    }

    public void mute() {
        System.out.println("Remote: mute");
        device.setVolume(0);
    }
}
```

####  **Demo.java:** Client code

```java
package refactoring_guru.bridge.example;

import refactoring_guru.bridge.example.devices.Device;
import refactoring_guru.bridge.example.devices.Radio;
import refactoring_guru.bridge.example.devices.Tv;
import refactoring_guru.bridge.example.remotes.AdvancedRemote;
import refactoring_guru.bridge.example.remotes.BasicRemote;

public class Demo {
    public static void main(String[] args) {
        testDevice(new Tv());
        testDevice(new Radio());
    }

    public static void testDevice(Device device) {
        System.out.println("Tests with basic remote.");
        BasicRemote basicRemote = new BasicRemote(device);
        basicRemote.power();
        device.printStatus();

        System.out.println("Tests with advanced remote.");
        AdvancedRemote advancedRemote = new AdvancedRemote(device);
        advancedRemote.power();
        advancedRemote.mute();
        device.printStatus();
    }
}
```

####  **OutputDemo.txt:** Execution result

```java
Tests with basic remote.
Remote: power toggle
------------------------------------
| I'm TV set.
| I'm enabled
| Current volume is 30%
| Current channel is 1
------------------------------------

Tests with advanced remote.
Remote: power toggle
Remote: mute
------------------------------------
| I'm TV set.
| I'm disabled
| Current volume is 0%
| Current channel is 1
------------------------------------

Tests with basic remote.
Remote: power toggle
------------------------------------
| I'm radio.
| I'm enabled
| Current volume is 30%
| Current channel is 1
------------------------------------

Tests with advanced remote.
Remote: power toggle
Remote: mute
------------------------------------
| I'm radio.
| I'm disabled
| Current volume is 0%
| Current channel is 1
------------------------------------
```