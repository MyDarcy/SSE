相比muse-extends-3的方案
- 这里的改变是文档向量是TF-IDF的值，而muse-extends-3中文档向量使用
归一化的TF值, 查询向量是归一化的IDF值
- 都使用了冗余关键词, 但是这里因为文档向量都是TF-IDF值, 所以查询向量中都是0,1值
代表包含关系。

综上，后面仍然是采用文档向量是TF值，查询向量是IDF值，当然这还跟FMS-1和FMS-2中
逻辑查询和个性化查询的向量构造方式有关。

更重要的一点是，如果构造某种方式同时支持个性化，逻辑操作等操作，避免
不同的方案的构造方式完全不兼容。