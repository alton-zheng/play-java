# 中介者模式

亦称：调解人、控制器、Intermediary、Controller、Mediator

&nbsp;

##  意图

**中介者模式**是一种行为设计模式， 能让你减少对象之间混乱无序的依赖关系。 该模式会限制对象之间的直接交互， 迫使它们通过一个中介者对象进行合作。

![中介者设计模式](images/mediator.png)

&nbsp;

##  问题

假如你有一个创建和修改客户资料的对话框， 它由各种控件组成， 例如文本框 （Text­Field）、 复选框 （Checkbox） 和按钮 （Button） 等。

![用户界面中各元素间的混乱关系](images/mediator-problem1-zh.png)

用户界面中各元素间的关系会随程序发展而变得混乱。

某些表单元素可能会直接进行互动。 例如， 选中 “我有一只狗” 复选框后可能会显示一个隐藏文本框用于输入狗狗的名字。 另一个例子是提交按钮必须在保存数据前校验所有输入内容。

![UI 中各元素相互依赖](images/mediator-problem2.png)

元素间存在许多关联。 因此， 对某些元素进行修改可能会影响其他元素。

如果直接在表单元素代码中实现业务逻辑， 你将很难在程序其他表单中复用这些元素类。 例如， 由于复选框类与狗狗的文本框相耦合， 所以将无法在其他表单中使用它。 你要么使用渲染资料表单时用到的所有类， 要么一个都不用。

&nbsp;

##  解决方案

中介者模式建议你停止组件之间的直接交流并使其相互独立。 这些组件必须调用特殊的中介者对象， 通过中介者对象重定向调用行为， 以间接的方式进行合作。 最终， 组件仅依赖于一个中介者类， 无需与多个其他组件相耦合。

在资料编辑表单的例子中， 对话框 （Dialog） 类本身将作为中介者， 其很可能已知自己所有的子元素， 因此你甚至无需在该类中引入新的依赖关系。

![UI 元素必须通过中介者进行沟通。](images/mediator-solution1-zh.png)

UI 元素必须通过中介者对象进行间接沟通。

绝大部分重要的修改都在实际表单元素中进行。 让我们想想提交按钮。 之前， 当用户点击按钮后， 它必须对所有表单元素数值进行校验。 而现在它的唯一工作是将点击事件通知给对话框。 收到通知后， 对话框可以自行校验数值或将任务委派给各元素。 这样一来， 按钮不再与多个表单元素相关联， 而仅依赖于对话框类。

你还可以为所有类型的对话框抽取通用接口， 进一步削弱其依赖性。 接口中将声明一个所有表单元素都能使用的通知方法， 可用于将元素中发生的事件通知给对话框。 这样一来， 所有实现了该接口的对话框都能使用这个提交按钮了。

采用这种方式， 中介者模式让你能在单个中介者对象中封装多个对象间的复杂关系网。 类所拥有的依赖关系越少， 就越易于修改、 扩展或复用。

&nbsp;

##  真实世界类比

![空中交通管制塔台](images/mediator-live-example.png)

飞行器驾驶员之间不会通过相互沟通来决定下一架降落的飞机。 所有沟通都通过控制塔台进行。

飞行器驾驶员们在靠近或离开空中管制区域时不会直接相互交流。 但他们会与飞机跑道附近， 塔台中的空管员通话。 如果没有空管员， 驾驶员就需要留意机场附近的所有飞机， 并与数十位飞行员组成的委员会讨论降落顺序。 那恐怕会让飞机坠毁的统计数据一飞冲天吧。

塔台无需管制飞行全程， 只需在航站区加强管控即可， 因为该区域的决策参与者数量对于飞行员来说实在太多了。

&nbsp;

##  中介者模式结构

![中介者设计模式的结构](images/mediator-structure.png)

1. **组件** （Component） 是各种包含业务逻辑的类。 每个组件都有一个指向中介者的引用， 该引用被声明为中介者接口类型。 组件不知道中介者实际所属的类， 因此你可通过将其连接到不同的中介者以使其能在其他程序中复用。

2. **中介者** （Mediator） 接口声明了与组件交流的方法， 但通常仅包括一个通知方法。 组件可将任意上下文 （包括自己的对象） 作为该方法的参数， 只有这样接收组件和发送者类之间才不会耦合。

3. **具体中介者** （Concrete Mediator） 封装了多种组件间的关系。 具体中介者通常会保存所有组件的引用并对其进行管理， 甚至有时会对其生命周期进行管理。

4. 组件并不知道其他组件的情况。 如果组件内发生了重要事件， 它只能通知中介者。 中介者收到通知后能轻易地确定发送者， 这或许已足以判断接下来需要触发的组件了。

   对于组件来说， 中介者看上去完全就是一个黑箱。 发送者不知道最终会由谁来处理自己的请求， 接收者也不知道最初是谁发出了请求。

&nbsp;

##  伪代码

在本例中， **中介者**模式可帮助你减少各种 UI 类 （按钮、 复选框和文本标签） 之间的相互依赖关系。

![中介者模式示例的结构](images/mediator-example.png)

UI 对话框类的结构

用户触发的元素不会直接与其他元素交流， 即使看上去它们应该这样做。 相反， 元素只需让中介者知晓事件即可， 并能在发出通知时同时传递任何上下文信息。

本例中的中介者是整个认证对话框。 对话框知道具体元素应如何进行合作并促进它们的间接交流。 当接收到事件通知后， 对话框会确定负责处理事件的元素并据此重定向请求。

```c
// 中介者接口声明了一个能让组件将各种事件通知给中介者的方法。中介者可对这
// 些事件做出响应并将执行工作传递给其他组件。
interface Mediator is
    method notify(sender: Component, event: string)


// 具体中介者类可解开各组件之间相互交叉的连接关系并将其转移到中介者中。
class AuthenticationDialog implements Mediator is
    private field title: string
    private field loginOrRegisterChkBx: Checkbox
    private field loginUsername, loginPassword: Textbox
    private field registrationUsername, registrationPassword,
                  registrationEmail: Textbox
    private field okBtn, cancelBtn: Button

    constructor AuthenticationDialog() is
        // 创建所有组件对象并将当前中介者传递给其构造函数以建立连接。

    // 当组件中有事件发生时，它会通知中介者。中介者接收到通知后可自行处理，
    // 也可将请求传递给另一个组件。
    method notify(sender, event) is
        if (sender == loginOrRegisterChkBx and event == "check")
            if (loginOrRegisterChkBx.checked)
                title = "登录"
                // 1. 显示登录表单组件。
                // 2. 隐藏注册表单组件。
            else
                title = "注册"
                // 1. 显示注册表单组件。
                // 2. 隐藏登录表单组件。

        if (sender == okBtn && event == "click")
            if (loginOrRegister.checked)
                // 尝试找到使用登录信息的用户。
                if (!found)
                    // 在登录字段上方显示错误信息。
            else
                // 1. 使用注册字段中的数据创建用户账号。
                // 2. 完成用户登录工作。 …


// 组件会使用中介者接口与中介者进行交互。因此只需将它们与不同的中介者连接
// 起来，你就能在其他情境中使用这些组件了。
class Component is
    field dialog: Mediator

    constructor Component(dialog) is
        this.dialog = dialog

    method click() is
        dialog.notify(this, "click")

    method keypress() is
        dialog.notify(this, "keypress")

// 具体组件之间无法进行交流。它们只有一个交流渠道，那就是向中介者发送通知。
class Button extends Component is
    // ...

class Textbox extends Component is
    // ...

class Checkbox extends Component is
    method check() is
        dialog.notify(this, "check")
    // ...
```

&nbsp;

##  中介者模式适合应用场景

 当一些对象和其他对象紧密耦合以致难以对其进行修改时， 可使用中介者模式。

 该模式让你将对象间的所有关系抽取成为一个单独的类， 以使对于特定组件的修改工作独立于其他组件。

 当组件因过于依赖其他组件而无法在不同应用中复用时， 可使用中介者模式。

 应用中介者模式后， 每个组件不再知晓其他组件的情况。 尽管这些组件无法直接交流， 但它们仍可通过中介者对象进行间接交流。 如果你希望在不同应用中复用一个组件， 则需要为其提供一个新的中介者类。

 如果为了能在不同情景下复用一些基本行为， 导致你需要被迫创建大量组件子类时， 可使用中介者模式。

 由于所有组件间关系都被包含在中介者中， 因此你无需修改组件就能方便地新建中介者类以定义新的组件合作方式。

&nbsp;

##  实现方式

1. 找到一组当前紧密耦合， 且提供其独立性能带来更大好处的类 （例如更易于维护或更方便复用）。
2. 声明中介者接口并描述中介者和各种组件之间所需的交流接口。 在绝大多数情况下， 一个接收组件通知的方法就足够了。 如果你希望在不同情景下复用组件类， 那么该接口将非常重要。 只要组件使用通用接口与其中介者合作， 你就能将该组件与不同实现中的中介者进行连接。
3. 实现具体中介者类。 该类可从自行保存其下所有组件的引用中受益。
4. 你可以更进一步， 让中介者负责组件对象的创建和销毁。 此后， 中介者可能会与 Factory 或 Facade 类似。
5. 组件必须保存对于中介者对象的引用。 该连接通常在组件的构造函数中建立， 该函数会将中介者对象作为参数传递。
6. 修改组件代码， 使其可调用中介者的通知方法， 而非其他组件的方法。 然后将调用其他组件的代码抽取到中介者类中， 并在中介者接收到该组件通知时执行这些代码。

&nbsp;

##  中介者模式优缺点

-  √ *单一职责原则*。 你可以将多个组件间的交流抽取到同一位置， 使其更易于理解和维护。
-  √ *开闭原则*。 你无需修改实际组件就能增加新的中介者。
-  √ 你可以减轻应用中多个组件间的耦合情况。
-  √ 你可以更方便地复用各个组件。
-  一段时间后， 中介者可能会演化成为 [上帝对象](https://zh.wikipedia.org/wiki/%E4%B8%8A%E5%B8%9D%E5%AF%B9%E8%B1%A1)。

&nbsp;

##  与其他模式的关系

- Chain of Responsibility、Command、 Mediator 和 Observer 用于处理请求发送者和接收者之间的不同连接方式：

  - Chain of Responsibility 按照顺序将请求动态传递给一系列的潜在接收者， 直至其中一名接收者对请求进行处理。
  - Command 在 Sender 和 Request 之间建立单向连接。
  - Mediator 清除了 Sender 和 Request 之间的直接连接， 强制它们通过一个中介对象进行间接沟通。
  - Observer 允许 Receiver  动态地订阅或取消接收请求。

- Facade 和 Mediator 的职责类似： 它们都尝试在大量紧密耦合的类中组织起合作。

  - Facade 为子系统中的所有对象定义了一个简单接口， 但是它不提供任何新功能。 子系统本身不会意识到facade 的存在。 子系统中的对象可以直接进行交流。
  - Mediator 将系统中组件的沟通行为中心化。 各组件只知道中介者对象， 无法直接相互交流。

- Mediator 和 Observer 之间的区别往往很难记住。 在大部分情况下， 你可以使用其中一种模式， 而有时可以同时使用。 让我们来看看如何做到这一点。

  - Mediator 的主要目标是消除一系列系统组件之间的相互依赖。 这些组件将依赖于同一个中介者对象。 Observer 的目标是在对象之间建立动态的单向连接， 使得部分对象可作为其他对象的附属发挥作用。

  - 有一种流行的 Mediator 实现方式依赖于 Observer。 中介者对象担当发布者的角色， 其他组件则作为订阅者。 可以订阅 Mediator 的事件或取消订阅。 当 Mediator 以这种方式实现时， 它可能看上去与 Observer 非常相似。

  - 当你感到疑惑时， 记住可以采用其他方式来实现中介者。 例如， 你可永久性地将所有组件链接到同一个中介者对象。 这种实现方式和 Observer 并不相同， 但这仍是一种 Mediator。
- 假设有一个程序， 其所有的组件都变成了发布者， 它们之间可以相互建立动态连接。 这样程序中就没有中心化的中介者对象， 而只有一些分布式的观察者。

&nbsp;

# Java **中介者**模式讲解和代码示例

**中介者**是一种行为设计模式， 让程序组件通过特殊的中介者对象进行间接沟通， 达到减少组件之间依赖关系的目的。

中介者能使得程序更易于修改和扩展， 而且能更方便地对独立的组件进行复用， 因为它们不再依赖于很多其他的类。

&nbsp;

## 在 Java 中使用模式

**使用示例：** 中介者模式在 Java 代码中最常用于帮助程序 GUI 组件之间的通信。 在 MVC 模式中， 控制器是中介者的同义词。

下面是核心 Java 程序库中该模式的一些示例：

- [`java.util.Timer`](http://docs.oracle.com/javase/8/docs/api/java/util/Timer.html) （所有 `schedule­XXX()`方法）
- [`java.util.concurrent.Executor#execute()`](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html#execute-java.lang.Runnable-)
- [`java.util.concurrent.ExecutorService`](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html) （ `invoke­XXX()`和 `submit­()`方法）
- [`java.util.concurrent.ScheduledExecutorService`](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html) （所有 `schedule­XXX()`方法）
- [`java.lang.reflect.Method#invoke()`](http://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#invoke-java.lang.Object-java.lang.Object...-)

&nbsp;

## 笔记程序

本例展示了如何将许多 GUI 元素组织起来， 使其在中介者的帮助下无需相互依赖就能合作。

&nbsp;

##  **components:** Colleague classes

####  **components/Component.java**

```java
package refactoring_guru.mediator.example.components;

import refactoring_guru.mediator.example.mediator.Mediator;

/**
 * Common component interface.
 */
public interface Component {
    void setMediator(Mediator mediator);
    String getName();
}

```

&nbsp;

####  **components/AddButton.java**

```java
package refactoring_guru.mediator.example.components;

import refactoring_guru.mediator.example.mediator.Mediator;
import refactoring_guru.mediator.example.mediator.Note;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Concrete components don't talk with each other. They have only one
 * communication channel–sending requests to the mediator.
 */
public class AddButton extends JButton implements Component {
    private Mediator mediator;

    public AddButton() {
        super("Add");
    }

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    protected void fireActionPerformed(ActionEvent actionEvent) {
        mediator.addNewNote(new Note());
    }

    @Override
    public String getName() {
        return "AddButton";
    }
}
```

&nbsp;

####  **components/DeleteButton.java**

```java
package refactoring_guru.mediator.example.components;

import refactoring_guru.mediator.example.mediator.Mediator;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Concrete components don't talk with each other. They have only one
 * communication channel–sending requests to the mediator.
 */
public class DeleteButton extends JButton  implements Component {
    private Mediator mediator;

    public DeleteButton() {
        super("Del");
    }

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    protected void fireActionPerformed(ActionEvent actionEvent) {
        mediator.deleteNote();
    }

    @Override
    public String getName() {
        return "DelButton";
    }
}
```

&nbsp;

####  **components/Filter.java**

```java
package refactoring_guru.mediator.example.components;

import refactoring_guru.mediator.example.mediator.Mediator;
import refactoring_guru.mediator.example.mediator.Note;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Concrete components don't talk with each other. They have only one
 * communication channel–sending requests to the mediator.
 */
public class Filter extends JTextField implements Component {
    private Mediator mediator;
    private ListModel listModel;

    public Filter() {}

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    protected void processComponentKeyEvent(KeyEvent keyEvent) {
        String start = getText();
        searchElements(start);
    }

    public void setList(ListModel listModel) {
        this.listModel = listModel;
    }

    private void searchElements(String s) {
        if (listModel == null) {
            return;
        }

        if (s.equals("")) {
            mediator.setElementsList(listModel);
            return;
        }

        ArrayList<Note> notes = new ArrayList<>();
        for (int i = 0; i < listModel.getSize(); i++) {
            notes.add((Note) listModel.getElementAt(i));
        }
        DefaultListModel<Note> listModel = new DefaultListModel<>();
        for (Note note : notes) {
            if (note.getName().contains(s)) {
                listModel.addElement(note);
            }
        }
        mediator.setElementsList(listModel);
    }

    @Override
    public String getName() {
        return "Filter";
    }
}
```

&nbsp;

####  **components/List.java**

```java
package refactoring_guru.mediator.example.components;

import refactoring_guru.mediator.example.mediator.Mediator;
import refactoring_guru.mediator.example.mediator.Note;

import javax.swing.*;

/**
 * Concrete components don't talk with each other. They have only one
 * communication channel–sending requests to the mediator.
 */
@SuppressWarnings("unchecked")
public class List extends JList implements Component {
    private Mediator mediator;
    private final DefaultListModel LIST_MODEL;

    public List(DefaultListModel listModel) {
        super(listModel);
        this.LIST_MODEL = listModel;
        setModel(listModel);
        this.setLayoutOrientation(JList.VERTICAL);
        Thread thread = new Thread(new Hide(this));
        thread.start();
    }

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    public void addElement(Note note) {
        LIST_MODEL.addElement(note);
        int index = LIST_MODEL.size() - 1;
        setSelectedIndex(index);
        ensureIndexIsVisible(index);
        mediator.sendToFilter(LIST_MODEL);
    }

    public void deleteElement() {
        int index = this.getSelectedIndex();
        try {
            LIST_MODEL.remove(index);
            mediator.sendToFilter(LIST_MODEL);
        } catch (ArrayIndexOutOfBoundsException ignored) {}
    }

    public Note getCurrentElement() {
        return (Note)getSelectedValue();
    }

    @Override
    public String getName() {
        return "List";
    }

    private class Hide implements Runnable {
        private List list;

        Hide(List list) {
            this.list = list;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if (list.isSelectionEmpty()) {
                    mediator.hideElements(true);
                } else {
                    mediator.hideElements(false);
                }
            }
        }
    }
}
```

&nbsp;

####  **components/SaveButton.java**

```java
package refactoring_guru.mediator.example.components;

import refactoring_guru.mediator.example.mediator.Mediator;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Concrete components don't talk with each other. They have only one
 * communication channel–sending requests to the mediator.
 */
public class SaveButton extends JButton implements Component {
    private Mediator mediator;

    public SaveButton() {
        super("Save");
    }

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    protected void fireActionPerformed(ActionEvent actionEvent) {
        mediator.saveChanges();
    }

    @Override
    public String getName() {
        return "SaveButton";
    }
}
```

&nbsp;

####  **components/TextBox.java**

```java
package refactoring_guru.mediator.example.components;

import refactoring_guru.mediator.example.mediator.Mediator;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Concrete components don't talk with each other. They have only one
 * communication channel–sending requests to the mediator.
 */
public class TextBox extends JTextArea implements Component {
    private Mediator mediator;

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    protected void processComponentKeyEvent(KeyEvent keyEvent) {
        mediator.markNote();
    }

    @Override
    public String getName() {
        return "TextBox";
    }
}
```

&nbsp;

#### **components/Title.java**

```java
package refactoring_guru.mediator.example.components;

import refactoring_guru.mediator.example.mediator.Mediator;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Concrete components don't talk with each other. They have only one
 * communication channel–sending requests to the mediator.
 */
public class Title extends JTextField implements Component {
    private Mediator mediator;

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    protected void processComponentKeyEvent(KeyEvent keyEvent) {
        mediator.markNote();
    }

    @Override
    public String getName() {
        return "Title";
    }
}
```

&nbsp;

##  **mediator**

####  **mediator/Mediator.java:** 定义通用的中介者接口

```java
package refactoring_guru.mediator.example.mediator;

import refactoring_guru.mediator.example.components.Component;

import javax.swing.*;

/**
 * Common mediator interface.
 */
public interface Mediator {
    void addNewNote(Note note);
    void deleteNote();
    void getInfoFromList(Note note);
    void saveChanges();
    void markNote();
    void clear();
    void sendToFilter(ListModel listModel);
    void setElementsList(ListModel list);
    void registerComponent(Component component);
    void hideElements(boolean flag);
    void createGUI();
}
```

&nbsp;

####  **mediator/Editor.java:** 具体中介者

```java
package refactoring_guru.mediator.example.mediator;

import refactoring_guru.mediator.example.components.*;
import refactoring_guru.mediator.example.components.Component;
import refactoring_guru.mediator.example.components.List;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Concrete mediator. All chaotic communications between concrete components
 * have been extracted to the mediator. Now components only talk with the
 * mediator, which knows who has to handle a request.
 */
public class Editor implements Mediator {
    private Title title;
    private TextBox textBox;
    private AddButton add;
    private DeleteButton del;
    private SaveButton save;
    private List list;
    private Filter filter;

    private JLabel titleLabel = new JLabel("Title:");
    private JLabel textLabel = new JLabel("Text:");
    private JLabel label = new JLabel("Add or select existing note to proceed...");
  
    /**
     * Here the registration of components by the mediator.
     */
    @Override
    public void registerComponent(Component component) {
        component.setMediator(this);
        switch (component.getName()) {
            case "AddButton":
                add = (AddButton)component;
                break;
            case "DelButton":
                del = (DeleteButton)component;
                break;
            case "Filter":
                filter = (Filter)component;
                break;
            case "List":
                list = (List)component;
                this.list.addListSelectionListener(listSelectionEvent -> {
                    Note note = (Note)list.getSelectedValue();
                    if (note != null) {
                        getInfoFromList(note);
                    } else {
                        clear();
                    }
                });
                break;
            case "SaveButton":
                save = (SaveButton)component;
                break;
            case "TextBox":
                textBox = (TextBox)component;
                break;
            case "Title":
                title = (Title)component;
                break;
        }
    }

    /**
     * Various methods to handle requests from particular components.
     */
    @Override
    public void addNewNote(Note note) {
        title.setText("");
        textBox.setText("");
        list.addElement(note);
    }

    @Override
    public void deleteNote() {
        list.deleteElement();
    }

    @Override
    public void getInfoFromList(Note note) {
        title.setText(note.getName().replace('*', ' '));
        textBox.setText(note.getText());
    }

    @Override
    public void saveChanges() {
        try {
            Note note = (Note) list.getSelectedValue();
            note.setName(title.getText());
            note.setText(textBox.getText());
            list.repaint();
        } catch (NullPointerException ignored) {}
    }

    @Override
    public void markNote() {
        try {
            Note note = list.getCurrentElement();
            String name = note.getName();
            if (!name.endsWith("*")) {
                note.setName(note.getName() + "*");
            }
            list.repaint();
        } catch (NullPointerException ignored) {}
    }

    @Override
    public void clear() {
        title.setText("");
        textBox.setText("");
    }

    @Override
    public void sendToFilter(ListModel listModel) {
        filter.setList(listModel);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setElementsList(ListModel list) {
        this.list.setModel(list);
        this.list.repaint();
    }

    @Override
    public void hideElements(boolean flag) {
        titleLabel.setVisible(!flag);
        textLabel.setVisible(!flag);
        title.setVisible(!flag);
        textBox.setVisible(!flag);
        save.setVisible(!flag);
        label.setVisible(flag);
    }

    @Override
    public void createGUI() {
        JFrame notes = new JFrame("Notes");
        notes.setSize(960, 600);
        notes.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel left = new JPanel();
        left.setBorder(new LineBorder(Color.BLACK));
        left.setSize(320, 600);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter:"));
        filter.setColumns(20);
        filterPanel.add(filter);
        filterPanel.setPreferredSize(new Dimension(280, 40));
        JPanel listPanel = new JPanel();
        list.setFixedCellWidth(260);
        listPanel.setSize(320, 470);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(275, 410));
        listPanel.add(scrollPane);
        JPanel buttonPanel = new JPanel();
        add.setPreferredSize(new Dimension(85, 25));
        buttonPanel.add(add);
        del.setPreferredSize(new Dimension(85, 25));
        buttonPanel.add(del);
        buttonPanel.setLayout(new FlowLayout());
        left.add(filterPanel);
        left.add(listPanel);
        left.add(buttonPanel);
        JPanel right = new JPanel();
        right.setLayout(null);
        right.setSize(640, 600);
        right.setLocation(320, 0);
        right.setBorder(new LineBorder(Color.BLACK));
        titleLabel.setBounds(20, 4, 50, 20);
        title.setBounds(60, 5, 555, 20);
        textLabel.setBounds(20, 4, 50, 130);
        textBox.setBorder(new LineBorder(Color.DARK_GRAY));
        textBox.setBounds(20, 80, 595, 410);
        save.setBounds(270, 535, 80, 25);
        label.setFont(new Font("Verdana", Font.PLAIN, 22));
        label.setBounds(100, 240, 500, 100);
        right.add(label);
        right.add(titleLabel);
        right.add(title);
        right.add(textLabel);
        right.add(textBox);
        right.add(save);
        notes.setLayout(null);
        notes.getContentPane().add(left);
        notes.getContentPane().add(right);
        notes.setResizable(false);
        notes.setLocationRelativeTo(null);
        notes.setVisible(true);
    }
}
```

&nbsp;

#### **mediator/Note.java:** 笔记类

```java
package refactoring_guru.mediator.example.mediator;

/**
 * Note class.
 */
public class Note {
    private String name;
    private String text;

    public Note() {
        name = "New note";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return name;
    }
}
```

&nbsp;

#### **Demo.java:** 初始化代码

```java
package refactoring_guru.mediator.example;

import refactoring_guru.mediator.example.components.*;
import refactoring_guru.mediator.example.mediator.Editor;
import refactoring_guru.mediator.example.mediator.Mediator;

import javax.swing.*;

/**
 * Demo class. Everything comes together here.
 */
public class Demo {
    public static void main(String[] args) {
        Mediator mediator = new Editor();

        mediator.registerComponent(new Title());
        mediator.registerComponent(new TextBox());
        mediator.registerComponent(new AddButton());
        mediator.registerComponent(new DeleteButton());
        mediator.registerComponent(new SaveButton());
        mediator.registerComponent(new List(new DefaultListModel()));
        mediator.registerComponent(new Filter());

        mediator.createGUI();
    }
}
```

&nbsp;

####  **OutputDemo.png:** 执行结果

![img](images/mediator-outputdemo.png)

&nbsp;