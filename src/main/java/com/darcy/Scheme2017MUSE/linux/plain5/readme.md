## 相比plain4
- 使用PriorityQueue(最大堆)来找最相似的两个节点。
而不是之前的一轮迭代找max.单轮的复杂度从O(m/2 * m * m * n)变成了
O(m * m * n) + 堆的调整时间。此时间开销也几乎是跟m成线性关系。
除了用堆以外,目前还没有想到更好的方法。

- 经测算，矩阵的乘法比double[]的乘法要慢一些。
将相关性评分从矩阵乘法变为double[]乘法，速度提升了
从905353ms -> 53177ms, 16倍+;