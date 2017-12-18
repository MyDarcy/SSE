package com.darcy.Scheme2017MUSE.base;

import Jama.Matrix;

/*
 * author: darcy
 * date: 2017/12/18 21:58
 * description:
 *
 *  Node u of HAC-tree is a fve-element tuple <VM, PL, PR, FD, sig>, where u.VM
 *  is the pruning vector, u.PL and u.PR, respectively, point to the lef and right
 *  child nodes of u.  u.FD stores the unique identifer of a document, and u.sig stores
 *  a digest of the u.FD document.
*/
public class HACTreeNode {
	// 剪枝向量.
	public Matrix pruningVector;

	// 左子树， 右子树的指针.
	public HACTreeNode left;
	public HACTreeNode right;

	// 文档的唯一标志符
	public String fileDescriptor;

	// 对文档内容签名的值，以用于验证.
	public String signature;

	public HACTreeNode(Matrix pruningVector, HACTreeNode left, HACTreeNode right, String fileDescriptor, String signature) {
		this.pruningVector = pruningVector;
		this.left = left;
		this.right = right;
		this.fileDescriptor = fileDescriptor;
		this.signature = signature;
	}
}

/*
D:\MrDarcy\ForGraduationWorks\Code\SSE\doc\muse\encrypted40\encrypted_00083697263e215e5e7eda753070f08aa374dd45.dat
encrypted_00083697263e215e5e7eda753070f08aa374dd45
 */