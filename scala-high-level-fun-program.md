## Scala 高级函数式编程

&nbsp;

## Algebraic data type

中文： 代数数据结构（ADT）

来源于函数式编程

函数式编程中很多种数据结构类型符合代数特性，也就是数学特性。

&nbsp;

## Sum Type 和 Product Type

Scala 类型： 

- 加法类型（`Sum Type`）
  - case
    - `Enumeration`
    - 对象的封闭继承（sealed hierarchy of object）
  - 单位
    - `0`
  - 属性
    - 交换律
    - 结合律
- 乘法类型(`Product Type`)
  - case
    - case 类
      - case class Persion(name: Name, age: Age) 
      - name 实例的个数 * age 实例的个数
    - `Unit`
      - 零元素（单个值）
    - Nothing
      - 0 个元素
  - 单位
    - `1`
  - 属性
    - 交换律
    - 分配率

&nbsp;

## Category Theory

范畴理论，大家都知道抽象在代码中的重要性，极度抽象能让相关热爱者热衷于此。在实际生产运用中，大部分开发者对它难以理解和维护，需要对范畴理论和高级抽象有很深入研究和运用才能得心应手！ Scalaz 实现范畴的主要 Scala 库。

&nbsp;

### Category 定义

- 一个包含一系类对象的类别。 

- 一组态射（morphism）, 也称为箭头（arrow）
  - 态射是函数概念中的一种推广
  - f: A -> B (Scala f: A => B)
    - A : domain （域）
    - B : codemain (值域)

- 一个称为态射组合的二元操作，其特性是， 对于 f: A -> B 与 g: B -> C ，其组合是 g.f: A -> C

&nbsp;

公理

- 每个对象 x 有且仅有一个单位态射，当域和值域相同时， $ID_x$  与单位映射的组合有以下属性： 
  - $f.ID_x$ = $ID_x.f$

- 结合律
  - $(f.g).h$ = $f.(g.h)$

&nbsp; &nbsp;

但要知道以上理论在软件开发运用中，还不是很多，下面介绍 `Functor` 和 `Monad` 
&nbsp;

### Functor 范畴

Functor 抽象了 map 操作

```scala
import scala.language.higherKinds

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B) : F[B]
}

object SeqF extends Functor[Seq] {
  override def map[A, B](seq: Seq[A])(f: A => B): Seq[B] =
    seq map f
}

object OptionF extends Functor[Option] {
  override def map[A, B](opt: Option[A])(f: A => B): Option[B] =
    opt map f
}

object FunctionF {
  def map[A, A2, B](func: A => A2)(f: A2 => B): A => B = {
    val functor = new Functor[({type r[b] = A => b})#r] {
      override def map[A3, B](func: A => A3)(f: A3 => B): A => B =
        (a: A) => f(func(a))
    }
    
    functor.map(func, f)
    
  }
}
```

&nbsp;

### Monad 范畴

monad 范畴是对 flatMap 的抽象

该名称源于古希腊的毕达哥拉斯学派哲学家锁创造的 `monas` 一词，翻译过来大致意思是 ”生成其他所有事物的神“

&nbsp;

以下是对 Monad 的定义： 

```scala
import scala.language.higherKinds

trait Monad[M[_]] {

  def flatMap[A, B](fa: M[A])(f: A => M[B]) : M[B]
  def unit[A](a: => A): M[A]
  def bind[A, B](fa: M[A])(f: A => M[B]): M[B] =
    flatMap(fa)(f)
  def >>=[A, B](fa: M[A])(f: A => M[B]): M[B] =
    flatMap(fa)(f)
  def pure[A](a: => A): M[A] = unit(a)
  def `return`[A](a: => A): M[A] = unit(a)
}

object SeqM extends Monad[Seq] {
  def flatMap[A, B](seq: Seq[A])(f: A => Seq[B]): Seq[B] = seq flatMap f
  def unit[A](a: => A): Seq[A] = Seq(a)
}

object OptM extends Monad[Option] {
  def flatMap[A, B](opt: Option[A])(f: A => Option[B]): Option[B] = opt flatMap f
  def unit[A](a: => A): Option[A] = Option(a)
}

```

&nbsp;

### Monad 的重要性

讽刺的是，在范畴理论中 Functor 比 Monad 更重要； 但在软件应用中， Functor 的重要性远远比不上 Monad

从本质上来说， Monad 之所以重要， 是因为它为我们提供了一个对某个值包装上下文信息的规范方法。

&nbsp;

## 总结

此篇文章主要介绍了两个高级函数式编程中 2 个重要的范畴理论及运用。

Scala 标准库采用 OOP 而不是用范畴的方法来添加函数， 如 map, flatMap 和 unit。 然后就像 flatMap 一样的方法，我们可以获得 ”具有 Monad 属性“的行为， 让 for 推导式更简洁。

范畴一直比较神秘， 因为复杂的数学表达式和名称使得大部分开发者很难理解它们。 但它们本质上是对我们熟悉的概念进行高度抽象，对程序的正确性、合理性、简洁性和表达力很有意义。







