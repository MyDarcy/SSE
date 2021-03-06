本方案实现了FMS_II表示的逻辑操作方式，
但是只是拓展了一个维度。而没有说引入多个冗余关键词
来实现某种程度的混淆。

实现注意点。
1. 文档向量都是表示对关键词的包含关系，即0，1值，而不是之前的
方案都是采用TF-IDF来作为权重。文档向量的最后一个维度是默认设置为1的。
2. 查询陷门的构造非常巧妙, 主要是构造三个递增关系序列。
3. 问题就在于这个构造方式很难和个性化方案结合起来。因为
    - 个性化: 文档向量是TF-IDF评分; 查询向量是用户兴趣模型中提取的关键词偏好（其实就是一个超递增序列）。
    - 逻辑  : 文档向量是0,1向量，查询向量是构造的三个序列。
4. 如果要结合起来的话, 感觉很难结合呀，一个本质是评分机制，一个则是包含关系。

fix-20180109
1. 解析or联结的关键词出错了导致运行的结果不对。
2. 叶子结点相关性太小(0.01)会直接剔除掉。
   - 在lv中，因为weight置1，而查询向量也是值0,1的或者构造的>1的递增序列。
   - 在pv中，weight是tf-idf值，所以需要计算出所有关键词的最小值，
   然后在剪枝的时候不能小于此最小值，或者tf-idf要乘以一个系数。

