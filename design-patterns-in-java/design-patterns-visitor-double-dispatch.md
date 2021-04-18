# 访问者和双分派

让我们看看下面几何图形类的层次结构 （注意伪代码）：

```
interface Graphic is
    method draw()

class Shape implements Graphic is
    field id
    method draw()
    // ...

class Dot extends Shape is
    field x, y
    method draw()
    // ...

class Circle extends Dot is
    field radius
    method draw()
    // ...

class Rectangle extends Shape is
    field width, height
    method draw()
    // ...

class CompoundGraphic implements Graphic is
    field children: array of Graphic
    method draw()
    // ...
```

这些代码运行正常且程序处于开发阶段。 但某天你决定开发导出功能。 如果将导出功能的代码放入这些类中， 它们看上去会很奇怪。 因此， 你决定不在层次结构里的类中添加导出功能， 而是在层次结构外创建一个包含所有导出逻辑的新类。 该类将包含将每个对象的公有状态导出为 XML 字符串的方法。

```
class Exporter is
    method export(s: Shape) is
        print("导出形状")
    method export(d: Dot)
        print("导出点")
    method export(c: Circle)
        print("导出圆形")
    method export(r: Rectangle)
        print("导出矩形")
    method export(cs: CompoundGraphic)
        print("导出组合图形")
```

这些代码看上去不错， 让我们运行试试：

```
class App() is
    method export(shape: Shape) is
        Exporter exporter = new Exporter()
        exporter.export(shape);

app.export(new Circle());
// 不幸的是，这里将输出“导出形状”。
```

等等！ 为什么？！

## 像编译器一样思考

注意： 下面的内容对于绝大多数面向对象编程的现代语言 （Java、 C# 和 PHP 等） 来说都是成立的。

### 后期/动态绑定

假设你是一个编译器。 你必须决定如何编译下面的代码：

```
method drawShape(shape: Shape) is
    shape.draw();
```

让我们看看... `Shape`形状类中定义了　 `draw`绘制方法。 稍等， 还有四个子类重写了该方法。 我们能否有把握地决定调用哪个实现呢？ 看上去不太可能。 确认的唯一方式是启动程序并检查传递给该方法的对象所属的类。 我们只知道一件事情： 该对象**将包含** `draw`方法的实现。

因此， 最终的机器代码将检查 `s`参数的类并且从合适的类中选择 `draw`方法的实现。

这种动态类型检查被称为后期 （或动态） 绑定：

- **后期**， 是因为我们在编译后和运行时才将对象及其实现链接起来。
- **动态**， 是因为每个新对象都可能需要链接到不同的实现。

### 前期/静态绑定

现在， 让我们来 “编译” 以下代码：

```
method exportShape(shape: Shape) is
    Exporter exporter = new Exporter()
    exporter.export(shape);
```

第二行代码很清楚：  `Exporter`类没有构造方法， 因此我们仅能将对象初始化。 那么对 `export`导出方法的调用呢？  `Exporter`有五个同名但参数不同的方法。 调用哪一个呢？ 看来我们在这里也需要动态绑定。

但还有另一个问题。 如果 `导出器`类中有一个图形类没有相应的 `export`方法怎么办？ 例如， 一个 `Ellipse`椭圆对象。 编译器不能确保存在适当的与重写后的方法相对应的重载方法。 编译器无法应对这种模凌两可的情况。

因此， 编译器开发者会选择安全的方式： 使用前期 （或静态） 绑定来处理重载方法。

- **前期**， 是因为它发生在运行程序前编译的时候。 --**静态**， 是因为它无法在运行时更改。

让我们回到之前的示例。 我们可以确定传递过来的参数类型属于 `Shape`类层次结构中： 要么是 `Shape`类， 要么是它的子类。 我们还知道 `Exporter`类包含支持 `Shape`类的导出功能基础实现：  `export(s: Shape)` 。

这是唯一能够安全链接当前代码而不会造成模凌两可情形的实现。 因此尽管我们将 `Rectangle`对象传递给了 `export­Shape` ， 导出类仍将调用 `export(s: Shape)`方法。

## 双分派

**双分派**是一个允许在重载时使用动态绑定的技巧。 下面是其实现方式：

```
class Visitor is
    method visit(s: Shape) is
        print("访问形状")
    method visit(d: Dot)
        print("访问点")

interface Graphic is
    method accept(v: Visitor)

class Shape implements Graphic is
    method accept(v: Visitor)
        // 编译器明确知道 `this` 的类型是 `Shape`。
        // 因此可以安全地调用 `visit(s: Shape)`。
        v.visit(this)

class Dot extends Shape is
    method accept(v: Visitor)
        // 编译器明确知道 `this` 的类型是 `Dot`。
        // 因此可以安全地调用 `visit(s: Dot)`。
        v.visit(this)


Visitor v = new Visitor();
Graphic g = new Dot();

// `accept` 方法是重写而不是重载的。编译器可以进行动态绑定。
// 因此在对象调用某个方法时，将执行其所属类中的 `accept`
// 方法（在本例中是 `Dot` 类）。
g.accept(v);

// 输出："访问点"
```

### 后记

尽管[访问者](https://refactoringguru.cn/design-patterns/visitor)模式基于双分派的原则创建， 但这并不是其主要目的。 访问者的目的是让你能为整个类层次结构添加 “外部” 操作， 而无需修改这些类的已有代码。