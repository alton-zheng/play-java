### 二分查找



&nbsp;

### 快排

该算法的实现可分为以下几步：

\1. 在数组中选一个基准数（通常为数组第一个）；

\2. 将数组中小于基准数的数据移到基准数左边，大于基准数的移到右边；

\3. 对于基准数左、右两边的数组，不断重复以上两个过程，直到每个子集只有一个元素，即为全部有序

&nbsp;

### 归并排序(Merge Sort)

归并排序，是创建在归并操作上的一种有效的排序算法。算法是采用分治法（Divide and Conquer）的一个非常典型的应用，且各层分治递归可以同时进行。归并排序思路简单，速度仅次于快速排序，为稳定排序算法，一般用于对总体无序，但是各子项相对有序的数列。

基本思想

归并排序是用分治思想，分治模式在每一层递归上有三个步骤：

- **分解（Divide）**：将n个元素分成个含n/2个元素的子序列。
- **解决（Conquer）**：用合并排序法对两个子序列递归的排序。
- **合并（Combine）**：合并两个已排序的子序列已得到排序结果。

- 实现逻辑

**2.1 迭代法**

> ① 申请空间，使其大小为两个已经排序序列之和，该空间用来存放合并后的序列
> ② 设定两个指针，最初位置分别为两个已经排序序列的起始位置
> ③ 比较两个指针所指向的元素，选择相对小的元素放入到合并空间，并移动指针到下一位置
> ④ 重复步骤③直到某一指针到达序列尾
> ⑤ 将另一序列剩下的所有元素直接复制到合并序列尾

**2.2 递归法**

> ① 将序列每相邻两个数字进行归并操作，形成floor(n/2)个序列，排序后每个序列包含两个元素
> ② 将上述序列再次归并，形成floor(n/4)个序列，每个序列包含四个元素
> ③ 重复步骤②，直到所有元素排序完毕



- 动图演示

![img](https://pic4.zhimg.com/v2-a29c0dd0186d1f8cef3c5ebdedf3e5a3_b.jpg)

归并排序演示

具体的我们以一组无序数列｛14，12，15，13，11，16｝为例分解说明，如下图所示：

![img](https://pic4.zhimg.com/80/v2-2958d4f3d9dd9156f1b5dca6788fe8a7_1440w.jpg)

上图中首先把一个未排序的序列从中间分割成2部分，再把2部分分成4部分，依次分割下去，直到分割成一个一个的数据，再把这些数据两两归并到一起，使之有序，不停的归并，最后成为一个排好序的序列。

4. 复杂度分析

> 平均时间复杂度：O(nlogn)
> 最佳时间复杂度：O(n)
> 最差时间复杂度：O(nlogn)
> 空间复杂度：O(n)
> 排序方式：In-place
> 稳定性：稳定

不管元素在什么情况下都要做这些步骤，所以花销的时间是不变的，所以该算法的最优时间复杂度和最差时间复杂度及平均时间复杂度都是一样的为：O( nlogn )

归并的空间复杂度就是那个临时的数组和递归时压入栈的数据占用的空间：n + logn；所以空间复杂度为: O(n)。

归并排序算法中，归并最后到底都是相邻元素之间的比较交换，并不会发生相同元素的相对位置发生变化，故是稳定性算法。

&nbsp;

### 冒泡排序

```
package com.wedoctor.sort;


import java.util.Arrays;


public class BubbleSort {
    public static void main(String[] args) {
        int[] arr = new int[] { 2, 8, 7, 9, 4, 1, 5, 0 };
        bubbleSort(arr);
    }


    public static void bubbleSort(int[] arr) {
        //控制多少轮
        for (int i = 1; i < arr.length; i++) {
            //控制每一轮的次数
            for (int j = 0; j <= arr.length -1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp;
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        System.out.println(Arrays.toString(arr));


    }
}

```

&nbsp;

### 5.字符串反转

```
package com.wedoctor.str;


public class StrReverse {
    public  static  String getNewStr(String str){
        StringBuffer sb = new StringBuffer(str);
        String newStr = sb.reverse().toString();
        return newStr;
    }


    public static void main(String[] args) {
        System.out.println(getNewStr("thjymhr"));
    }
}

```

&nbsp;

### Btree简单讲一下

B树(B-树)是一种适合外查找的搜索树，是一种平衡的多叉树

B树的每个结点包含着结点的值和结点所处的位置

&nbsp;

### 动态规划 最大连续子序列和

```
package com.wedoctor;


import java.util.Arrays;


public class MaxSum {
    public static int findMax(int arr[]){
         if (arr.length == 1){
             return arr[0];
         }
        int mid = (arr.length) / 2;
        int[] leftArr = Arrays.copyOfRange(arr, 0, mid);
        int[] rightArr = Arrays.copyOfRange(arr, mid, arr.length);


        int lenLeft = findMax(leftArr);
        int lenRight = findMax(rightArr);
        int lenMid = maxInMid(leftArr, rightArr);


        int max = Math.max(Math.max(lenLeft,lenRight),lenMid);
        return max;
    }


    public static int maxInMid(int left[],int right[]){
        int maxLeft = 0;
        int maxRight = 0;
        int tmpLeft = 0;
        int tmpRight = 0;
        for (int i = 0;i< left.length;i++){
            tmpLeft = tmpLeft + left[left.length - 1 - i];
            maxLeft = Math.max(tmpLeft,maxLeft);
        }


        for (int i = 0;i< right.length;i++){
            tmpRight = tmpRight + right[i];
            maxRight = Math.max(tmpRight,maxRight);
        }
        return  maxRight + maxLeft;
    }


    public static void main(String[] args) {
        int arr[] = {3,-1,10};
        System.out.println(findMax(arr));
    }
}

```

&nbsp;

### 8.二叉树概念，特点及代码实现

二叉树是n(n>=0)个结点的有限集合，该集合或者为空集（称为空二叉树），或者由一个根结点和两棵互不相交的、分别称为根结点的左子树和右子树组成。

特点：

- 每个结点最多有两颗子树，所以二叉树中不存在度大于2的结点。
- 左子树和右子树是有顺序的，次序不能任意颠倒。
- 即使树中某结点只有一棵子树，也要区分它是左子树还是右子树。

实现:

```
package com.wedoctor;


public class BinaryTreeNode {
    int data;
    BinaryTreeNode left;
    BinaryTreeNode right;


    BinaryTreeNode (int x) {
        data= x;
    }


    public BinaryTreeNode(int data, BinaryTreeNode left, BinaryTreeNode right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }


    public int getData() {
        return data;
    }


    public void setData(int data) {
        this.data = data;
    }


    public BinaryTreeNode getLeft() {
        return left;
    }


    public void setLeft(BinaryTreeNode left) {
        this.left = left;
    }


    public BinaryTreeNode getRight() {
        return right;
    }


    public void setRight(BinaryTreeNode right) {
        this.right = right;
    }
}

```

### 9.链表